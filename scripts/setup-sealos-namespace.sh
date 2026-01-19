#!/bin/bash
# 自动检测并配置 Sealos Namespace

set -e

echo "🔍 检测 Sealos Namespace..."

# 从 kubeconfig 中获取当前 namespace
SEALOS_NS=$(kubectl config view --minify -o jsonpath='{.contexts[0].context.namespace}')

if [ -z "$SEALOS_NS" ]; then
    echo "❌ 错误: 无法从 kubeconfig 获取 namespace"
    echo "请确保已正确配置 kubeconfig"
    exit 1
fi

echo "✅ 检测到 Sealos Namespace: $SEALOS_NS"
echo ""

# 备份配置文件
echo "📦 备份配置文件..."
BACKUP_DIR=".backup-$(date +%Y%m%d-%H%M%S)"
mkdir -p "$BACKUP_DIR"
cp -r sealos/*.yaml "$BACKUP_DIR/" 2>/dev/null || true
cp .github/workflows/ci-cd.yml "$BACKUP_DIR/" 2>/dev/null || true
echo "✅ 备份完成: $BACKUP_DIR"
echo ""

# 替换 Sealos 配置文件中的 namespace
echo "🔧 更新 Sealos 配置文件..."
for file in sealos/*.yaml; do
    if [ -f "$file" ]; then
        echo "  - 处理 $file"
        # 替换 namespace: tdd-ma
        sed -i '' "s/namespace: tdd-ma/namespace: $SEALOS_NS/g" "$file"
        # 替换 -n tdd-ma
        sed -i '' "s/-n tdd-ma/-n $SEALOS_NS/g" "$file"
        # 删除 Namespace 定义（不需要创建）
        sed -i '' '/^---$/,/^---$/{ /kind: Namespace/,/^---$/d; }' "$file"
    fi
done
echo "✅ Sealos 配置文件更新完成"
echo ""

# 替换 CI/CD 配置中的 namespace
echo "🔧 更新 CI/CD 配置..."
sed -i '' "s/tdd-ma/$SEALOS_NS/g" .github/workflows/ci-cd.yml
echo "✅ CI/CD 配置更新完成"
echo ""

# 显示变更
echo "📋 变更摘要:"
echo "----------------------------------------"
git diff --stat
echo "----------------------------------------"
echo ""

# 验证配置
echo "🧪 验证配置..."
echo "  检查 namespace 是否可访问..."
if kubectl get pods -n "$SEALOS_NS" > /dev/null 2>&1; then
    echo "  ✅ Namespace $SEALOS_NS 可访问"
else
    echo "  ⚠️  无法访问 namespace $SEALOS_NS (可能还没有资源)"
fi
echo ""

echo "🎉 配置完成！"
echo ""
echo "📌 下一步:"
echo "1. 查看变更: git diff"
echo "2. 提交变更: git add -A && git commit -m \"fix: use Sealos namespace $SEALOS_NS\""
echo "3. 推送部署: git push origin main"
echo ""
echo "或者直接运行:"
echo "  git add -A && git commit -m \"fix: use Sealos namespace $SEALOS_NS\" && git push origin main"
