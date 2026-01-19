# 🧪 营销活动自动化平台 (TDD Marketing Automation)

> TDD 实战训练项目 - 使用测试驱动开发构建营销自动化系统

[![CI/CD Pipeline](https://github.com/dylean/tdd-marketing-automation/actions/workflows/ci-cd.yml/badge.svg)](https://github.com/dylean/tdd-marketing-automation/actions/workflows/ci-cd.yml)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

---

## 📋 目录

- [项目架构](#-项目架构)
- [技术栈](#-技术栈)
- [快速开始](#-快速开始)
- [部署指南](#-部署指南)
- [TDD 实践指南](#-tdd-实践指南)
- [培训资料](#-培训资料)

---

## 🏗️ 项目架构

本项目采用 **DDD 分层架构** + **TDD 开发模式**：

```
src/main/java/com/tdd/ma/
├── interfaces/          # 接口层 - Controller, DTO
│   └── rest/           # REST API 端点
├── application/         # 应用层 - Application Service, Command, Query
│   └── campaign/       # 营销活动应用服务
├── domain/              # 领域层 - Entity, Value Object, Repository Interface
│   ├── campaign/       # 营销活动聚合
│   ├── audience/       # 受众人群聚合
│   └── common/         # 公共领域模型
└── infrastructure/      # 基础设施层 - Repository 实现, 外部服务集成
    ├── persistence/    # MyBatis 持久化
    ├── cache/          # Redis 缓存
    ├── external/       # FeignClient 外部服务
    └── config/         # 配置类
```

### 依赖规则

```
┌─────────────────────────────────────────────────────┐
│                    Interfaces                        │
│              (Controller, DTO)                       │
└─────────────────────┬───────────────────────────────┘
                      │ 依赖
                      ▼
┌─────────────────────────────────────────────────────┐
│                   Application                        │
│           (Application Service)                      │
└─────────────────────┬───────────────────────────────┘
                      │ 依赖
                      ▼
┌─────────────────────────────────────────────────────┐
│                     Domain                           │
│     (Entity, Value Object, Repository Interface)     │
└─────────────────────────────────────────────────────┘
                      ▲
                      │ 实现接口
┌─────────────────────┴───────────────────────────────┐
│                  Infrastructure                      │
│    (Repository Impl, Cache, External Service)       │
└─────────────────────────────────────────────────────┘
```

由 **ArchUnit** 架构测试自动守护 ✅

---

## 🛠️ 技术栈

### 核心框架

| 技术 | 版本 | 说明 |
|------|------|------|
| **Java** | 21 | LTS 长期支持版本 |
| **Spring Boot** | 3.2.0 | 应用框架 |
| **MyBatis Plus** | 3.5.5 | ORM 框架 |
| **Gradle** | 8.5 | 构建工具（Kotlin DSL） |

### 数据存储

| 技术 | 说明 |
|------|------|
| **MySQL** | 8.0+ 关系型数据库 |
| **Redis** | 7.0+ 缓存和会话存储 |
| **Flyway** | 数据库版本管理 |

### 外部服务

| 技术 | 说明 |
|------|------|
| **OpenFeign** | 声明式 HTTP 客户端 |
| **Audience Service** | 外部受众人群服务 |

### 测试工具

| 技术 | 说明 |
|------|------|
| **JUnit 5** | 单元测试框架 |
| **Mockito** | Mock 框架 |
| **ArchUnit** | 架构守护测试 |
| **Spring Boot Test** | 集成测试 |

### 代码质量

| 工具 | 说明 |
|------|------|
| **Checkstyle** | 代码风格检查（Google Java Style） |
| **SpotBugs** | 静态代码分析 |
| **Git Hooks** | 提交前自动检查 |

### DevOps

| 工具 | 说明 |
|------|------|
| **GitHub Actions** | CI/CD 自动化 |
| **Docker** | 容器化部署 |
| **Sealos** | Kubernetes 云平台 |

---

## 🚀 快速开始

### 环境要求

- ☕ **JDK 21+**
- 🐘 **MySQL 8.0+**
- 🔴 **Redis 7.0+**
- 🐳 **Docker** (可选，用于本地运行数据库)

### 1. 克隆项目

```bash
git clone https://github.com/dylean/tdd-marketing-automation.git
cd tdd-marketing-automation
```

### 2. 启动数据库（使用 Docker Compose）

```bash
# 启动 MySQL 和 Redis
docker-compose up -d mysql redis

# 验证服务运行
docker-compose ps
```

### 3. 配置环境变量

```bash
# 复制环境变量模板
cp .env.example .env

# 编辑 .env 文件（根据实际情况修改）
vim .env
```

### 4. 运行测试

```bash
# 运行所有测试
./gradlew test

# 运行单元测试
./gradlew test --tests "com.tdd.ma.domain.*" --tests "com.tdd.ma.application.*"

# 运行集成测试
./gradlew test --tests "com.tdd.ma.integration.*"

# 运行架构测试
./gradlew test --tests "com.tdd.ma.architecture.*"
```

### 5. 启动应用

```bash
# 开发模式启动
./gradlew bootRun

# 或构建 JAR 后运行
./gradlew bootJar
java -jar build/libs/tdd-marketing-automation-1.0.0.jar
```

访问应用：http://localhost:8080

### 6. 查看健康状态

```bash
curl http://localhost:8080/actuator/health
```

---

## 📦 部署指南

### 方式一：Sealos 云平台部署（推荐）⭐

**最快 5 分钟部署到生产环境！**

```bash
# 1. 阅读快速指南
cat sealos/QUICK_START.md

# 2. 配置密码
cd sealos
cp secrets-template.yaml secrets.yaml
vim secrets.yaml

# 3. 一键部署
kubectl create namespace tdd-ma
kubectl apply -f secrets.yaml
kubectl apply -f database-deployment.yaml
kubectl apply -f app-deployment.yaml
```

**特性**：
- ☁️ 基于 Kubernetes，自动扩缩容
- 💰 按量付费，成本节省 50%+
- 🔐 自动 HTTPS 证书
- 📊 内置监控和日志
- 🚀 一键部署，零运维

**完整文档**：
- 📖 [快速部署指南](./sealos/QUICK_START.md) - 5 分钟快速上手
- 📚 [完整部署文档](./docs/SEALOS_DEPLOYMENT.md) - 详细配置和故障排查

---

### 方式二：Docker Compose 部署

```bash
# 1. 配置环境变量
cp .env.example .env
vim .env

# 2. 启动所有服务
docker-compose up -d

# 3. 查看日志
docker-compose logs -f app
```

访问应用：http://localhost:8080

**文档**：[Docker 部署指南](./README_DOCKER_SETUP.md)

---

### 方式三：GitHub Actions 自动部署

每次推送到 `main` 分支，自动触发 CI/CD 流水线：

```
Push to main
     ↓
🧪 CI: 测试 + 代码质量检查
     ↓
🐳 构建并推送 Docker 镜像
     ↓
☁️ 自动部署到 Sealos
```

**配置步骤**：

1. 在 GitHub 仓库设置中添加 Secrets：
   - `DOCKER_HUB_USERNAME`
   - `DOCKER_HUB_TOKEN`
   - `SEALOS_KUBECONFIG`
   - `MYSQL_PASSWORD`
   - `REDIS_PASSWORD`

2. 推送代码即可自动部署：

```bash
git add .
git commit -m "feat: add new feature"
git push origin main
```

3. 查看部署进度：访问 GitHub Actions 页面

**文档**：[CI/CD 配置指南](./docs/CI-CD-SETUP.md)

---

## 🧪 TDD 实践指南

### 红-绿-重构循环

```
🔴 Red → 🟢 Green → 🔵 Refactor
  ↑                       ↓
  └───────────────────────┘
```

1. **🔴 Red** - 编写一个失败的测试
2. **🟢 Green** - 编写最少的代码让测试通过
3. **🔵 Refactor** - 在测试保护下重构代码

### 测试金字塔

```
           /\
          /  \  E2E Tests (少量)
         /────\
        /      \  Integration Tests (适量)
       /────────\
      /          \  Unit Tests (大量)
     /────────────\
```

### 架构守护测试

本项目使用 **ArchUnit** 自动守护架构规则：

| 测试类 | 描述 | 示例规则 |
|--------|------|----------|
| `DddLayerArchitectureTest` | DDD 分层依赖规则 | Domain 层不能依赖 Application 层 |
| `NamingConventionTest` | 命名规范检查 | Controller 必须以 `Controller` 结尾 |
| `CodingRulesTest` | 编码规范检查 | Domain 实体不能使用 `@Data` |

运行架构测试：

```bash
./gradlew test --tests "com.tdd.ma.architecture.*"
```

### 代码质量检查

**本地检查**（Git Hooks 自动执行）：

```bash
# 手动运行代码质量检查
./gradlew check

# 只运行 Checkstyle
./gradlew checkstyleMain

# 只运行 SpotBugs
./gradlew spotbugsMain
```

**CI 自动检查**：每次推送到 GitHub 自动运行

---

## 📊 培训资料

### TDD 分享 PPT

培训 PPT 使用 [Slidev](https://sli.dev/) 构建，位于 `docs/slides/` 目录。

```bash
cd docs/slides

# 安装依赖
npm install

# 本地预览
npm run dev

# 构建生产版本
npm run build

# 导出 PDF
npm run export
```

访问：http://localhost:3030

**内容大纲**：

1. **TDD 简介** - 什么是 TDD，为什么要用 TDD
2. **手动 TDD 实战** - 营销活动预算管理案例
3. **AI 辅助 TDD** - 使用 Cursor/Copilot 进行 TDD
4. **总结与 Q&A**

**在线访问**：[部署到 Vercel](./docs/slides/DEPLOY_GUIDE.md)

### 故事卡

实战演练所需的故事卡位于 `docs/story-cards/` 目录：

| 故事卡 | 难度 | 说明 |
|--------|------|------|
| [01-manual-tdd-campaign-budget.md](./docs/story-cards/01-manual-tdd-campaign-budget.md) | ⭐⭐⭐ | 手动 TDD：营销活动预算管理 |
| [02-ai-tdd-campaign-analytics.md](./docs/story-cards/02-ai-tdd-campaign-analytics.md) | ⭐⭐⭐⭐ | AI TDD：营销活动数据分析 |

---

## 📁 项目结构

```
tdd-marketing-automation/
├── .github/                    # GitHub Actions CI/CD
│   └── workflows/
│       └── ci-cd.yml          # 统一的 CI/CD 流水线
├── sealos/                     # Sealos 部署配置
│   ├── app-deployment.yaml    # 应用部署配置
│   ├── database-deployment.yaml # 数据库配置
│   ├── secrets-template.yaml  # 密码模板
│   ├── QUICK_START.md         # 快速部署指南
│   └── README.md              # Sealos 配置说明
├── docs/                       # 文档
│   ├── slides/                # TDD 培训 PPT
│   ├── story-cards/           # 实战故事卡
│   ├── SEALOS_DEPLOYMENT.md   # Sealos 完整部署文档
│   ├── CI-CD-SETUP.md         # CI/CD 配置指南
│   ├── CODE_QUALITY.md        # 代码质量工具文档
│   └── GIT_HOOKS.md           # Git Hooks 文档
├── src/
│   ├── main/java/com/tdd/ma/ # 应用代码
│   └── test/java/com/tdd/ma/ # 测试代码
├── scripts/                    # 脚本
│   └── git-hooks/             # Git Hooks
│       ├── pre-push.sh        # 推送前代码检查
│       └── commit-msg.sh      # 提交消息验证
├── config/                     # 配置文件
│   ├── checkstyle/            # Checkstyle 配置
│   └── spotbugs/              # SpotBugs 配置
├── docker-compose.yml         # Docker Compose 配置
├── Dockerfile                 # Docker 镜像构建
├── build.gradle.kts           # Gradle 构建脚本（Kotlin DSL）
└── README.md                  # 本文件
```

---

## 🔧 开发指南

### Git 提交规范

遵循 [Conventional Commits](https://www.conventionalcommits.org/zh-hans/) 规范：

```bash
# 格式
<type>(<scope>): <subject>

# 示例
feat(campaign): add budget management
fix(cache): fix redis connection timeout
docs(readme): update deployment guide
test(campaign): add integration test for create campaign
```

**类型**：
- `feat`: 新功能
- `fix`: Bug 修复
- `docs`: 文档更新
- `test`: 测试相关
- `refactor`: 代码重构
- `style`: 代码格式
- `chore`: 构建/工具相关

**Git Hooks** 会自动验证提交消息格式 ✅

### 代码风格

- 遵循 **Google Java Style Guide**
- 使用 **Checkstyle** 自动检查
- 使用 **SpotBugs** 静态分析
- Git `pre-push` 钩子自动运行检查

### 分支策略

```
main (受保护)
  ↑
  └─ feature/xxx (功能分支)
  └─ fix/xxx (修复分支)
```

---

## 📈 监控和日志

### 应用监控

访问 Spring Boot Actuator 端点：

```bash
# 健康检查
curl http://localhost:8080/actuator/health

# 应用信息
curl http://localhost:8080/actuator/info

# 性能指标
curl http://localhost:8080/actuator/metrics
```

### Sealos 监控

如果部署在 Sealos：

```bash
# 查看 Pod 状态
kubectl get pods -n tdd-ma

# 查看应用日志
kubectl logs -f deployment/tdd-marketing-automation -n tdd-ma

# 查看资源使用
kubectl top pods -n tdd-ma

# 查看 HPA 状态
kubectl get hpa -n tdd-ma
```

---

## 🤝 贡献指南

欢迎贡献！请遵循以下步骤：

1. Fork 本仓库
2. 创建功能分支：`git checkout -b feature/xxx`
3. 提交更改：`git commit -m 'feat: add xxx'`
4. 推送分支：`git push origin feature/xxx`
5. 提交 Pull Request

**PR 要求**：
- ✅ 所有测试通过
- ✅ 代码质量检查通过
- ✅ 提交消息符合规范
- ✅ 包含必要的测试

---

## 📚 学习资源

### TDD 相关

- [《测试驱动开发》](https://book.douban.com/subject/1230036/) - Kent Beck
- [《重构》](https://book.douban.com/subject/30468597/) - Martin Fowler
- [TDD by Example](https://www.amazon.com/Test-Driven-Development-Kent-Beck/dp/0321146530)

### DDD 相关

- [《领域驱动设计》](https://book.douban.com/subject/26819666/) - Eric Evans
- [《实现领域驱动设计》](https://book.douban.com/subject/25844633/) - Vaughn Vernon

### 架构测试

- [ArchUnit 官方文档](https://www.archunit.org/)
- [ArchUnit 用户指南](https://www.archunit.org/userguide/html/000_Index.html)

---

## 📄 License

MIT License - 详见 [LICENSE](./LICENSE) 文件

---

## 🆘 获取帮助

- 📖 **文档**：查看 `docs/` 目录下的详细文档
- 🐛 **Bug 报告**：提交 [GitHub Issue](https://github.com/dylean/tdd-marketing-automation/issues)
- 💬 **讨论**：参与 [GitHub Discussions](https://github.com/dylean/tdd-marketing-automation/discussions)
- ☁️ **Sealos 支持**：访问 [Sealos 论坛](https://forum.sealos.io)

---

## ⭐ Star History

如果这个项目对你有帮助，请给个 Star ⭐️

---

**Made with ❤️ using TDD**
