# Docker 部署指南

本文档介绍如何配置 Docker Hub 仓库，并使用 Docker 运行 TDD Marketing Automation 应用。

---

## 📦 配置 Docker Hub 仓库

### 1. 创建 Docker Hub 账号

访问 [Docker Hub](https://hub.docker.com/) 注册账号（如果还没有）。

### 2. 创建访问令牌（推荐）

**为什么使用 Token 而不是密码？**
- ✅ 更安全（可以随时撤销）
- ✅ 权限控制更精细
- ✅ 适合 CI/CD 自动化

**步骤**：

1. 登录 Docker Hub
2. 点击右上角头像 → **Account Settings**
3. 选择 **Security** → **New Access Token**
4. 填写：
   - **Description**: `GitHub Actions for tdd-marketing-automation`
   - **Access permissions**: `Read, Write, Delete`
5. 点击 **Generate**
6. **⚠️ 立即复制 Token**（只显示一次）

---

## 🔐 配置 GitHub Secrets

在你的 GitHub 仓库中配置 Docker Hub 凭证：

### 步骤：

1. 打开 GitHub 仓库
2. 进入 **Settings** → **Secrets and variables** → **Actions**
3. 点击 **New repository secret**
4. 添加以下两个 secrets：

| Secret Name | Value | 说明 |
|-------------|-------|------|
| `DOCKER_HUB_USERNAME` | 你的 Docker Hub 用户名 | 例如: `johndoe` |
| `DOCKER_HUB_TOKEN` | 刚创建的访问令牌 | 例如: `dckr_pat_...` |

**示例**：
```
DOCKER_HUB_USERNAME: your-dockerhub-username
DOCKER_HUB_TOKEN: <paste-your-token-here>
```

---

## 🚀 部署流程

### 方式 1: 通过 Git Tag 自动部署（推荐）

```bash
# 1. 提交代码
git add .
git commit -m "feat: add new feature"

# 2. 打 tag（触发部署）
git tag v1.0.0
git push origin v1.0.0

# 3. GitHub Actions 自动执行：
#    - 构建应用
#    - 构建 Docker 镜像
#    - 推送到 Docker Hub
```

**镜像标签**：
- `your-username/tdd-marketing-automation:v1.0.0`
- `your-username/tdd-marketing-automation:latest`

### 方式 2: 手动触发部署

1. 打开 GitHub 仓库
2. 进入 **Actions** → **Deploy**
3. 点击 **Run workflow**
4. 选择分支
5. 点击 **Run workflow**

---

## 🐳 Docker 运行参数

### 方式 1: 使用 Docker Compose（推荐）

#### 1. 创建 `.env` 文件

```bash
cp .env.example .env
vim .env
```

修改配置：

```bash
# Docker Hub 用户名
DOCKER_HUB_USERNAME=deantdd

# 数据库配置
DB_NAME=tdd_marketing_automation
DB_USERNAME=tdd_user
DB_PASSWORD=your_secure_password

# Redis 配置（如果需要密码）
REDIS_PASSWORD=your_redis_password

# 外部服务 URL
AUDIENCE_SERVICE_URL=http://your-audience-service:8081

# 应用端口
APP_PORT=8080
```

#### 2. 启动所有服务

```bash
# 拉取最新镜像
docker-compose pull

# 启动服务（后台运行）
docker-compose up -d

# 查看日志
docker-compose logs -f app

# 查看状态
docker-compose ps
```

#### 3. 停止服务

```bash
# 停止所有服务
docker-compose down

# 停止并删除数据卷
docker-compose down -v
```

---

### 方式 2: 使用 Docker Run 命令

#### 完整启动命令

```bash
docker run -d \
  --name tdd-ma-app \
  --restart unless-stopped \
  -p 8080:8080 \
  -e DB_URL=your-mysql-host \
  -e DB_PORT=3306 \
  -e DB_NAME=tdd_marketing_automation \
  -e DB_USERNAME=tdd_user \
  -e DB_PASSWORD=your_password \
  -e REDIS_HOST=your-redis-host \
  -e REDIS_PORT=6379 \
  -e REDIS_PASSWORD=your_redis_password \
  -e AUDIENCE_SERVICE_URL=http://audience-service:8081 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e JAVA_OPTS="-Xmx512m -Xms256m -XX:+UseG1GC" \
  -v /path/to/logs:/app/logs \
  --network your-network \
  deantdd/tdd-marketing-automation:latest
```

---

## 📝 环境变量说明

### 必需的环境变量

| 环境变量 | 说明 | 示例 | 默认值 |
|---------|------|------|--------|
| `DB_URL` | MySQL 主机地址 | `mysql.example.com` | `localhost` |
| `DB_PORT` | MySQL 端口 | `3306` | `3306` |
| `DB_NAME` | 数据库名称 | `tdd_marketing_automation` | - |
| `DB_USERNAME` | 数据库用户名 | `tdd_user` | `root` |
| `DB_PASSWORD` | 数据库密码 | `your_password` | `root` |

### Redis 配置

| 环境变量 | 说明 | 示例 | 默认值 |
|---------|------|------|--------|
| `REDIS_HOST` | Redis 主机地址 | `redis.example.com` | `localhost` |
| `REDIS_PORT` | Redis 端口 | `6379` | `6379` |
| `REDIS_PASSWORD` | Redis 密码（可选） | `your_redis_pwd` | 空 |

### 外部服务配置

| 环境变量 | 说明 | 示例 | 默认值 |
|---------|------|------|--------|
| `AUDIENCE_SERVICE_URL` | 受众服务 URL | `http://audience:8081` | `http://localhost:8081` |

### 应用配置

| 环境变量 | 说明 | 示例 | 默认值 |
|---------|------|------|--------|
| `SPRING_PROFILES_ACTIVE` | Spring Profile | `prod` / `dev` | - |
| `SERVER_PORT` | 应用端口 | `8080` | `8080` |

### JVM 参数

| 环境变量 | 说明 | 示例 |
|---------|------|------|
| `JAVA_OPTS` | JVM 参数 | `-Xmx512m -Xms256m` |

**推荐配置**：
```bash
JAVA_OPTS="-Xmx512m -Xms256m -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -Duser.timezone=Asia/Shanghai"
```

---

## 🔍 健康检查

### 方式 1: 使用 Actuator

```bash
# 检查应用健康状态
curl http://localhost:8080/actuator/health

# 预期响应
{
  "status": "UP",
  "components": {
    "db": { "status": "UP" },
    "redis": { "status": "UP" },
    "diskSpace": { "status": "UP" },
    "ping": { "status": "UP" }
  }
}
```

### 方式 2: 查看日志

```bash
# Docker Compose
docker-compose logs -f app

# Docker Run
docker logs -f tdd-ma-app
```

---

## 🛠️ 常用操作

### 查看运行状态

```bash
# Docker Compose
docker-compose ps

# Docker Run
docker ps | grep tdd-ma-app
```

### 进入容器

```bash
# Docker Compose
docker-compose exec app bash

# Docker Run
docker exec -it tdd-ma-app bash
```

### 重启服务

```bash
# Docker Compose
docker-compose restart app

# Docker Run
docker restart tdd-ma-app
```

### 查看资源占用

```bash
docker stats tdd-ma-app
```

### 更新到最新版本

```bash
# 1. 拉取最新镜像
docker pull deantdd/tdd-marketing-automation:latest

# 2. 停止并删除旧容器
docker-compose down

# 3. 启动新容器
docker-compose up -d

# 4. 查看日志
docker-compose logs -f app
```

---

## 🐛 故障排查

### 问题 1: 容器启动失败

**排查步骤**：

```bash
# 查看容器日志
docker logs tdd-ma-app

# 常见错误：
# 1. 数据库连接失败 → 检查 DB_URL, DB_USERNAME, DB_PASSWORD
# 2. Redis 连接失败 → 检查 REDIS_HOST, REDIS_PORT
# 3. 端口被占用 → 修改 APP_PORT
```

### 问题 2: 数据库连接失败

**检查清单**：

```bash
# 1. 验证数据库是否可访问
docker exec -it tdd-ma-mysql mysql -u root -p

# 2. 检查网络连接
docker network ls
docker network inspect tdd-ma-network

# 3. 验证环境变量
docker exec tdd-ma-app env | grep DB_
```

### 问题 3: 健康检查失败

```bash
# 进入容器检查
docker exec -it tdd-ma-app bash

# 测试健康检查端点
curl http://localhost:8080/actuator/health

# 检查 Flyway 迁移
curl http://localhost:8080/actuator/flyway
```

### 问题 4: 内存不足

**解决方案**：

```bash
# 方式 1: 调整 docker-compose.yml
services:
  app:
    deploy:
      resources:
        limits:
          memory: 1G
        reservations:
          memory: 512M

# 方式 2: 调整 JAVA_OPTS
JAVA_OPTS="-Xmx384m -Xms192m -XX:+UseG1GC"
```

---

## 📊 监控和日志

### 查看应用日志

```bash
# 实时查看
docker-compose logs -f app

# 查看最近 100 行
docker-compose logs --tail=100 app

# 查看特定时间段
docker-compose logs --since 30m app
```

### 日志持久化

日志会自动保存到 Docker Volume `app_logs`：

```bash
# 查看 volume
docker volume ls | grep app_logs

# 查看 volume 路径
docker volume inspect tdd-marketing-automation_app_logs
```

---

## 🔒 生产环境安全建议

### 1. 使用 Secrets 管理敏感信息

```yaml
# docker-compose.yml
services:
  app:
    secrets:
      - db_password
      - redis_password

secrets:
  db_password:
    file: ./secrets/db_password.txt
  redis_password:
    file: ./secrets/redis_password.txt
```

### 2. 限制资源使用

```yaml
services:
  app:
    deploy:
      resources:
        limits:
          cpus: '1.0'
          memory: 1G
```

### 3. 启用只读根文件系统

```yaml
services:
  app:
    read_only: true
    tmpfs:
      - /tmp
```

### 4. 使用非 root 用户

已在 Dockerfile 中配置：

```dockerfile
RUN addgroup --system appuser && adduser --system --ingroup appuser appuser
USER appuser
```

---

## 📚 相关命令速查

```bash
# ========== Docker Compose ==========
docker-compose up -d              # 启动所有服务
docker-compose down               # 停止所有服务
docker-compose ps                 # 查看服务状态
docker-compose logs -f app        # 查看应用日志
docker-compose exec app bash      # 进入应用容器
docker-compose restart app        # 重启应用

# ========== Docker ==========
docker pull username/image:tag    # 拉取镜像
docker run -d ...                 # 启动容器
docker ps                         # 查看运行中的容器
docker logs -f container          # 查看日志
docker exec -it container bash    # 进入容器
docker stop container             # 停止容器
docker rm container               # 删除容器
docker stats container            # 查看资源使用

# ========== 清理 ==========
docker system prune               # 清理未使用的资源
docker volume prune               # 清理未使用的 volumes
docker image prune                # 清理未使用的镜像
```

---

## 🎓 最佳实践

1. **使用 Docker Compose**  
   推荐用于开发和小规模部署，配置简单，易于管理。

2. **配置健康检查**  
   确保服务异常时自动重启。

3. **日志持久化**  
   使用 volumes 保存日志，便于问题排查。

4. **环境变量外部化**  
   使用 `.env` 文件管理配置，不要硬编码敏感信息。

5. **定期更新镜像**  
   及时拉取最新镜像，应用安全补丁。

6. **监控资源使用**  
   使用 `docker stats` 监控 CPU、内存使用情况。

---

**维护者**: DevOps Team  
**最后更新**: 2026-01-20  
**版本**: v1.0
