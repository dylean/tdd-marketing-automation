# 🚀 Sealos 部署指南

本文档详细介绍如何将 TDD Marketing Automation 项目部署到 Sealos 云平台。

---

## 📋 目录

1. [Sealos 简介](#sealos-简介)
2. [前置准备](#前置准备)
3. [快速开始](#快速开始)
4. [手动部署](#手动部署)
5. [自动部署（CI/CD）](#自动部署cicd)
6. [配置管理](#配置管理)
7. [监控和日志](#监控和日志)
8. [故障排查](#故障排查)
9. [成本优化](#成本优化)

---

## 🌟 Sealos 简介

### 什么是 Sealos？

Sealos 是一个基于 Kubernetes 的云操作系统，它将复杂的 K8s 操作简化为直观的界面操作。

### 核心优势

| 特性 | 说明 |
|------|------|
| 🎯 **开箱即用** | 无需懂 Kubernetes，可视化操作 |
| 💰 **按量付费** | 只为实际使用的资源付费，比传统云服务便宜 30%-50% |
| 🔒 **多租户隔离** | 每个用户独立的 Namespace，安全可靠 |
| 📦 **一站式服务** | 应用、数据库、存储、函数计算一应俱全 |
| 🚀 **自动扩缩容** | 根据负载自动调整实例数量（HPA） |
| 🌐 **自动 HTTPS** | 自动配置域名和 SSL 证书 |

### 架构对比

#### 传统云服务部署

```
你的应用
  ↓
Docker 容器
  ↓
云服务器（ECS/EC2）
  ↓
手动配置负载均衡
  ↓
手动配置数据库
  ↓
手动配置域名和 HTTPS
```

💸 **成本**：~¥500/月（最低配置）
⏱️ **部署时间**：2-4 小时

#### Sealos 部署

```
你的应用
  ↓
Docker 镜像
  ↓
Sealos 一键部署
  ↓
自动配置全部服务
```

💸 **成本**：~¥100-200/月（按实际使用量）
⏱️ **部署时间**：5-10 分钟

---

## 🎯 前置准备

### 1. 注册 Sealos 账号

访问 [Sealos 官网](https://sealos.io) 注册账号并登录。

### 2. 准备 Docker 镜像

确保你的 Docker 镜像已推送到 Docker Hub：

```bash
# 登录 Docker Hub
docker login

# 构建镜像
./gradlew bootJar
docker build -t your-username/tdd-marketing-automation:latest .

# 推送镜像
docker push your-username/tdd-marketing-automation:latest
```

### 3. 准备部署配置文件

项目已包含 Sealos 部署配置文件：

```
sealos/
├── app-deployment.yaml        # 应用部署配置
└── database-deployment.yaml   # 数据库部署配置
```

---

## 🚀 快速开始

### 方式一：使用 Sealos Web 界面（推荐新手）

#### 步骤 1：创建数据库

1. 登录 Sealos 控制台
2. 点击 **数据库** → **创建数据库**
3. 选择 **MySQL 8.0**
4. 配置：
   - 数据库名称：`marketing_automation`
   - 用户名：`root`
   - 密码：设置一个强密码
   - 存储空间：10Gi
5. 点击 **创建**，等待数据库就绪

6. 重复上述步骤创建 **Redis 7.0** 数据库

#### 步骤 2：部署应用

1. 点击 **应用管理** → **创建应用**
2. 填写基本信息：
   - 应用名称：`tdd-marketing-automation`
   - 镜像地址：`your-username/tdd-marketing-automation:latest`
   - CPU：0.5 核
   - 内存：1Gi
   - 实例数量：2
3. 添加环境变量：

```bash
SPRING_PROFILES_ACTIVE=prod
SPRING_DATASOURCE_URL=jdbc:mysql://mysql-service:3306/marketing_automation?useSSL=false&serverTimezone=Asia/Shanghai
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=<你的MySQL密码>
SPRING_DATA_REDIS_HOST=redis-service
SPRING_DATA_REDIS_PORT=6379
SPRING_DATA_REDIS_PASSWORD=<你的Redis密码>
```

4. 配置健康检查：
   - 路径：`/actuator/health`
   - 端口：8080

5. 点击 **部署**

#### 步骤 3：配置域名

1. 应用部署成功后，点击 **网络** → **添加域名**
2. 输入你的域名（如 `tdd-ma.example.com`）
3. Sealos 会自动配置 HTTPS 证书
4. 等待 1-2 分钟，访问你的域名

🎉 **完成！** 你的应用已在 Sealos 上运行！

---

### 方式二：使用 kubectl 命令行（推荐进阶用户）

#### 步骤 1：安装 kubectl

```bash
# macOS
brew install kubectl

# Linux
curl -LO "https://dl.k8s.io/release/$(curl -L -s https://dl.k8s.io/release/stable.txt)/bin/linux/amd64/kubectl"
chmod +x kubectl
sudo mv kubectl /usr/local/bin/

# Windows
choco install kubernetes-cli
```

#### 步骤 2：获取 kubeconfig

1. 登录 Sealos 控制台
2. 点击右上角用户头像 → **账户设置**
3. 找到 **Kubeconfig** 部分
4. 点击 **下载 kubeconfig**
5. 保存到 `~/.kube/config`

```bash
mkdir -p ~/.kube
mv ~/Downloads/kubeconfig ~/.kube/config
chmod 600 ~/.kube/config
```

#### 步骤 3：验证连接

```bash
kubectl get nodes
```

如果看到节点列表，说明连接成功。

#### 步骤 4：修改部署配置

编辑 `sealos/app-deployment.yaml`，替换以下内容：

```yaml
# 第 20 行左右，修改 Docker 镜像地址
image: your-username/tdd-marketing-automation:latest
```

编辑 `sealos/database-deployment.yaml`，修改密码：

```yaml
# 第 10 行左右
stringData:
  username: "root"
  password: "your-strong-mysql-password"     # 修改这里
  root-password: "your-strong-mysql-password" # 修改这里

# 第 24 行左右
stringData:
  password: "your-strong-redis-password"     # 修改这里
```

#### 步骤 5：部署数据库

```bash
# 部署数据库
kubectl apply -f sealos/database-deployment.yaml

# 等待数据库就绪
kubectl wait --for=condition=ready pod -l app=mysql -n tdd-ma --timeout=300s
kubectl wait --for=condition=ready pod -l app=redis -n tdd-ma --timeout=300s

# 验证数据库状态
kubectl get pods -n tdd-ma
```

#### 步骤 6：部署应用

```bash
# 部署应用
kubectl apply -f sealos/app-deployment.yaml

# 查看部署状态
kubectl rollout status deployment/tdd-marketing-automation -n tdd-ma

# 查看 Pods 状态
kubectl get pods -n tdd-ma

# 查看服务状态
kubectl get services -n tdd-ma

# 查看 Ingress（域名）
kubectl get ingress -n tdd-ma
```

#### 步骤 7：配置域名

编辑 `sealos/app-deployment.yaml` 中的 Ingress 部分：

```yaml
spec:
  tls:
  - hosts:
    - your-domain.com  # 修改为你的域名
    secretName: tdd-ma-tls
  rules:
  - host: your-domain.com  # 修改为你的域名
```

更新配置：

```bash
kubectl apply -f sealos/app-deployment.yaml
```

在你的 DNS 提供商处添加 A 记录，指向 Sealos 提供的 IP 地址：

```bash
# 获取 Ingress IP
kubectl get ingress -n tdd-ma
```

等待 1-2 分钟，访问 `https://your-domain.com`

🎉 **完成！**

---

## 🤖 自动部署（CI/CD）

### GitHub Actions 自动部署到 Sealos

项目已配置 GitHub Actions，每次推送到 `main` 分支会自动部署到 Sealos。

#### 配置 GitHub Secrets

在 GitHub 仓库中配置以下 Secrets：

1. 进入仓库 → **Settings** → **Secrets and variables** → **Actions**
2. 点击 **New repository secret**，添加以下 Secrets：

| Secret 名称 | 说明 | 获取方式 |
|------------|------|----------|
| `DOCKER_HUB_USERNAME` | Docker Hub 用户名 | Docker Hub 账号 |
| `DOCKER_HUB_TOKEN` | Docker Hub 访问令牌 | Docker Hub → Account Settings → Security |
| `SEALOS_KUBECONFIG` | Sealos kubeconfig（Base64 编码） | 见下方说明 |
| `MYSQL_PASSWORD` | MySQL 密码 | 自定义强密码 |
| `MYSQL_ROOT_PASSWORD` | MySQL root 密码 | 自定义强密码 |
| `REDIS_PASSWORD` | Redis 密码 | 自定义强密码 |

#### 获取 SEALOS_KUBECONFIG

```bash
# 1. 下载 kubeconfig（从 Sealos 控制台）
# 2. Base64 编码
cat ~/Downloads/kubeconfig | base64 -w 0  # Linux
cat ~/Downloads/kubeconfig | base64        # macOS

# 3. 复制输出的 Base64 字符串到 GitHub Secret
```

#### 触发部署

```bash
# 推送代码到 main 分支
git add .
git commit -m "feat: deploy to sealos"
git push origin main
```

GitHub Actions 会自动执行以下步骤：

1. 🧪 **CI 阶段**：编译、测试、代码质量检查
2. 🐳 **Docker 镜像**：构建并推送到 Docker Hub
3. ☁️ **Sealos 部署**：部署到 Sealos 集群

查看部署进度：

```
https://github.com/your-username/tdd-marketing-automation/actions
```

---

## ⚙️ 配置管理

### 环境变量配置

#### 方式一：通过 Secret 管理（推荐）

```bash
# 创建 Secret
kubectl create secret generic app-config -n tdd-ma \
  --from-literal=MYSQL_PASSWORD=your-password \
  --from-literal=REDIS_PASSWORD=your-password

# 在 Deployment 中引用
env:
- name: SPRING_DATASOURCE_PASSWORD
  valueFrom:
    secretKeyRef:
      name: app-config
      key: MYSQL_PASSWORD
```

#### 方式二：通过 ConfigMap 管理（非敏感配置）

```bash
# 创建 ConfigMap
kubectl create configmap app-config -n tdd-ma \
  --from-literal=SPRING_PROFILES_ACTIVE=prod \
  --from-literal=AUDIENCE_SERVICE_URL=https://api.example.com

# 在 Deployment 中引用
env:
- name: SPRING_PROFILES_ACTIVE
  valueFrom:
    configMapKeyRef:
      name: app-config
      key: SPRING_PROFILES_ACTIVE
```

### 更新配置

```bash
# 更新 Secret
kubectl create secret generic app-config -n tdd-ma \
  --from-literal=MYSQL_PASSWORD=new-password \
  --dry-run=client -o yaml | kubectl apply -f -

# 重启应用以应用新配置
kubectl rollout restart deployment/tdd-marketing-automation -n tdd-ma
```

---

## 📊 监控和日志

### 查看应用日志

```bash
# 查看所有 Pod 日志
kubectl logs -l app=tdd-ma -n tdd-ma

# 查看特定 Pod 日志
kubectl logs <pod-name> -n tdd-ma

# 实时查看日志
kubectl logs -f deployment/tdd-marketing-automation -n tdd-ma

# 查看最近 100 行日志
kubectl logs deployment/tdd-marketing-automation -n tdd-ma --tail=100
```

### 查看应用状态

```bash
# 查看 Pods 状态
kubectl get pods -n tdd-ma

# 查看 Deployment 状态
kubectl get deployment -n tdd-ma

# 查看详细信息
kubectl describe pod <pod-name> -n tdd-ma

# 查看 HPA（自动扩缩容）状态
kubectl get hpa -n tdd-ma
```

### 进入容器调试

```bash
# 进入 Pod
kubectl exec -it <pod-name> -n tdd-ma -- /bin/sh

# 查看应用进程
ps aux | grep java

# 查看内存使用
free -m

# 查看磁盘使用
df -h
```

### Sealos 监控面板

Sealos 提供了内置的监控面板：

1. 登录 Sealos 控制台
2. 点击 **应用管理** → 选择你的应用
3. 点击 **监控** 标签

可以看到：
- CPU 使用率
- 内存使用率
- 网络流量
- Pod 数量变化

---

## 🔧 故障排查

### 🛠️ 快速诊断工具

我们提供了一个自动诊断脚本，可以快速检查部署状态和常见问题：

```bash
# 自动检测 namespace
./scripts/diagnose-sealos-deployment.sh

# 或指定 namespace
./scripts/diagnose-sealos-deployment.sh ns-l34pu8d4
```

**诊断脚本功能**：
- ✅ 检查 Namespace、Deployment、Pod、Service、Ingress 状态
- ✅ 显示 Pod 详细日志（最近100行）
- ✅ 显示最近的 Kubernetes Events
- ✅ 自动诊断常见问题（镜像拉取失败、健康检查失败等）
- ✅ 提供快速操作命令

**推荐使用场景**：
- 部署后验证状态
- 部署失败时快速定位问题
- 定期检查应用健康状态

---

### 问题 1：Pod 一直处于 Pending 状态

**原因**：资源不足或 PVC 无法绑定

**解决方案**：

```bash
# 查看 Pod 详情
kubectl describe pod <pod-name> -n tdd-ma

# 查看 Events 部分，通常会有错误提示
# 如果是资源不足，考虑降低资源请求或升级 Sealos 套餐
```

### 问题 2：Pod 一直重启（CrashLoopBackOff）

**原因**：应用启动失败

**解决方案**：

```bash
# 查看日志
kubectl logs <pod-name> -n tdd-ma --previous

# 常见原因：
# 1. 数据库连接失败 → 检查数据库服务是否就绪
# 2. 配置错误 → 检查环境变量
# 3. JVM 内存不足 → 增加内存限制
```

### 问题 3：无法访问应用（502/504 错误）

**原因**：应用未就绪或 Ingress 配置错误

**解决方案**：

```bash
# 1. 检查 Pod 是否就绪
kubectl get pods -n tdd-ma

# 2. 检查 Service
kubectl get svc -n tdd-ma

# 3. 检查 Ingress
kubectl get ingress -n tdd-ma

# 4. 测试 Service 连通性
kubectl run -it --rm debug --image=curlimages/curl --restart=Never -n tdd-ma -- \
  curl http://tdd-ma-service:8080/actuator/health

# 5. 检查健康检查
kubectl describe pod <pod-name> -n tdd-ma | grep -A 10 "Liveness\|Readiness"
```

### 问题 4：数据库连接失败

**原因**：数据库服务未就绪或密码错误

**解决方案**：

```bash
# 1. 检查数据库 Pod 状态
kubectl get pods -n tdd-ma | grep mysql

# 2. 检查数据库日志
kubectl logs -l app=mysql -n tdd-ma

# 3. 测试数据库连接
kubectl run -it --rm mysql-client --image=mysql:8.0 --restart=Never -n tdd-ma -- \
  mysql -h mysql-service -u root -p

# 4. 检查 Secret
kubectl get secret mysql-secret -n tdd-ma -o yaml
```

### 问题 5：自动扩缩容不工作

**原因**：Metrics Server 未安装或 HPA 配置错误

**解决方案**：

```bash
# 1. 检查 Metrics Server
kubectl get deployment metrics-server -n kube-system

# 2. 检查 HPA 状态
kubectl get hpa -n tdd-ma

# 3. 查看 HPA 详情
kubectl describe hpa tdd-ma-hpa -n tdd-ma

# 4. 手动测试扩容
kubectl scale deployment tdd-marketing-automation --replicas=5 -n tdd-ma
```

---

## 💰 成本优化

### 1. 调整资源配置

根据实际使用情况调整 CPU 和内存：

```yaml
resources:
  requests:
    memory: "256Mi"  # 从 512Mi 降低
    cpu: "100m"      # 从 250m 降低
  limits:
    memory: "512Mi"  # 从 1Gi 降低
    cpu: "500m"      # 从 1000m 降低
```

### 2. 优化 HPA 配置

```yaml
spec:
  minReplicas: 1  # 从 2 降低（开发环境）
  maxReplicas: 5  # 从 10 降低
  metrics:
  - type: Resource
    resource:
      name: cpu
      target:
        type: Utilization
        averageUtilization: 80  # 从 70 提高
```

### 3. 使用 Sealos 托管数据库

如果使用 Sealos 提供的数据库服务（而不是自己部署 MySQL），可以享受：
- 自动备份
- 一键恢复
- 更低的成本

### 4. 定时扩缩容

对于流量有明显波峰波谷的应用，可以使用 CronJob 定时调整 replicas：

```yaml
# 工作日白天增加实例
apiVersion: batch/v1
kind: CronJob
metadata:
  name: scale-up
  namespace: tdd-ma
spec:
  schedule: "0 8 * * 1-5"  # 周一到周五 8:00
  jobTemplate:
    spec:
      template:
        spec:
          containers:
          - name: kubectl
            image: bitnami/kubectl
            command:
            - kubectl
            - scale
            - deployment/tdd-marketing-automation
            - --replicas=3
            - -n
            - tdd-ma
          restartPolicy: OnFailure

# 夜间减少实例
---
apiVersion: batch/v1
kind: CronJob
metadata:
  name: scale-down
  namespace: tdd-ma
spec:
  schedule: "0 23 * * *"  # 每天 23:00
  jobTemplate:
    spec:
      template:
        spec:
          containers:
          - name: kubectl
            image: bitnami/kubectl
            command:
            - kubectl
            - scale
            - deployment/tdd-marketing-automation
            - --replicas=1
            - -n
            - tdd-ma
          restartPolicy: OnFailure
```

### 成本估算

| 资源 | 配置 | 月费用（估算） |
|------|------|---------------|
| 应用实例 x2 | 0.5核/1Gi | ¥80-120 |
| MySQL | 0.5核/1Gi/10G存储 | ¥40-60 |
| Redis | 0.25核/512Mi | ¥20-30 |
| 网络流量 | 100GB | ¥10-20 |
| **总计** | - | **¥150-230** |

对比传统云服务器（ECS/EC2）：~¥500/月，**节省约 50%-60%**

---

## 📚 参考资源

- [Sealos 官方文档](https://sealos.io/docs)
- [Kubernetes 官方文档](https://kubernetes.io/docs)
- [kubectl 命令速查表](https://kubernetes.io/docs/reference/kubectl/cheatsheet/)
- [项目 GitHub 仓库](https://github.com/dylean/tdd-marketing-automation)

---

## 🆘 获取帮助

如果遇到问题：

1. **查看日志**：`kubectl logs -f deployment/tdd-marketing-automation -n tdd-ma`
2. **Sealos 社区**：[https://forum.sealos.io](https://forum.sealos.io)
3. **提 Issue**：在 GitHub 仓库提 Issue
4. **Sealos 官方支持**：登录 Sealos 控制台，点击右下角客服图标

---

🎉 **祝部署顺利！**
