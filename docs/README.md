# 📚 项目文档索引

欢迎来到 TDD 营销自动化项目文档中心！

---

## 🚀 快速开始

| 文档 | 说明 |
|------|------|
| [CI/CD 快速开始](CI_CD_QUICK_START.md) | 5 分钟配置 GitHub Actions |
| [Docker 快速开始](DOCKER_QUICK_START.md) | Docker Hub 配置和部署 |
| [本地开发指南](LOCAL_DEVELOPMENT.md) | 使用 Docker Compose 本地开发 |

---

## 🔧 CI/CD 和部署

| 文档 | 说明 |
|------|------|
| [CI/CD 完整配置](CI-CD-SETUP.md) | GitHub Actions 完整配置指南 |
| [CI/CD 工作流详解](CI_CD_WORKFLOWS.md) | 工作流程和故障排查 |
| [Docker 部署指南](DOCKER_DEPLOYMENT.md) | Docker 和 Docker Compose 部署 |
| [Sealos 部署指南](SEALOS_DEPLOYMENT.md) | Kubernetes/Sealos 云部署 |

---

## ☁️ Sealos 部署

| 文档 | 说明 |
|------|------|
| [Sealos 配置文件说明](sealos/CONFIGURATION.md) | 配置文件概览 |
| [Sealos 外部数据库配置](sealos/EXTERNAL_DB_SETUP.md) | 连接外部 MySQL/Redis |
| [Sealos 快速部署](sealos/QUICK_START_EXTERNAL_DB.md) | 5 分钟快速部署 |
| [Sealos Namespace 配置](SEALOS_NAMESPACE_SETUP.md) | Namespace 权限和配置 |

---

## 🔐 配置和密钥

| 文档 | 说明 |
|------|------|
| [GitHub Secrets 配置](GITHUB_SECRETS_SETUP.md) | 获取和配置所有必需的 Secrets |
| [环境变量流程图](ENV_VARIABLES_FLOW.md) | 环境变量从 GitHub 到应用的完整流程 |

---

## 🧪 代码质量和测试

| 文档 | 说明 |
|------|------|
| [代码质量工具](CODE_QUALITY.md) | Checkstyle 和 SpotBugs 使用指南 |
| [Checkstyle 配置](CHECKSTYLE.md) | 代码风格检查详细说明 |
| [SpotBugs 配置](SPOTBUGS.md) | 静态代码分析详细说明 |
| [Git Hooks](GIT_HOOKS.md) | 本地代码质量检查和提交规范 |

---

## 📖 Story Cards（案例故事卡）

| 文档 | 说明 |
|------|------|
| [01. 手动 TDD - 活动预算管理](story-cards/01-manual-tdd-campaign-budget.md) | 手动 TDD 演示案例 |
| [02. AI TDD - 活动数据分析](story-cards/02-ai-tdd-campaign-analytics.md) | AI 辅助 TDD 演示案例 |
| [Story Cards 使用指南](story-cards/README.md) | 故事卡使用说明 |

---

## 🎓 演示文稿（Slidev）

| 文档 | 说明 |
|------|------|
| [Slidev 部署指南](slides/DEPLOY_GUIDE.md) | 部署到 Vercel |
| [Slidev README](slides/README.md) | 本地运行和开发 |

---

## 📋 架构和设计

- **DDD 分层架构**: Domain、Application、Infrastructure、Interfaces
- **测试策略**: 单元测试、集成测试、架构守护测试
- **外部服务集成**: Redis 缓存、FeignClient、外部 MySQL

---

## 🛠️ 技术栈

- **后端**: Spring Boot 3.2, Java 21
- **数据库**: MySQL 8.0, Redis
- **构建工具**: Gradle 8.x (Kotlin DSL)
- **CI/CD**: GitHub Actions
- **容器化**: Docker, Docker Compose
- **云平台**: Sealos (Kubernetes)
- **代码质量**: Checkstyle, SpotBugs, ArchUnit

---

## 🤝 贡献指南

1. Fork 项目
2. 创建特性分支: `git checkout -b feat/amazing-feature`
3. 提交更改: `git commit -m "feat: add amazing feature"`
4. 推送分支: `git push origin feat/amazing-feature`
5. 创建 Pull Request

提交信息请遵循 [Conventional Commits](https://www.conventionalcommits.org/) 规范。

---

## 📞 获取帮助

- **问题反馈**: 在 GitHub Issues 中提交
- **文档问题**: 查看对应的 README 文件
- **本地调试**: 参考 [本地开发指南](LOCAL_DEVELOPMENT.md)

---

**最后更新**: 2026-01-20  
**维护者**: DevOps Team
