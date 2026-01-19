#!/bin/bash

# ===================================================================
# Sealos 配置验证脚本
# 用途：验证 Secret 配置是否正确
# 使用：./scripts/verify-sealos-config.sh
# ===================================================================

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo "========================================"
echo "    🔍 Sealos 配置验证"
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
echo -e "${BLUE}1. 检查 Secret 是否存在${NC}"
if kubectl get secret external-db-secret -n $NAMESPACE > /dev/null 2>&1; then
  echo -e "${GREEN}✅ external-db-secret 存在${NC}"
else
  echo -e "${RED}❌ external-db-secret 不存在${NC}"
  echo "   请先创建 Secret"
  exit 1
fi
echo ""

# 显示 Secret 的 keys（不显示值）
echo -e "${BLUE}2. Secret 包含的配置项${NC}"
kubectl get secret external-db-secret -n $NAMESPACE -o jsonpath='{.data}' | jq -r 'keys[]' 2>/dev/null || kubectl get secret external-db-secret -n $NAMESPACE -o json | grep -o '"[^"]*":' | tr -d '":' | grep -v "^metadata$\|^data$\|^type$"
echo ""

# 解码并显示配置（明文显示）
echo -e "${BLUE}3. 当前配置值（明文）${NC}"
echo ""

MYSQL_HOST=$(kubectl get secret external-db-secret -n $NAMESPACE -o jsonpath='{.data.mysql-host}' | base64 -d)
MYSQL_PORT=$(kubectl get secret external-db-secret -n $NAMESPACE -o jsonpath='{.data.mysql-port}' | base64 -d)
MYSQL_DB=$(kubectl get secret external-db-secret -n $NAMESPACE -o jsonpath='{.data.mysql-database}' | base64 -d)
MYSQL_USER=$(kubectl get secret external-db-secret -n $NAMESPACE -o jsonpath='{.data.mysql-username}' | base64 -d)
MYSQL_PASS=$(kubectl get secret external-db-secret -n $NAMESPACE -o jsonpath='{.data.mysql-password}' | base64 -d)

REDIS_HOST=$(kubectl get secret external-db-secret -n $NAMESPACE -o jsonpath='{.data.redis-host}' | base64 -d)
REDIS_PORT=$(kubectl get secret external-db-secret -n $NAMESPACE -o jsonpath='{.data.redis-port}' | base64 -d)
REDIS_PASS=$(kubectl get secret external-db-secret -n $NAMESPACE -o jsonpath='{.data.redis-password}' | base64 -d)

echo "📊 MySQL 配置:"
echo "   主机: $MYSQL_HOST"
echo "   端口: $MYSQL_PORT"
echo "   数据库: $MYSQL_DB"
echo "   用户名: $MYSQL_USER"
echo "   密码: $MYSQL_PASS"
echo ""

echo "📊 Redis 配置:"
echo "   主机: $REDIS_HOST"
echo "   端口: $REDIS_PORT"
echo "   密码: $REDIS_PASS"
echo ""

# 检查 Pod 环境变量
echo -e "${BLUE}4. Pod 中的环境变量${NC}"
POD_NAME=$(kubectl get pods -l app=tdd-ma -n $NAMESPACE -o jsonpath='{.items[0].metadata.name}' 2>/dev/null || echo "")
if [ -n "$POD_NAME" ]; then
  echo "📦 Pod: $POD_NAME"
  echo ""
  echo "环境变量（数据库相关）:"
  kubectl exec $POD_NAME -n $NAMESPACE -- env 2>/dev/null | grep -E "DB_|REDIS_|MYSQL_" || echo "   未找到相关环境变量"
else
  echo -e "${YELLOW}⚠️  没有运行中的 Pod${NC}"
fi
echo ""

# MySQL 连接测试建议
echo "========================================"
echo "    💡 MySQL 连接测试"
echo "========================================"
echo ""
echo "请在 MySQL 服务器上执行以下命令验证用户权限："
echo ""
echo -e "${YELLOW}-- 1. 检查用户是否存在${NC}"
echo "SELECT user, host FROM mysql.user WHERE user='$MYSQL_USER';"
echo ""
echo -e "${YELLOW}-- 2. 检查用户权限${NC}"
echo "SHOW GRANTS FOR '$MYSQL_USER'@'%';"
echo ""
echo -e "${YELLOW}-- 3. 如果用户不存在或权限不足，创建/授权用户${NC}"
echo "CREATE USER IF NOT EXISTS '$MYSQL_USER'@'%' IDENTIFIED BY '$MYSQL_PASS';"
echo "GRANT ALL PRIVILEGES ON $MYSQL_DB.* TO '$MYSQL_USER'@'%';"
echo "FLUSH PRIVILEGES;"
echo ""
echo -e "${YELLOW}-- 4. 验证连接（从本地）${NC}"
echo "mysql -h $MYSQL_HOST -P $MYSQL_PORT -u $MYSQL_USER -p'$MYSQL_PASS' $MYSQL_DB"
echo ""
echo -e "${YELLOW}-- 5. 或者手动输入密码${NC}"
echo "mysql -h $MYSQL_HOST -P $MYSQL_PORT -u $MYSQL_USER -p $MYSQL_DB"
echo "# 然后输入密码: $MYSQL_PASS"
echo ""

# 常见问题诊断
echo "========================================"
echo "    🔧 常见问题诊断"
echo "========================================"
echo ""

echo "❌ 错误: Access denied for user '***'@'10.108.11.235'"
echo ""
echo "可能原因："
echo "  1️⃣  密码错误"
echo "     - 检查 GitHub Secrets 中的 EXTERNAL_MYSQL_PASSWORD 是否正确"
echo "     - 确认密码没有特殊字符导致的转义问题"
echo ""
echo "  2️⃣  用户不存在或 host 不匹配"
echo "     - MySQL 用户的 host 必须是 '%' 或包含 Pod IP"
echo "     - 运行上面的 SQL 检查用户和权限"
echo ""
echo "  3️⃣  权限不足"
echo "     - 用户需要对数据库 $MYSQL_DB 有完整权限"
echo "     - 运行 GRANT ALL PRIVILEGES 授权"
echo ""
echo "  4️⃣  数据库不存在"
echo "     - 确保数据库 $MYSQL_DB 已创建"
echo "     - CREATE DATABASE IF NOT EXISTS $MYSQL_DB;"
echo ""

echo "📋 修复步骤："
echo ""
echo "步骤 1: 验证 MySQL 配置"
echo "  运行上面的 SQL 命令检查用户和权限"
echo ""
echo "步骤 2: 更新 Secret（如果密码错误）"
echo "  kubectl delete secret external-db-secret -n $NAMESPACE"
echo "  # 然后重新运行 CI/CD 或手动创建 Secret"
echo ""
echo "步骤 3: 重启应用"
echo "  kubectl rollout restart deployment/tdd-marketing-automation -n $NAMESPACE"
echo ""
echo "步骤 4: 查看日志验证"
echo "  kubectl logs -f deployment/tdd-marketing-automation -n $NAMESPACE"
echo ""
