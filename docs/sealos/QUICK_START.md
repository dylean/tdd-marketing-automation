# 🚀 Sealos 快速部署指南（5 分钟）

> 最快速度将应用部署到 Sealos 云平台

---

## 📋 前置条件

- [x] 已注册 [Sealos](https://sealos.io) 账号
- [x] Docker 镜像已推送到 Docker Hub
- [x] 已安装 `kubectl` 命令行工具

---

## 🎯 部署步骤

### 步骤 1: 获取 kubeconfig (1 分钟)

1. 登录 [Sealos 控制台](https://cloud.sealos.io)
2. 点击右上角头像 → **账户设置**
3. 找到 **Kubeconfig** → 点击 **下载**
4. 保存到本地：

```bash
mkdir -p ~/.kube
mv ~/Downloads/kubeconfig ~/.kube/config
chmod 600 ~/.kube/config

# 验证连接
kubectl get nodes
```

✅ 如果能看到节点列表，说明连接成功！

---

### 步骤 2: 配置密码 (1 分钟)

```bash
cd sealos

# 复制密码模板
cp secrets-template.yaml secrets.yaml

# 编辑 secrets.yaml，修改以下三处密码:
# 1. CHANGE_ME_MYSQL_PASSWORD
# 2. CHANGE_ME_MYSQL_ROOT_PASSWORD
# 3. CHANGE_ME_REDIS_PASSWORD

# 使用你喜欢的编辑器
vim secrets.yaml
# 或
code secrets.yaml
```

**密码要求**：
- 长度至少 12 位
- 包含大小写字母、数字、特殊字符
- 示例：`MyS3cr3t!P@ssw0rd#2024`

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
# 创建 namespace
kubectl create namespace tdd-ma

# 部署密码配置
kubectl apply -f secrets.yaml

# 部署数据库
kubectl apply -f database-deployment.yaml

# 等待数据库就绪（大约 1 分钟）
kubectl wait --for=condition=ready pod -l app=mysql -n tdd-ma --timeout=300s
kubectl wait --for=condition=ready pod -l app=redis -n tdd-ma --timeout=300s

# 部署应用
kubectl apply -f app-deployment.yaml

# 等待应用就绪（大约 1 分钟）
kubectl rollout status deployment/tdd-marketing-automation -n tdd-ma --timeout=300s
```

---

### 步骤 5: 查看部署状态 (30 秒)

```bash
# 查看所有 Pods
kubectl get pods -n tdd-ma

# 预期输出:
# NAME                                        READY   STATUS    RESTARTS   AGE
# mysql-xxxxxxxxx-xxxxx                       1/1     Running   0          2m
# redis-xxxxxxxxx-xxxxx                       1/1     Running   0          2m
# tdd-marketing-automation-xxxxxxxxx-xxxxx    1/1     Running   0          1m
# tdd-marketing-automation-xxxxxxxxx-xxxxx    1/1     Running   0          1m

# 查看服务
kubectl get services -n tdd-ma

# 查看 Ingress (域名)
kubectl get ingress -n tdd-ma
```

---

### 步骤 6: 配置域名（可选）

#### 方式 A: 使用 Sealos 提供的临时域名

Sealos 会自动分配一个临时域名，查看：

```bash
kubectl get ingress tdd-ma-ingress -n tdd-ma -o jsonpath='{.spec.rules[0].host}'
```

直接访问这个域名即可！

#### 方式 B: 使用自己的域名

1. 编辑 `app-deployment.yaml`，修改 Ingress 部分（第 157-180 行）：

```yaml
spec:
  tls:
  - hosts:
    - your-domain.com  # 改成你的域名
    secretName: tdd-ma-tls
  rules:
  - host: your-domain.com  # 改成你的域名
```

2. 更新配置：

```bash
kubectl apply -f app-deployment.yaml
```

3. 在你的 DNS 提供商添加 A 记录：

```bash
# 获取 Sealos Ingress IP
kubectl get ingress tdd-ma-ingress -n tdd-ma

# 添加 DNS A 记录:
# 类型: A
# 主机: @ 或 www
# 值: <上面获取的 IP>
# TTL: 600
```

4. 等待 1-2 分钟，访问你的域名

---

## 🎉 完成！

现在访问你的域名，你应该能看到应用正在运行！

测试健康检查：

```bash
# 替换为你的域名
curl https://your-domain.com/actuator/health

# 预期输出:
# {"status":"UP"}
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

# 删除所有资源（危险操作！）
kubectl delete namespace tdd-ma
```

---

## 🔄 更新应用

当代码更新后，重新部署：

```bash
# 1. 推送新的 Docker 镜像
docker build -t your-username/tdd-marketing-automation:latest .
docker push your-username/tdd-marketing-automation:latest

# 2. 重启应用（Kubernetes 会自动拉取最新镜像）
kubectl rollout restart deployment/tdd-marketing-automation -n tdd-ma

# 3. 查看更新进度
kubectl rollout status deployment/tdd-marketing-automation -n tdd-ma
```

或者直接推送到 GitHub，让 CI/CD 自动部署（推荐）！

---

## ⚠️ 故障排查

### 问题: Pod 一直处于 Pending

```bash
# 查看详情
kubectl describe pod <pod-name> -n tdd-ma

# 通常原因: 资源不足
# 解决方案: 降低 resources.requests 或升级 Sealos 套餐
```

### 问题: Pod 一直重启（CrashLoopBackOff）

```bash
# 查看日志
kubectl logs <pod-name> -n tdd-ma --previous

# 通常原因: 
# 1. 数据库连接失败 → 检查密码是否正确
# 2. 端口冲突 → 检查是否有其他服务占用 8080
# 3. 内存不足 → 增加 resources.limits.memory
```

### 问题: 无法访问应用（502/504）

```bash
# 1. 检查 Pod 是否就绪
kubectl get pods -n tdd-ma

# 2. 测试内部连通性
kubectl run -it --rm debug --image=curlimages/curl --restart=Never -n tdd-ma -- \
  curl http://tdd-ma-service:8080/actuator/health

# 3. 检查 Ingress
kubectl describe ingress tdd-ma-ingress -n tdd-ma
```

---

## 📚 下一步

- 📖 阅读 [完整部署文档](./SEALOS_DEPLOYMENT.md)
- 🤖 配置 [GitHub Actions 自动部署](../.github/workflows/ci-cd.yml)
- 📊 学习如何 [监控和日志管理](./SEALOS_DEPLOYMENT.md#监控和日志)
- 💰 了解 [成本优化技巧](./SEALOS_DEPLOYMENT.md#成本优化)

---

🎊 **恭喜你！应用已成功部署到 Sealos！**
