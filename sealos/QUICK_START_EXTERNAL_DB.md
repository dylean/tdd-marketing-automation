# 🚀 Sealos 快速部署指南（使用外部数据库）

> 5 分钟将应用部署到 Sealos，连接你现有的 MySQL 和 Redis 数据库

---

## 📋 前置条件

- [x] 已注册 [Sealos](https://sealos.io) 账号
- [x] Docker 镜像已推送到 Docker Hub
- [x] 已安装 `kubectl` 命令行工具
- [x] **已有可用的 MySQL 8.0+ 数据库**
- [x] **已有可用的 Redis 7.0+ 实例**

---

## 🎯 部署步骤

### 步骤 1: 获取 kubeconfig (1 分钟)

```bash
# 1. 登录 Sealos 控制台 → 右上角头像 → 账户设置 → 下载 Kubeconfig
mkdir -p ~/.kube
mv ~/Downloads/kubeconfig ~/.kube/config
chmod 600 ~/.kube/config

# 2. 验证连接
kubectl get nodes
```

✅ 如果能看到节点列表，说明连接成功！

---

### 步骤 2: 配置外部数据库连接 (2 分钟)

```bash
cd sealos

# 复制外部数据库配置模板
cp external-db-secret-template.yaml external-db-secret.yaml

# 编辑配置文件
vim external-db-secret.yaml
```

**修改以下内容**（替换为你的实际数据库信息）：

```yaml
stringData:
  # MySQL 配置
  mysql-url: "jdbc:mysql://your-mysql-host:3306/marketing_automation?useSSL=false&serverTimezone=Asia/Shanghai&characterEncoding=utf8"
  mysql-username: "your_mysql_user"
  mysql-password: "your_mysql_password"
  
  # Redis 配置
  redis-host: "your-redis-host"
  redis-port: "6379"
  redis-password: "your_redis_password"  # 如果没有密码，设置为 ""
```

**配置示例**：

```yaml
stringData:
  mysql-url: "jdbc:mysql://10.0.1.100:3306/marketing_automation?useSSL=false&serverTimezone=Asia/Shanghai&characterEncoding=utf8"
  mysql-username: "tdd_ma_user"
  mysql-password: "MyS3cr3tP@ss!"
  
  redis-host: "10.0.1.101"
  redis-port: "6379"
  redis-password: "R3d!sP@ss"
```

---

### 步骤 3: 修改 Docker 镜像地址 (30 秒)

编辑 `app-deployment.yaml`，找到第 20 行左右：

```yaml
# 修改前:
image: ${DOCKER_HUB_USERNAME}/tdd-marketing-automation:latest

# 修改后（替换为你的 Docker Hub 用户名）:
image: your-username/tdd-marketing-automation:latest
```

---

### 步骤 4: 一键部署 (2 分钟)

```bash
# 1. 创建 namespace
kubectl create namespace tdd-ma

# 2. 应用外部数据库配置
kubectl apply -f external-db-secret.yaml

# 3. 验证 Secret 创建成功
kubectl get secret external-db-secret -n tdd-ma

# 4. 部署应用
kubectl apply -f app-deployment.yaml

# 5. 等待应用就绪（大约 1 分钟）
kubectl rollout status deployment/tdd-marketing-automation -n tdd-ma --timeout=300s
```

---

### 步骤 5: 查看部署状态 (30 秒)

```bash
# 查看所有 Pods
kubectl get pods -n tdd-ma

# 预期输出:
# NAME                                        READY   STATUS    RESTARTS   AGE
# tdd-marketing-automation-xxxxxxxxx-xxxxx    1/1     Running   0          1m
# tdd-marketing-automation-xxxxxxxxx-xxxxx    1/1     Running   0          1m

# 查看应用日志
kubectl logs -f deployment/tdd-marketing-automation -n tdd-ma

# 查看 Ingress (域名)
kubectl get ingress -n tdd-ma
```

---

### 步骤 6: 验证数据库连接（1 分钟）

```bash
# 查看应用日志，确认启动成功
kubectl logs deployment/tdd-marketing-automation -n tdd-ma | grep "Started"

# 预期输出:
# Started MarketingAutomationApplication in X.XXX seconds

# 测试健康检查
INGRESS_HOST=$(kubectl get ingress tdd-ma-ingress -n tdd-ma -o jsonpath='{.spec.rules[0].host}')
curl https://${INGRESS_HOST}/actuator/health

# 预期输出:
# {"status":"UP"}
```

---

## 🎉 完成！

现在访问你的域名或 Ingress 地址，应用已经在运行了！

**获取访问地址**：

```bash
kubectl get ingress tdd-ma-ingress -n tdd-ma
```

---

## 📋 常用命令

```bash
# 查看应用日志
kubectl logs -f deployment/tdd-marketing-automation -n tdd-ma

# 重启应用（拉取最新镜像）
kubectl rollout restart deployment/tdd-marketing-automation -n tdd-ma

# 扩容到 5 个实例
kubectl scale deployment tdd-marketing-automation --replicas=5 -n tdd-ma

# 缩容到 1 个实例
kubectl scale deployment tdd-marketing-automation --replicas=1 -n tdd-ma

# 查看 HPA（自动扩缩容）状态
kubectl get hpa -n tdd-ma

# 进入容器调试
kubectl exec -it <pod-name> -n tdd-ma -- /bin/sh

# 测试数据库连通性（在容器内）
nc -zv your-mysql-host 3306
nc -zv your-redis-host 6379
```

---

## 🔄 更新应用

当代码更新后，重新部署：

```bash
# 方式 1: 推送到 GitHub，让 CI/CD 自动部署（推荐）
git push origin main

# 方式 2: 手动推送镜像并重启
docker build -t your-username/tdd-marketing-automation:latest .
docker push your-username/tdd-marketing-automation:latest
kubectl rollout restart deployment/tdd-marketing-automation -n tdd-ma
```

---

## ⚠️ 故障排查

### 问题: 应用无法连接 MySQL

```bash
# 1. 查看错误日志
kubectl logs deployment/tdd-marketing-automation -n tdd-ma | grep -i "mysql\|connection"

# 2. 检查 Secret 配置
kubectl get secret external-db-secret -n tdd-ma -o yaml

# 3. 测试网络连通性
kubectl run test-mysql --image=mysql:8.0 -it --rm -n tdd-ma -- \
  mysql -h your-mysql-host -P 3306 -u your_user -p

# 4. 检查 MySQL 防火墙规则
# 确保 Sealos 集群 IP 可以访问 MySQL
```

### 问题: 应用无法连接 Redis

```bash
# 1. 查看错误日志
kubectl logs deployment/tdd-marketing-automation -n tdd-ma | grep -i "redis"

# 2. 测试 Redis 连接
kubectl run test-redis --image=redis:7.0 -it --rm -n tdd-ma -- \
  redis-cli -h your-redis-host -p 6379 -a your_password ping

# 3. 检查 Redis 配置
# 确保 Redis bind 地址允许远程连接
```

### 问题: Pod 一直重启

```bash
# 查看 Pod 事件
kubectl describe pod <pod-name> -n tdd-ma

# 查看上一次的日志
kubectl logs <pod-name> -n tdd-ma --previous

# 常见原因:
# 1. 数据库连接失败 → 检查数据库地址、用户名、密码
# 2. 内存不足 → 增加 resources.limits.memory
# 3. 健康检查失败 → 延长 initialDelaySeconds
```

---

## 📚 下一步

- 📖 阅读 [外部数据库配置详细指南](./EXTERNAL_DB_SETUP.md)
- 🤖 配置 [GitHub Actions 自动部署](.github/workflows/ci-cd.yml)
- 📊 学习如何 [监控和日志管理](../docs/SEALOS_DEPLOYMENT.md#监控和日志)

---

## 💡 提示

- 🔐 **安全第一**：不要将 `external-db-secret.yaml` 提交到 Git
- 📊 **监控**：使用 `kubectl get pods -n tdd-ma` 随时查看状态
- 🔍 **调试**：查看日志是排查问题的第一步
- 🌐 **网络**：确保数据库可以从 Sealos 集群访问

---

🎊 **恭喜你！应用已成功部署到 Sealos，并连接到你的外部数据库！**
