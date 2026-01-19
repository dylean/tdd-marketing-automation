#!/bin/bash

# ===================================================================
# Sealos Secret 修复脚本
# 用途：修复错误格式的 external-db-secret
# 使用：./scripts/fix-sealos-secret.sh
# ===================================================================

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo "========================================"
echo "    🔧 Sealos Secret 修复"
echo "========================================"
echo ""

# 检测 namespace
NAMESPACE=$(kubectl config view --minify -o jsonpath='{.contexts[0].context.namespace}')
if [ -z "$NAMESPACE" ]; then
  echo -e "${RED}❌ 无法检测到 namespace${NC}"
  exit 1
fi
echo -e "${GREEN}📍 Namespace: $NAMESPACE${NC}"
echo ""

# 检查 Secret 是否存在
if ! kubectl get secret external-db-secret -n $NAMESPACE > /dev/null 2>&1; then
  echo -e "${RED}❌ Secret 不存在${NC}"
  exit 1
fi

# 读取当前 Secret
echo "📋 读取当前 Secret..."
SECRET_DATA=$(kubectl get secret external-db-secret -n $NAMESPACE -o json)

# 检查是否有 mysql-host（正确格式）
if echo "$SECRET_DATA" | jq -e '.data."mysql-host"' > /dev/null 2>&1; then
  MYSQL_HOST=$(echo "$SECRET_DATA" | jq -r '.data."mysql-host"' | base64 -d)
  if [ -n "$MYSQL_HOST" ]; then
    echo -e "${GREEN}✅ Secret 格式正确，无需修复${NC}"
    echo ""
    echo "MySQL 配置:"
    echo "  主机: $MYSQL_HOST"
    echo "  端口: $(echo "$SECRET_DATA" | jq -r '.data."mysql-port"' | base64 -d)"
    echo "  数据库: $(echo "$SECRET_DATA" | jq -r '.data."mysql-database"' | base64 -d)"
    exit 0
  fi
fi

echo -e "${YELLOW}⚠️  检测到 Secret 格式不正确，开始修复...${NC}"
echo ""

# 从 mysql-url 解析信息
if echo "$SECRET_DATA" | jq -e '.data."mysql-url"' > /dev/null 2>&1; then
  MYSQL_URL=$(echo "$SECRET_DATA" | jq -r '.data."mysql-url"' | base64 -d)
  echo "原始 URL: $MYSQL_URL"
  echo ""
  
  # 解析 URL
  # 移除 jdbc:mysql:// 前缀
  MYSQL_URL_CLEAN=$(echo "$MYSQL_URL" | sed 's|jdbc:mysql://||')
  
  # 移除可能存在的 user:password@ 部分
  if [[ "$MYSQL_URL_CLEAN" == *"@"* ]]; then
    MYSQL_URL_CLEAN=$(echo "$MYSQL_URL_CLEAN" | sed 's/^[^@]*@//')
  fi
  
  # 提取 host, port, database
  MYSQL_HOST=$(echo "$MYSQL_URL_CLEAN" | sed -n 's/^\([^:]*\):.*/\1/p')
  MYSQL_PORT=$(echo "$MYSQL_URL_CLEAN" | sed -n 's/^[^:]*:\([0-9]*\)\/.*/\1/p')
  MYSQL_DB=$(echo "$MYSQL_URL_CLEAN" | sed -n 's/^[^/]*\/\([^?]*\).*/\1/p')
else
  echo -e "${RED}❌ 无法从 Secret 中读取 mysql-url${NC}"
  exit 1
fi

# 读取其他配置
MYSQL_USER=$(echo "$SECRET_DATA" | jq -r '.data."mysql-username"' | base64 -d)
MYSQL_PASS=$(echo "$SECRET_DATA" | jq -r '.data."mysql-password"' | base64 -d)
REDIS_HOST=$(echo "$SECRET_DATA" | jq -r '.data."redis-host"' | base64 -d)
REDIS_PORT=$(echo "$SECRET_DATA" | jq -r '.data."redis-port"' | base64 -d)
REDIS_PASS=$(echo "$SECRET_DATA" | jq -r '.data."redis-password"' | base64 -d)

