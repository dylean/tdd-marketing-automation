#!/bin/bash

# ===================================================================
# Sealos 部署诊断脚本
# 用途：快速诊断 Sealos 部署问题
# 使用：./scripts/diagnose-sealos-deployment.sh [namespace]
# ===================================================================

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 检测 namespace
if [ -z "$1" ]; then
  NAMESPACE=$(kubectl config view --minify -o jsonpath='{.contexts[0].context.namespace}')
  if [ -z "$NAMESPACE" ]; then
    echo -e "${RED}❌ 无法自动检测 namespace，请手动指定：${NC}"
    echo "   使用方法: $0 <namespace>"
    exit 1
  fi
  echo -e "${GREEN}🔍 自动检测到 namespace: $NAMESPACE${NC}"
else
  NAMESPACE=$1
  echo -e "${GREEN}🔍 使用指定的 namespace: $NAMESPACE${NC}"
fi

echo ""
echo "========================================"
echo "    Sealos 部署诊断 - $NAMESPACE"
echo "========================================"
echo ""

# 1. Namespace 状态
echo -e "${BLUE}📦 1. Namespace 状态${NC}"
kubectl get namespace $NAMESPACE || {
  echo -e "${RED}❌ Namespace 不存在或无权限访问${NC}"
  exit 1
}
echo ""

# 2. Deployment 状态
echo -e "${BLUE}🚀 2. Deployment 状态${NC}"
if kubectl get deployment tdd-marketing-automation -n $NAMESPACE > /dev/null 2>&1; then
  kubectl get deployment tdd-marketing-automation -n $NAMESPACE -o wide
  echo ""
  echo "📊 Deployment 详情:"
  kubectl describe deployment tdd-marketing-automation -n $NAMESPACE | tail -20
else
  echo -e "${YELLOW}⚠️  Deployment 不存在${NC}"
fi
echo ""

# 3. Pod 状态
echo -e "${BLUE}📦 3. Pod 状态${NC}"
kubectl get pods -l app=tdd-ma -n $NAMESPACE -o wide
echo ""

# 检查是否有 Pod
POD_COUNT=$(kubectl get pods -l app=tdd-ma -n $NAMESPACE --no-headers 2>/dev/null | wc -l)
if [ "$POD_COUNT" -eq 0 ]; then
  echo -e "${YELLOW}⚠️  没有找到 Pod${NC}"
else
  # 4. Pod 详细状态
  echo -e "${BLUE}🔍 4. Pod 详细状态（最新的 Pod）${NC}"
  LATEST_POD=$(kubectl get pods -l app=tdd-ma -n $NAMESPACE --sort-by=.metadata.creationTimestamp -o jsonpath='{.items[-1].metadata.name}' 2>/dev/null)
  if [ -n "$LATEST_POD" ]; then
    kubectl describe pod $LATEST_POD -n $NAMESPACE
  fi
  echo ""
  
  # 5. Pod 日志
  echo -e "${BLUE}📜 5. Pod 日志（最近100行）${NC}"
  for pod in $(kubectl get pods -l app=tdd-ma -n $NAMESPACE -o jsonpath='{.items[*].metadata.name}'); do
    echo "--- Pod: $pod ---"
    POD_STATUS=$(kubectl get pod $pod -n $NAMESPACE -o jsonpath='{.status.phase}')
    echo "状态: $POD_STATUS"
    
    if [ "$POD_STATUS" = "Running" ] || [ "$POD_STATUS" = "Failed" ] || [ "$POD_STATUS" = "CrashLoopBackOff" ]; then
      kubectl logs $pod -n $NAMESPACE --tail=100 --all-containers=true 2>&1 || echo "无法获取日志"
    else
      echo "Pod 尚未启动，跳过日志"
    fi
    echo ""
  done
fi

# 6. Service 状态
echo -e "${BLUE}🌐 6. Service 状态${NC}"
kubectl get service tdd-ma-service -n $NAMESPACE -o wide 2>/dev/null || echo -e "${YELLOW}⚠️  Service 不存在${NC}"
echo ""

# 7. Ingress 状态
echo -e "${BLUE}🔗 7. Ingress 状态${NC}"
if kubectl get ingress tdd-ma-ingress -n $NAMESPACE > /dev/null 2>&1; then
  kubectl get ingress tdd-ma-ingress -n $NAMESPACE
  INGRESS_HOST=$(kubectl get ingress tdd-ma-ingress -n $NAMESPACE -o jsonpath='{.spec.rules[0].host}')
  echo ""
  echo -e "${GREEN}📍 访问地址: https://${INGRESS_HOST}${NC}"
