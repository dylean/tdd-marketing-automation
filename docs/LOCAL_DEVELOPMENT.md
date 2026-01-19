# 💻 本地开发指南

本文档说明如何在本地使用 Docker Compose 快速启动开发环境。

---

## 🎯 适用场景

- ✅ 本地开发和调试
- ✅ 快速测试新功能
- ✅ 不需要连接外部数据库
- ✅ 一键启动完整环境

---

## 🚀 快速开始

### 步骤 1: 配置环境变量

```bash
# 复制环境变量模板
cp .env.example .env

# 编辑配置（可选，默认值已可用）
vim .env
```

**默认配置**：
```bash
DB_NAME=tdd_marketing_automation
DB_USERNAME=tdd_user
DB_PASSWORD=user_password
DB_PORT=3306
REDIS_PORT=6379
APP_PORT=8080
```

### 步骤 2: 启动服务

```bash
# 启动所有服务（MySQL + Redis + 应用）
docker-compose up -d

# 查看日志
docker-compose logs -f

# 只查看应用日志
docker-compose logs -f app
```

### 步骤 3: 验证服务

```bash
# 查看服务状态
docker-compose ps

# 预期输出:
# NAME              STATUS    PORTS
# tdd-ma-mysql      running   0.0.0.0:3306->3306/tcp
# tdd-ma-redis      running   0.0.0.0:6379->6379/tcp
# tdd-ma-app        running   0.0.0.0:8080->8080/tcp

# 测试应用
curl http://localhost:8080/actuator/health

# 预期输出:
# {"status":"UP"}
```

### 步骤 4: 访问应用

- **应用**: http://localhost:8080
- **健康检查**: http://localhost:8080/actuator/health
- **MySQL**: localhost:3306
- **Redis**: localhost:6379

---

## 📦 Docker Compose 服务

### 服务列表

| 服务 | 镜像 | 端口 | 说明 |
|------|------|------|------|
| **mysql** | mysql:8.0 | 3306 | MySQL 数据库 |
| **redis** | redis:7.0-alpine | 6379 | Redis 缓存 |
| **app** | 本地构建 | 8080 | Spring Boot 应用 |

### 数据持久化

数据存储在 Docker volumes 中：
- `mysql_data`: MySQL 数据
- `redis_data`: Redis 数据

```bash
# 查看 volumes
docker volume ls | grep tdd-ma

# 清理数据（危险操作！）
docker-compose down -v
```

---

## 🔧 常用命令

### 启动和停止

```bash
# 启动所有服务
docker-compose up -d

# 停止所有服务
docker-compose stop

# 停止并删除容器
docker-compose down

# 停止并删除容器和数据卷（危险！）
docker-compose down -v
```

### 查看日志

```bash
# 查看所有日志
docker-compose logs

# 实时查看日志
docker-compose logs -f

# 查看特定服务日志
docker-compose logs -f app
docker-compose logs -f mysql
docker-compose logs -f redis

# 查看最近 100 行日志
docker-compose logs --tail=100 app
```

### 重启服务

```bash
# 重启所有服务
docker-compose restart

# 重启特定服务
docker-compose restart app
docker-compose restart mysql
docker-compose restart redis
```

### 重新构建

```bash
# 重新构建应用镜像
docker-compose build app

# 重新构建并启动
docker-compose up -d --build

# 强制重新构建（不使用缓存）
docker-compose build --no-cache app
```

---

## 🔍 调试技巧

### 1. 进入容器

```bash
# 进入应用容器
docker-compose exec app /bin/sh

# 进入 MySQL 容器
docker-compose exec mysql mysql -u root -p

# 进入 Redis 容器
docker-compose exec redis redis-cli
```

### 2. 查看容器状态

```bash
# 查看资源使用
docker-compose top

# 查看详细信息
docker-compose ps -a
```

### 3. 测试数据库连接

```bash
# 测试 MySQL
docker-compose exec mysql mysql -u tdd_user -p tdd_marketing_automation

# 测试 Redis
docker-compose exec redis redis-cli ping
```

---

## 🐛 故障排查

### 问题 1: 端口被占用

**错误**：
```
Error: Bind for 0.0.0.0:3306 failed: port is already allocated
```

**解决方案**：
```bash
# 查看端口占用
lsof -i :3306
lsof -i :6379
lsof -i :8080

# 修改 .env 中的端口
DB_PORT=3307
REDIS_PORT=6380
APP_PORT=8081

# 重新启动
docker-compose up -d
```

---

### 问题 2: 数据库连接失败

**错误**：
```
CommunicationsException: Communications link failure
```

**解决方案**：
```bash
# 1. 检查 MySQL 是否就绪
docker-compose logs mysql | grep "ready for connections"

# 2. 等待 MySQL 启动完成（约 30 秒）
docker-compose up -d mysql
sleep 30

# 3. 重启应用
docker-compose restart app
```

---

### 问题 3: 应用启动失败

**解决方案**：
```bash
# 1. 查看应用日志
docker-compose logs app

# 2. 检查环境变量
docker-compose exec app env | grep -E "DB_|REDIS_"

# 3. 重新构建
docker-compose build --no-cache app
docker-compose up -d
```

---

### 问题 4: 数据丢失

**原因**：使用了 `docker-compose down -v`

**解决方案**：
```bash
# 备份数据（定期执行）
docker-compose exec mysql mysqldump -u root -p tdd_marketing_automation > backup.sql

# 恢复数据
docker-compose exec -T mysql mysql -u root -p tdd_marketing_automation < backup.sql
```

---

## 🔄 开发工作流

### 典型开发流程

```bash
# 1. 启动环境
docker-compose up -d

# 2. 修改代码
vim src/main/java/com/tdd/ma/...

# 3. 重新构建并启动
./gradlew bootJar
docker-compose build app
docker-compose up -d

# 4. 查看日志
docker-compose logs -f app

# 5. 测试
curl http://localhost:8080/actuator/health

# 6. 停止环境（保留数据）
docker-compose stop
```

---

## 🆚 本地开发 vs 生产部署

| 项目 | 本地开发 | Sealos 生产 |
|------|---------|------------|
| **配置文件** | `.env` | `sealos/external-db-secret-template.yaml` |
| **数据库** | Docker 容器 | 外部 MySQL |
| **Redis** | Docker 容器 | 外部 Redis |
| **启动方式** | `docker-compose up` | `kubectl apply` |
| **访问地址** | localhost:8080 | https://your-domain.com |
| **数据持久化** | Docker volumes | 外部数据库 |
| **适用场景** | 开发、测试 | 生产环境 |

---

## 📚 相关文档

- [Docker Compose 官方文档](https://docs.docker.com/compose/)
- [Sealos 部署指南](./SEALOS_DEPLOYMENT.md)
- [环境变量流程](./ENV_VARIABLES_FLOW.md)
- [GitHub Secrets 配置](./GITHUB_SECRETS_SETUP.md)

---

## 💡 最佳实践

1. ✅ **使用 `.env` 文件**
   - 不要提交到 Git
   - 每个开发者有自己的配置

2. ✅ **定期备份数据**
   - 避免使用 `docker-compose down -v`
   - 定期导出数据库

3. ✅ **查看日志**
   - 启动后检查日志
   - 确认服务正常运行

4. ✅ **清理资源**
   - 定期清理未使用的镜像和容器
   - `docker system prune -a`

---

**🎉 现在你可以在本地快速开发了！**

```bash
cp .env.example .env
docker-compose up -d
curl http://localhost:8080/actuator/health
```
