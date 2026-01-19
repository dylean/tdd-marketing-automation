#!/bin/bash

# 安装 Git Hooks 脚本
# 用于手动安装（Gradle 插件会自动安装）

echo "📦 安装 Git Hooks..."

HOOKS_DIR=".git/hooks"
SCRIPTS_DIR="scripts/git-hooks"

# 确保 hooks 目录存在
mkdir -p "$HOOKS_DIR"

# 安装 pre-push hook
if [ -f "$SCRIPTS_DIR/pre-push.sh" ]; then
    cp "$SCRIPTS_DIR/pre-push.sh" "$HOOKS_DIR/pre-push"
    chmod +x "$HOOKS_DIR/pre-push"
    echo "✅ 已安装 pre-push hook"
fi

# 安装 commit-msg hook
if [ -f "$SCRIPTS_DIR/commit-msg.sh" ]; then
    cp "$SCRIPTS_DIR/commit-msg.sh" "$HOOKS_DIR/commit-msg"
    chmod +x "$HOOKS_DIR/commit-msg"
    echo "✅ 已安装 commit-msg hook"
fi

echo ""
echo "🎉 Git Hooks 安装完成！"
echo ""
echo "说明："
echo "  - git commit: 自动校验 commit message 格式"
echo "  - git push: 自动运行 checkstyle 和 spotbugs"
echo ""
echo "跳过检查："
echo "  - git commit --no-verify"
echo "  - git push --no-verify"
echo ""
