# GitHub Actions CI/CD 工作流说明

## 📋 工作流列表

### 1. CI (持续集成)
**文件**: `ci.yml`  
**触发条件**: 
- Push 到 `main` 或 `develop` 分支
- Pull Request 到 `main` 或 `develop` 分支

**流程**:
```
编译 → 单元测试 → 集成测试 → 代码质量检查 → 构建产物
```

**特点**:
- ✅ 使用 MySQL 和 Redis 服务容器
- ✅ 分离单元测试和集成测试
- ✅ 运行 ArchUnit 架构守护测试
- ✅ 生成测试报告
- ✅ 上传构建产物（JAR 包）

---

### 2. Deploy (持续部署)
**文件**: `deploy.yml`  
**触发条件**:
- Push 到 `main` 分支（部署到 Staging）
- 创建 tag `v*`（部署到 Production）
- 手动触发（可选择环境）

**流程**:
```
构建 → Docker 镜像 → 推送仓库 → 部署 → 健康检查
```

**环境**:
- **Staging**: 预发布环境，用于测试
- **Production**: 生产环境，需要手动审批

---

### 3. PR Check (PR 检查)
**文件**: `pr-check.yml`  
**触发条件**: Pull Request 开启或更新

**检查项**:
- ✅ 编译检查
- ✅ 快速测试（Domain + Application 层）
- ✅ 架构守护检查
- ✅ PR 标题格式检查
- ✅ PR 大小标签

---

## 🔧 配置说明

### 必需的 Secrets

在 GitHub Repository Settings → Secrets 中配置：

| Secret Name | 说明 | 示例 |
|------------|------|------|
| `DOCKER_USERNAME` | Docker Hub 用户名 | your-username |
| `DOCKER_PASSWORD` | Docker Hub 密码/Token | xxx |
| `DEPLOY_SSH_KEY` | 部署服务器 SSH 私钥 | -----BEGIN OPENSSH... |
| `STAGING_SERVER` | Staging 服务器地址 | staging.example.com |
| `PRODUCTION_SERVER` | Production 服务器地址 | app.example.com |

### 环境变量

在 `ci.yml` 中配置的测试环境变量：

```yaml
TEST_DB_URL: localhost
TEST_DB_PORT: 3306
TEST_DB_NAME: tdd_marketing_automation_test
TEST_DB_USERNAME: root
TEST_DB_PASSWORD: test_password
REDIS_HOST: localhost
REDIS_PORT: 6379
```

---

## 📊 工作流状态徽章

在 README.md 中添加徽章：

```markdown
![CI](https://github.com/your-username/tdd-marketing-automation/workflows/CI/badge.svg)
![Deploy](https://github.com/your-username/tdd-marketing-automation/workflows/Deploy/badge.svg)
```

---

## 🚀 使用指南

### 日常开发流程

1. **创建分支**
```bash
git checkout -b feat/add-new-feature
```

2. **提交代码**
```bash
git add .
git commit -m "feat(campaign): add budget validation"
git push origin feat/add-new-feature
```

3. **创建 Pull Request**
- PR 会自动触发 `pr-check.yml`
- 等待所有检查通过
- 请求团队成员 Review

4. **合并到 develop**
- 合并后触发 `ci.yml` 完整测试

5. **合并到 main**
- 触发 `ci.yml` + `deploy.yml`
- 自动部署到 Staging 环境

6. **发布到生产**
```bash
git tag -a v1.0.0 -m "Release v1.0.0"
git push origin v1.0.0
```
- 自动部署到 Production 环境

---

## 🐳 Docker 构建

### 本地构建测试

```bash
# 构建镜像
docker build -t tdd-marketing-automation:local .

# 运行容器
docker run -p 8080:8080 \
  -e DB_URL=your-db-host \
  -e DB_USERNAME=root \
  -e DB_PASSWORD=password \
  tdd-marketing-automation:local
```

### 多阶段构建优势

- ✅ 构建阶段和运行阶段分离
- ✅ 最小化镜像大小（使用 JRE 而非 JDK）
- ✅ 利用 Docker 缓存加速构建
- ✅ 安全性（非 root 用户运行）

---

## 🔍 故障排查

### 测试失败

1. **查看测试报告**
   - 在 GitHub Actions 页面下载 `test-reports` artifact
   - 打开 `index.html` 查看详细报告

2. **本地重现**
```bash
# 运行相同的测试
./gradlew test --tests "com.tdd.ma.domain.*"
```

### 构建失败

1. **检查 Gradle 日志**
```bash
./gradlew build --stacktrace
```

2. **清理缓存**
```bash
./gradlew clean build --no-daemon
```

### 部署失败

1. **检查健康检查端点**
```bash
curl http://your-server:8080/actuator/health
```

2. **查看容器日志**
```bash
docker logs <container-id>
```

---

## 📈 性能优化

### 加速 CI 构建

1. **启用 Gradle 缓存**
   - 已在 `setup-java` 中配置 `cache: gradle`

2. **并行测试**
```groovy
// build.gradle.kts
tasks.test {
    maxParallelForks = Runtime.runtime.availableProcessors()
}
```

3. **只运行受影响的测试**
   - 使用 `--tests` 参数指定测试范围

---

## 🔒 安全最佳实践

1. **不在代码中硬编码敏感信息**
   - 使用 GitHub Secrets
   - 使用环境变量

2. **定期更新依赖**
```bash
./gradlew dependencyUpdates
```

3. **扫描安全漏洞**
   - 可以添加 Snyk 或 Dependabot

4. **最小权限原则**
   - 容器使用非 root 用户
   - GitHub Token 只授予必要权限

---

## 📚 相关资源

- [GitHub Actions 文档](https://docs.github.com/en/actions)
- [Docker 最佳实践](https://docs.docker.com/develop/dev-best-practices/)
- [Spring Boot Docker 指南](https://spring.io/guides/topicals/spring-boot-docker/)
- [Gradle Docker 插件](https://github.com/palantir/gradle-docker)

---

**维护者**: DevOps Team  
**最后更新**: 2026-01-19