echo "解析后的配置:"
echo "  MySQL 主机: $MYSQL_HOST"
echo "  MySQL 端口: $MYSQL_PORT"
echo "  MySQL 数据库: $MYSQL_DB"
echo "  MySQL 用户名: $MYSQL_USER"
echo "  MySQL 密码: $MYSQL_PASS"
echo "  Redis 主机: $REDIS_HOST"
echo "  Redis 端口: $REDIS_PORT"
echo "  Redis 密码: $REDIS_PASS"
echo ""

# 验证必需字段
if [ -z "$MYSQL_HOST" ] || [ -z "$MYSQL_PORT" ] || [ -z "$MYSQL_DB" ]; then
  echo -e "${RED}❌ URL 解析失败，请手动配置${NC}"
  echo ""
  echo "请运行以下命令手动创建 Secret："
  echo ""
  echo "kubectl delete secret external-db-secret -n $NAMESPACE"
  echo ""
  echo "kubectl create secret generic external-db-secret -n $NAMESPACE \\"
  echo "  --from-literal=mysql-host=\"<your-mysql-host>\" \\"
  echo "  --from-literal=mysql-port=\"3306\" \\"
  echo "  --from-literal=mysql-database=\"<your-database>\" \\"
  echo "  --from-literal=mysql-username=\"$MYSQL_USER\" \\"
  echo "  --from-literal=mysql-password=\"$MYSQL_PASS\" \\"
  echo "  --from-literal=redis-host=\"$REDIS_HOST\" \\"
  echo "  --from-literal=redis-port=\"$REDIS_PORT\" \\"
  echo "  --from-literal=redis-password=\"$REDIS_PASS\""
  exit 1
fi

# 确认操作
echo -e "${YELLOW}准备重新创建 Secret，继续吗？(y/n)${NC}"
read -r CONFIRM
if [ "$CONFIRM" != "y" ] && [ "$CONFIRM" != "Y" ]; then
  echo "已取消"
  exit 0
fi

echo ""
echo "🗑️  删除旧 Secret..."
kubectl delete secret external-db-secret -n $NAMESPACE

echo ""
echo "📦 创建新 Secret（正确格式）..."
kubectl create secret generic external-db-secret -n $NAMESPACE \
  --from-literal=mysql-host="$MYSQL_HOST" \
  --from-literal=mysql-port="$MYSQL_PORT" \
  --from-literal=mysql-database="$MYSQL_DB" \
  --from-literal=mysql-username="$MYSQL_USER" \
  --from-literal=mysql-password="$MYSQL_PASS" \
  --from-literal=redis-host="$REDIS_HOST" \
  --from-literal=redis-port="$REDIS_PORT" \
  --from-literal=redis-password="$REDIS_PASS"

echo ""
echo -e "${GREEN}✅ Secret 修复完成！${NC}"
echo ""

# 验证新 Secret
echo "验证新 Secret..."
./scripts/verify-sealos-config.sh

echo ""
echo "========================================"
echo "    🚀 下一步操作"
echo "========================================"
echo ""
echo "1️⃣  重启应用以应用新配置:"
echo "   kubectl rollout restart deployment/tdd-marketing-automation -n $NAMESPACE"
echo ""
echo "2️⃣  查看应用日志:"
echo "   kubectl logs -f deployment/tdd-marketing-automation -n $NAMESPACE"
echo ""
echo "3️⃣  更新 GitHub Secrets (避免下次被覆盖):"
echo "   EXTERNAL_MYSQL_URL 应该是:"
echo "   jdbc:mysql://$MYSQL_HOST:$MYSQL_PORT/$MYSQL_DB?useSSL=false&serverTimezone=Asia/Shanghai"
echo "   (不要包含用户名和密码)"
echo ""
