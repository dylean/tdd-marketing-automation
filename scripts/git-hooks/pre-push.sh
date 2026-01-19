#!/bin/bash

# Git Pre-Push Hook
# 在 push 前运行代码质量检查

echo "🔍 运行代码质量检查..."

# 运行 Checkstyle 和 SpotBugs
./gradlew checkstyleMain spotbugsMain -x test --no-daemon

# 检查退出码
EXIT_CODE=$?

if [ $EXIT_CODE -ne 0 ]; then
    echo ""
    echo "❌ 代码质量检查失败！"
    echo ""
    echo "请修复以下问题后再 push："
    echo "  1. 查看 Checkstyle 报告: open build/reports/checkstyle/main.html"
    echo "  2. 查看 SpotBugs 报告: open build/reports/spotbugs/main.html"
    echo ""
    echo "💡 如果确定要跳过检查，使用: git push --no-verify"
    echo ""
    exit 1
fi

echo "✅ 代码质量检查通过！"
echo ""

exit 0
