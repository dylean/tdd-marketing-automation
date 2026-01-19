# GitHub CI/CD 配置指南

## 🎯 快速开始

项目已配置完整的 GitHub Actions CI/CD 流程，推送代码后会自动触发。

### 1️⃣ 第一次使用

**推送代码到 GitHub**:
```bash
git add .
git commit -m "feat: setup GitHub CI/CD"
git push origin main
```

**查看工作流**:
- 访问 `https://github.com/your-username/tdd-marketing-automation/actions`
- 查看 CI 工作流运行状态

---

## 📦 已配置的工作流

### CI (持续集成) - `ci.yml`

**触发时机**:
- ✅ Push 到 `main` 或 `develop` 分支
- ✅ Pull Request

**执行内容**:
```
┌─────────────────────────────────────────┐
│  1. 编译检查 (compileJava)              │
│  2. 单元测试 (domain + application)     │
│  3. 集成测试 (integration)              │
│  4. 架构守护测试 (ArchUnit)             │
│  5. 构建 JAR 包                         │
│  6. 上传测试报告和构建产物               │
└─────────────────────────────────────────┘
```

**服务容器**:
- MySQL 8.0 (端口 3306)
- Redis 7.0 (端口 6379)

---

### Deploy (持续部署) - `deploy.yml`

**触发时机**:
- ✅ Push 到 `main` → 部署到 Staging
- ✅ Tag `v*` → 部署到 Production
- ✅ 手动触发

**部署流程**:
```
构建 JAR → 构建 Docker 镜像 → 推送镜像 → 部署 → 健康检查
```

---

### PR Check (PR 检查) - `pr-check.yml`

**检查内容**:
- ✅ 编译检查
- ✅ 快速测试
- ✅ 架构守护
- ✅ PR 标题格式 (Conventional Commits)
- ✅ PR 大小标签

---

## ⚙️ 配置 GitHub Secrets

### 必需的 Secrets

前往 `Settings` → `Secrets and variables` → `Actions` → `New repository secret`:

| Secret 名称 | 说明 | 如何获取 |
|------------|------|---------|
| `DOCKER_USERNAME` | Docker Hub 用户名 | [hub.docker.com](https://hub.docker.com) 注册 |
| `DOCKER_PASSWORD` | Docker Hub Token | Settings → Security → New Access Token |
| `DEPLOY_SSH_KEY` | 服务器 SSH 私钥 | `ssh-keygen -t ed25519 -C "deploy@ci"` |

### 可选的 Secrets

| Secret 名称 | 说明 |
|------------|------|
| `STAGING_SERVER` | Staging 服务器地址 |
| `PRODUCTION_SERVER` | Production 服务器地址 |
| `SLACK_WEBHOOK` | Slack 通知 Webhook |

---

## 🐳 Docker 部署

### 本地测试 Docker 构建

```bash
# 1. 构建镜像
docker build -t tdd-ma:local .

# 2. 运行容器
docker run -p 8080:8080 \
  -e DB_URL=your-mysql-host \
  -e DB_USERNAME=root \
  -e DB_PASSWORD=password \
  -e REDIS_HOST=your-redis-host \
  tdd-ma:local

# 3. 健康检查
curl http://localhost:8080/actuator/health
```

### Docker Compose 本地开发

创建 `docker-compose.yml`:

```yaml
version: '3.8'

services:
  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      - DB_URL=mysql
      - DB_USERNAME=root
      - DB_PASSWORD=password
      - REDIS_HOST=redis
    depends_on:
      - mysql
      - redis

  mysql:
    image: mysql:8.0
    environment:
      - MYSQL_ROOT_PASSWORD=password
      - MYSQL_DATABASE=tdd_marketing_automation
    ports:
      - "3306:3306"

  redis:
    image: redis:7.0
    ports:
      - "6379:6379"
```

运行:
```bash
docker-compose up -d
```

---

## 🚀 发布流程

### 开发分支 → Staging

```bash
# 1. 开发新功能
git checkout -b feat/new-feature

# 2. 提交代码
git commit -m "feat(campaign): add new feature"

# 3. 推送并创建 PR
git push origin feat/new-feature

# 4. PR 通过后合并到 develop
# (自动触发 CI)

# 5. 合并 develop 到 main
# (自动触发 CI + Deploy to Staging)
```

### Staging → Production

```bash
# 1. 确认 Staging 环境正常

# 2. 创建版本 Tag
git checkout main
git pull
git tag -a v1.0.0 -m "Release version 1.0.0"
git push origin v1.0.0

# 3. 自动部署到 Production
# (需要在 GitHub 手动批准)
```

---

## 📊 监控和告警

### 查看 CI 状态

**方式 1: GitHub Actions 页面**
```
https://github.com/your-username/tdd-marketing-automation/actions
```

**方式 2: 在 README 中添加徽章**
```markdown
![CI](https://github.com/your-username/tdd-marketing-automation/workflows/CI/badge.svg)
![Deploy](https://github.com/your-username/tdd-marketing-automation/workflows/Deploy/badge.svg)
```

**方式 3: 测试报告**
- 每次 CI 运行后会生成测试报告
- 在 Actions → 选择运行 → Artifacts → 下载 `test-reports`

---

## 🔧 自定义配置

### 修改 CI 流程

编辑 `.github/workflows/ci.yml`:

```yaml
# 例如：添加代码覆盖率检查
- name: 生成代码覆盖率报告
  run: ./gradlew jacocoTestReport

- name: 上传到 Codecov
  uses: codecov/codecov-action@v3
```

### 修改部署目标

编辑 `.github/workflows/deploy.yml`:

```yaml
# 例如：部署到 Kubernetes
- name: 部署到 K8s
  run: |
    kubectl apply -f k8s/deployment.yml
    kubectl rollout status deployment/tdd-ma
```

---

## 🐛 故障排查

### CI 失败

**步骤 1: 查看日志**
```
Actions → 点击失败的运行 → 点击失败的步骤
```

**步骤 2: 本地重现**
```bash
# 运行相同的测试命令
./gradlew test --tests "com.tdd.ma.domain.*"
```

**步骤 3: 检查依赖**
```bash
./gradlew dependencies --configuration runtimeClasspath
```

### Docker 构建失败

**检查 Dockerfile**:
```bash
docker build -t test . --progress=plain
```

**查看构建日志**:
```bash
docker build -t test . --no-cache 2>&1 | tee build.log
```

### 部署失败

**健康检查**:
```bash
curl -v http://your-server:8080/actuator/health
```

**查看容器日志**:
```bash
docker logs <container-id> --tail 100
```

**进入容器调试**:
```bash
docker exec -it <container-id> sh
```

---

## 📈 性能优化

### 加速 CI 构建

**1. 启用 Gradle Daemon**
```groovy
// gradle.properties
org.gradle.daemon=true
org.gradle.parallel=true
org.gradle.caching=true
```

**2. 使用 GitHub Actions 缓存**
- 已在 `setup-java` 中配置 `cache: gradle`

**3. 并行运行测试**
```groovy
// build.gradle.kts
tasks.test {
    maxParallelForks = (Runtime.runtime.availableProcessors() / 2).takeIf { it > 0 } ?: 1
}
```

---

## 🔒 安全建议

1. **定期更新依赖**
```bash
./gradlew dependencyUpdates
```

2. **扫描容器镜像**
```bash
docker scan tdd-ma:latest
```

3. **使用 Secrets 管理敏感信息**
- ❌ 不要在代码中硬编码密码
- ✅ 使用 GitHub Secrets
- ✅ 使用环境变量

4. **最小权限原则**
- 容器使用非 root 用户运行
- GitHub Token 只授予必要权限

---

## 📝 Commit 规范

遵循 [Conventional Commits](https://www.conventionalcommits.org/):

```
<type>(<scope>): <subject>

<body>

<footer>
```

**类型**:
- `feat`: 新功能
- `fix`: Bug 修复
- `docs`: 文档更新
- `style`: 代码格式调整
- `refactor`: 代码重构
- `perf`: 性能优化
- `test`: 测试相关
- `chore`: 构建/工具变更

**示例**:
```bash
git commit -m "feat(campaign): add budget management feature"
git commit -m "fix(cache): resolve redis connection timeout"
git commit -m "docs: update CI/CD setup guide"
```

---

## 🎓 最佳实践

### 1. 分支策略

```
main       (生产)
  ↑
develop    (开发)
  ↑
feature/*  (功能分支)
bugfix/*   (修复分支)
hotfix/*   (紧急修复)
```

### 2. PR 流程

1. 创建 feature 分支
2. 开发并提交
3. 推送并创建 PR
4. CI 自动检查
5. Code Review
6. 合并到 develop
7. 定期合并到 main

### 3. 测试策略

- 单元测试覆盖率 > 80%
- 关键路径必须有集成测试
- 架构守护测试防止架构腐化

---

## 📚 相关文档

- [工作流详细说明](.github/workflows/README.md)
- [Dockerfile 说明](../Dockerfile)
- [PR 模板](.github/PULL_REQUEST_TEMPLATE.md)

---

**维护者**: DevOps Team  
**创建时间**: 2026-01-19  
**版本**: v1.0
