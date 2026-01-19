# GitHub CI/CD 快速入门 (5分钟)

## ✅ 1. 推送到 GitHub

```bash
git add .
git commit -m "feat: setup GitHub CI/CD"
git push origin main
```

## ✅ 2. 查看 Actions

访问: `https://github.com/你的用户名/tdd-marketing-automation/actions`

你会看到 CI 工作流自动运行 🚀

## ✅ 3. （可选）配置 Docker 部署

### 3.1 创建 Docker Hub Token

1. 访问 https://hub.docker.com
2. Account Settings → Security → New Access Token
3. 复制 Token

### 3.2 配置 GitHub Secrets

1. 前往仓库 `Settings` → `Secrets and variables` → `Actions`
2. 点击 `New repository secret`
3. 添加以下 Secrets:

| Name | Value |
|------|-------|
| `DOCKER_USERNAME` | 你的 Docker Hub 用户名 |
| `DOCKER_PASSWORD` | 刚才生成的 Token |

## ✅ 4. 测试 PR 流程

```bash
# 1. 创建分支
git checkout -b feat/test-ci

# 2. 修改代码（例如添加注释）
echo "// Test CI" >> src/main/java/com/tdd/ma/MarketingAutomationApplication.java

# 3. 提交并推送
git add .
git commit -m "feat: test CI workflow"
git push origin feat/test-ci

# 4. 在 GitHub 创建 Pull Request
```

PR 会自动触发检查 ✨

## ✅ 5. 发布到生产（可选）

```bash
# 1. 合并 PR 到 main
# 2. 创建版本标签
git checkout main
git pull
git tag -a v1.0.0 -m "Release v1.0.0"
git push origin v1.0.0
```

Deploy 工作流会自动触发 🎉

---

## 🎓 下一步

- 📖 阅读 [完整配置指南](../docs/CI-CD-SETUP.md)
- 🔧 自定义工作流: [工作流说明](.github/workflows/README.md)
- 🐳 Docker 部署: [Dockerfile](../Dockerfile)

---

**提示**: 所有工作流都已配置好，无需额外修改即可使用！
