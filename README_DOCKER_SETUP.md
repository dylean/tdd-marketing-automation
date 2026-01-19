# 🚀 Docker Hub 配置快速指南

## 第一步：配置 GitHub Secrets

1. 访问你的 GitHub 仓库
2. **Settings** → **Secrets and variables** → **Actions**
3. 添加两个 secrets：

```
DOCKER_HUB_USERNAME: your-dockerhub-username
DOCKER_HUB_TOKEN: <paste-your-token-here>
```

获取 Token: https://hub.docker.com/settings/security → **New Access Token**

---

## 第二步：触发部署

```bash
# 打 tag 自动部署
git tag v1.0.0
git push origin v1.0.0
```

镜像会自动推送到: `your-username/tdd-marketing-automation:v1.0.0`

---

## 第三步：在服务器上运行

### 方式 1: Docker Compose（推荐）

```bash
# 1. 创建配置文件
cp .env.example .env
vim .env  # 修改 DOCKER_HUB_USERNAME 和数据库密码

# 2. 启动服务
docker-compose up -d

# 3. 查看日志
docker-compose logs -f app

# 4. 访问应用
curl http://localhost:8080/actuator/health
```

### 方式 2: Docker Run

```bash
docker run -d \
  --name tdd-ma-app \
  -p 8080:8080 \
  -e DB_URL=your-mysql-host \
  -e DB_USERNAME=tdd_user \
  -e DB_PASSWORD=your_password \
  -e REDIS_HOST=your-redis-host \
  your-username/tdd-marketing-automation:latest
```

---

## 环境变量（必填）

| 变量 | 说明 | 示例 |
|------|------|------|
| DB_URL | MySQL 地址 | `mysql.example.com` |
| DB_USERNAME | 数据库用户 | `tdd_user` |
| DB_PASSWORD | 数据库密码 | `your_password` |
| REDIS_HOST | Redis 地址 | `redis.example.com` |

完整文档: [docs/DOCKER_DEPLOYMENT.md](docs/DOCKER_DEPLOYMENT.md)