else
  echo -e "${YELLOW}⚠️  Ingress 不存在${NC}"
fi
echo ""

# 8. HPA 状态
echo -e "${BLUE}📊 8. HPA 状态${NC}"
kubectl get hpa tdd-ma-hpa -n $NAMESPACE 2>/dev/null || echo -e "${YELLOW}⚠️  HPA 不存在${NC}"
echo ""

# 9. Secret 状态
echo -e "${BLUE}🔐 9. Secret 状态${NC}"
if kubectl get secret external-db-secret -n $NAMESPACE > /dev/null 2>&1; then
  echo -e "${GREEN}✅ external-db-secret 存在${NC}"
  kubectl get secret external-db-secret -n $NAMESPACE
else
  echo -e "${RED}❌ external-db-secret 不存在${NC}"
fi
echo ""

# 10. 最近事件
echo -e "${BLUE}⚠️  10. 最近事件（最新20条）${NC}"
kubectl get events -n $NAMESPACE --sort-by='.lastTimestamp' | tail -20
echo ""

# 11. 资源配额
echo -e "${BLUE}📈 11. 资源配额和使用情况${NC}"
kubectl describe namespace $NAMESPACE | grep -A 10 "Resource Quotas" || echo "无资源配额限制"
echo ""

# 常见问题诊断
echo "========================================"
echo "    💡 常见问题诊断"
echo "========================================"
echo ""

# 检查 Pod 状态
if [ "$POD_COUNT" -eq 0 ]; then
  echo -e "${RED}❌ 没有 Pod 运行${NC}"
  echo "   可能原因："
  echo "   - Deployment 未创建"
  echo "   - Selector 不匹配"
  echo "   - 资源配额不足"
else
  # 检查 Pod 是否健康
  RUNNING_PODS=$(kubectl get pods -l app=tdd-ma -n $NAMESPACE --field-selector=status.phase=Running --no-headers 2>/dev/null | wc -l)
  if [ "$RUNNING_PODS" -eq 0 ]; then
    echo -e "${RED}❌ 没有 Pod 处于 Running 状态${NC}"
    echo "   可能原因："
    
    # 检查镜像拉取失败
    if kubectl get pods -l app=tdd-ma -n $NAMESPACE -o jsonpath='{.items[*].status.containerStatuses[*].state.waiting.reason}' | grep -q "ImagePullBackOff"; then
      echo -e "   ${YELLOW}📦 镜像拉取失败${NC}"
      echo "      - 检查 Docker Hub 镜像是否存在"
      echo "      - 检查镜像名称是否正确"
      echo "      - 检查 imagePullSecrets 配置"
    fi
    
    # 检查 CrashLoopBackOff
    if kubectl get pods -l app=tdd-ma -n $NAMESPACE -o jsonpath='{.items[*].status.containerStatuses[*].state.waiting.reason}' | grep -q "CrashLoopBackOff"; then
      echo -e "   ${YELLOW}💥 应用启动失败（CrashLoopBackOff）${NC}"
      echo "      - 检查应用日志（上面的日志部分）"
      echo "      - 检查环境变量配置"
      echo "      - 检查数据库连接"
    fi
    
    # 检查健康检查失败
    if kubectl describe pods -l app=tdd-ma -n $NAMESPACE | grep -q "Readiness probe failed"; then
      echo -e "   ${YELLOW}🏥 健康检查失败${NC}"
      echo "      - 检查健康检查端点是否正常"
      echo "      - 检查应用是否完全启动"
      echo "      - 考虑增加 initialDelaySeconds"
    fi
  else
    echo -e "${GREEN}✅ 有 $RUNNING_PODS 个 Pod 正在运行${NC}"
  fi
fi

echo ""
echo "========================================"
echo "    🔧 快速操作命令"
echo "========================================"
echo ""
echo "📋 查看实时日志:"
echo "   kubectl logs -f deployment/tdd-marketing-automation -n $NAMESPACE"
echo ""
echo "🔄 重启 Deployment:"
echo "   kubectl rollout restart deployment/tdd-marketing-automation -n $NAMESPACE"
echo ""
echo "↩️  回滚到上一版本:"
echo "   kubectl rollout undo deployment/tdd-marketing-automation -n $NAMESPACE"
echo ""
echo "🗑️  删除 Pod（自动重建）:"
echo "   kubectl delete pod -l app=tdd-ma -n $NAMESPACE"
echo ""
echo "🔍 进入容器调试:"
echo "   kubectl exec -it deployment/tdd-marketing-automation -n $NAMESPACE -- /bin/sh"
echo ""
