# Git Hooks 使用指南

项目已配置 Git Hooks，在 commit 和 push 时自动进行代码质量检查。

---

## 🎯 配置的 Hooks

### 1. commit-msg Hook
**触发时机**: `git commit`  
**作用**: 校验 commit message 格式

**规范**: [Conventional Commits](https://www.conventionalcommits.org/)

```
<type>(<scope>): <subject>

<body>

<footer>
```

**示例**:
```bash
git commit -m "feat(campaign): add budget management feature"
git commit -m "fix(cache): resolve redis connection timeout"
git commit -m "docs: update README with new API"
git commit -m "test(campaign): add unit tests for validation"
```

**Type 说明**:
| Type | 用途 | 示例 |
|------|------|------|
| `feat` | 新功能 | `feat(api): add new endpoint` |
| `fix` | Bug 修复 | `fix(auth): resolve token expiry` |
| `docs` | 文档更新 | `docs: update installation guide` |
| `style` | 代码格式 | `style: fix indentation` |
| `refactor` | 重构 | `refactor(service): simplify logic` |
| `perf` | 性能优化 | `perf(query): optimize database query` |
| `test` | 测试 | `test(user): add integration tests` |
| `chore` | 构建/工具 | `chore: update dependencies` |
| `build` | 构建系统 | `build: add webpack config` |
| `ci` | CI 配置 | `ci: update GitHub Actions` |
| `revert` | 回退 | `revert: revert commit abc123` |

---

### 2. pre-push Hook
**触发时机**: `git push`  
**作用**: 运行代码质量检查

**检查内容**:
- ✅ Checkstyle 代码风格检查
- ✅ SpotBugs 静态代码分析

**流程**:
```
git push
  ↓
运行 ./gradlew checkstyleMain spotbugsMain
  ↓
检查通过 → push 成功 ✅
检查失败 → push 中止 ❌
```

---

## 🚀 安装 Git Hooks

### 方式 1: 自动安装（推荐）

运行任意 Gradle 命令会自动安装：

```bash
./gradlew build
```

Gradle Git Hooks 插件会自动将 hooks 安装到 `.git/hooks/`。

### 方式 2: 手动安装

```bash
./scripts/install-git-hooks.sh
```

---

## 💡 使用示例

### ✅ 正确的 Commit

```bash
# 1. 修改代码
vim src/main/java/com/tdd/ma/domain/campaign/Campaign.java

# 2. 提交（格式正确）
git add .
git commit -m "feat(campaign): add budget validation logic"

# ✅ Commit message 格式正确
# 提交成功！
```

### ❌ 错误的 Commit

```bash
git commit -m "add new feature"

# ❌ Commit message 格式不正确！
#
# 📋 要求的格式: <type>(<scope>): <subject>
#
# 你的 commit message:
#   add new feature
```

**修正**:
```bash
git commit -m "feat(campaign): add budget management feature"
```

### ✅ 正确的 Push

```bash
# 1. 提交代码
git commit -m "feat(campaign): add budget feature"

# 2. Push（代码质量通过）
git push origin feat/add-budget

# 🔍 运行代码质量检查...
# ✅ 代码质量检查通过！
# Push 成功！
```

### ❌ Push 失败（代码质量问题）

```bash
git push origin feat/add-budget

# 🔍 运行代码质量检查...
# ❌ 代码质量检查失败！
#
# 请修复以下问题后再 push：
#   1. 查看 Checkstyle 报告: open build/reports/checkstyle/main.html
#   2. 查看 SpotBugs 报告: open build/reports/spotbugs/main.html
#
# Push 被阻止！
```

**修复步骤**:
```bash
# 1. 查看报告
open build/reports/checkstyle/main.html

# 2. 修复问题
vim src/main/java/...

# 3. 重新提交
git add .
git commit -m "style: fix code formatting"

# 4. 重新 push
git push origin feat/add-budget
```

---

## 🔓 跳过检查

**场景**: 紧急修复、临时提交

### 跳过 commit-msg 检查

```bash
git commit -m "urgent fix" --no-verify
```

### 跳过 pre-push 检查

```bash
git push --no-verify
```

**⚠️ 警告**: 
- 跳过检查会降低代码质量
- 建议只在紧急情况下使用
- CI 仍然会运行检查

---

## 🛠️ 自定义配置

### 修改 Commit Message 规则

编辑 `scripts/git-hooks/commit-msg.sh`:

```bash
# 修改正则表达式
PATTERN="^(feat|fix|docs)(\(.+\))?: .{1,100}"
```

### 修改 Pre-Push 检查内容

编辑 `scripts/git-hooks/pre-push.sh`:

```bash
# 只运行 Checkstyle
./gradlew checkstyleMain --no-daemon

# 或者运行测试
./gradlew test --no-daemon
```

### 添加新的 Hook

1. 创建脚本:
```bash
vim scripts/git-hooks/pre-commit.sh
chmod +x scripts/git-hooks/pre-commit.sh
```

2. 更新 `build.gradle.kts`:
```kotlin
gitHooks {
    setHooks(
        mapOf(
            "pre-commit" to "scripts/git-hooks/pre-commit.sh",
            "pre-push" to "scripts/git-hooks/pre-push.sh",
            "commit-msg" to "scripts/git-hooks/commit-msg.sh"
        )
    )
}
```

---

## 🐛 故障排查

### Hook 不执行

**原因**: Hooks 未安装或没有执行权限

**解决**:
```bash
# 重新安装
./scripts/install-git-hooks.sh

# 或者运行 Gradle
./gradlew build
```

### Hook 执行报错

**原因**: 脚本路径错误或依赖未满足

**解决**:
```bash
# 检查 hook 是否存在
ls -la .git/hooks/

# 手动执行测试
.git/hooks/pre-push

# 查看错误信息
```

### Gradlew 找不到

**原因**: 从非项目根目录执行 git 命令

**解决**:
```bash
# 方案 1: 在项目根目录执行
cd /path/to/project
git push

# 方案 2: 修改 hook 脚本添加 cd 命令
cd "$(git rev-parse --show-toplevel)"
./gradlew checkstyleMain spotbugsMain
```

---

## 📊 团队协作

### 新成员加入

新成员 clone 项目后，Git Hooks 会自动安装：

```bash
git clone https://github.com/your-org/tdd-marketing-automation.git
cd tdd-marketing-automation
./gradlew build  # 自动安装 hooks
```

### 统一规范

团队所有成员使用相同的 hooks 脚本，确保：
- ✅ Commit message 格式统一
- ✅ 代码质量标准一致
- ✅ 减少 CI 失败

---

## 🎓 最佳实践

### 1. Commit 规范

```bash
# ✅ 好的 commit
git commit -m "feat(campaign): add budget validation with BigDecimal"
git commit -m "fix(cache): resolve redis connection pool exhaustion"
git commit -m "test(budget): add unit tests for negative amount validation"

# ❌ 不好的 commit
git commit -m "update code"
git commit -m "fix bug"
git commit -m "wip"
```

### 2. 提交前检查

```bash
# 本地运行检查
./gradlew checkstyleMain spotbugsMain

# 查看报告
open build/reports/checkstyle/main.html
open build/reports/spotbugs/main.html

# 修复后再提交
git add .
git commit -m "style: fix code formatting issues"
```

### 3. 小步提交

```bash
# ✅ 推荐：多次小提交
git commit -m "feat(campaign): add Budget entity"
git commit -m "feat(campaign): add budget validation logic"
git commit -m "test(campaign): add budget tests"

# ❌ 不推荐：一次大提交
git commit -m "feat(campaign): add complete budget feature"
```

---

## 📚 参考资源

- [Conventional Commits](https://www.conventionalcommits.org/)
- [Git Hooks 文档](https://git-scm.com/book/en/v2/Customizing-Git-Git-Hooks)
- [Angular Commit Message Guidelines](https://github.com/angular/angular/blob/main/CONTRIBUTING.md#commit)

---

**维护者**: DevOps Team  
**最后更新**: 2026-01-19  
**版本**: v1.0
