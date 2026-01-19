#!/bin/bash

# Git Commit-Msg Hook
# 校验 commit message 是否符合 Conventional Commits 规范

COMMIT_MSG_FILE=$1
COMMIT_MSG=$(cat "$COMMIT_MSG_FILE")

# 正则表达式：匹配 Conventional Commits 格式
# 格式: <type>(<scope>): <subject>
# 或者: <type>: <subject>
PATTERN="^(feat|fix|docs|style|refactor|perf|test|chore|build|ci|revert)(\(.+\))?: .{1,100}"

if ! echo "$COMMIT_MSG" | grep -qE "$PATTERN"; then
    echo ""
    echo "❌ Commit message 格式不正确！"
    echo ""
    echo "📋 要求的格式: <type>(<scope>): <subject>"
    echo ""
    echo "✅ 有效的 types:"
    echo "  - feat:     新功能"
    echo "  - fix:      Bug 修复"
    echo "  - docs:     文档更新"
    echo "  - style:    代码格式调整（不影响功能）"
    echo "  - refactor: 代码重构"
    echo "  - perf:     性能优化"
    echo "  - test:     测试相关"
    echo "  - chore:    构建/工具变更"
    echo "  - build:    构建系统变更"
    echo "  - ci:       CI 配置变更"
    echo "  - revert:   回退提交"
    echo ""
    echo "📝 示例:"
    echo "  feat(campaign): add budget management feature"
    echo "  fix(cache): resolve redis connection timeout"
    echo "  docs: update README"
    echo "  test(campaign): add unit tests for budget validation"
    echo ""
    echo "你的 commit message:"
    echo "  $COMMIT_MSG"
    echo ""
    echo "💡 如果确定要跳过检查，使用: git commit --no-verify"
    echo ""
    exit 1
fi

# 检查 subject 长度（不超过 100 字符）
SUBJECT=$(echo "$COMMIT_MSG" | head -n 1)
SUBJECT_LENGTH=${#SUBJECT}

if [ $SUBJECT_LENGTH -gt 100 ]; then
    echo ""
    echo "⚠️  警告: Commit message 第一行过长 ($SUBJECT_LENGTH 字符)"
    echo "   建议不超过 100 字符"
    echo ""
fi

echo "✅ Commit message 格式正确"
exit 0
