# 📦 Sealos 部署配置

本目录包含将应用部署到 Sealos 云平台所需的所有配置文件。

---

## 📁 文件说明

| 文件 | 说明 |
|------|------|
| `app-deployment.yaml` | 应用部署配置（Deployment、Service、Ingress、HPA） |
| `database-deployment.yaml` | 数据库部署配置（MySQL、Redis） |
| `secrets-template.yaml` | 密码配置模板（需复制为 `secrets.yaml` 并填写实际密码） |
| `QUICK_START.md` | 5 分钟快速部署指南 |

---

## 🚀 快速开始

### 最快速度部署（5 分钟）

```bash
# 1. 阅读快速指南
cat QUICK_START.md

# 2. 复制并配置密码
cp secrets-template.yaml secrets.yaml
vim secrets.yaml  # 修改密码

# 3. 一键部署
kubectl create namespace tdd-ma
kubectl apply -f secrets.yaml
kubectl apply -f database-deployment.yaml
kubectl apply -f app-deployment.yaml

# 4. 查看状态
kubectl get pods -n tdd-ma
```

详细步骤请参考 [QUICK_START.md](./QUICK_START.md)

---

## 📖 完整文档

- **新手入门**: [快速部署指南](./QUICK_START.md) - 5 分钟快速上手
- **进阶使用**: [完整部署文档](../docs/SEALOS_DEPLOYMENT.md) - 详细配置和故障排查

---

## ⚙️ 配置说明

### 应用部署配置（app-deployment.yaml）

包含以下 Kubernetes 资源：

1. **Namespace**: `tdd-ma` - 独立的命名空间
2. **Deployment**: `tdd-marketing-automation` - 应用部署
   - 默认 2 个副本（高可用）
   - 资源限制：512Mi-1Gi 内存，250m-1000m CPU
   - 健康检查：liveness & readiness probes
3. **Service**: `tdd-ma-service` - 服务发现
   - ClusterIP 类型，端口 8080
4. **Ingress**: `tdd-ma-ingress` - 外网访问
   - 自动 HTTPS（Let's Encrypt）
   - 可配置自定义域名
5. **HorizontalPodAutoscaler**: `tdd-ma-hpa` - 自动扩缩容
   - 最小 2 个副本，最大 10 个副本
   - 基于 CPU (70%) 和内存 (80%) 自动扩缩容

### 数据库部署配置（database-deployment.yaml）

包含以下资源：

1. **MySQL 8.0**
   - 1 个副本（单实例）
   - 持久化存储：10Gi PVC
   - 自动创建数据库 `marketing_automation`
2. **Redis 7.0**
   - 1 个副本
   - AOF 持久化
   - 密码保护
3. **Secrets**
   - MySQL 用户名和密码
   - Redis 密码

### 密码配置（secrets-template.yaml）

模板文件，需要复制为 `secrets.yaml` 并填写实际密码：

```bash
cp secrets-template.yaml secrets.yaml
vim secrets.yaml
```

**注意**：`secrets.yaml` 已在 `.gitignore` 中，不会被提交到 Git。

---

## 🔐 安全最佳实践

1. ✅ **不要提交密码到 Git**
   - `secrets.yaml` 已在 `.gitignore` 中
   - 使用强密码（至少 12 位，包含大小写字母、数字、特殊字符）

2. ✅ **使用 Kubernetes Secrets 管理敏感信息**
   - 不要在 Deployment 中硬编码密码
   - 使用 `secretKeyRef` 引用 Secret

3. ✅ **定期轮换密码**
   ```bash
   # 更新密码
   kubectl create secret generic mysql-secret -n tdd-ma \
     --from-literal=password=new-password \
     --dry-run=client -o yaml | kubectl apply -f -
   
   # 重启应用
   kubectl rollout restart deployment/tdd-marketing-automation -n tdd-ma
   ```

4. ✅ **限制 RBAC 权限**
   - 只授予必要的权限
   - 不要使用 `cluster-admin` 角色

---

## 🔄 更新部署

### 更新应用版本

```bash
# 方式 1: 修改 image tag（推荐）
kubectl set image deployment/tdd-marketing-automation \
  app=your-username/tdd-marketing-automation:v1.2.0 \
  -n tdd-ma

# 方式 2: 重启 Deployment（拉取 latest）
kubectl rollout restart deployment/tdd-marketing-automation -n tdd-ma

# 查看更新进度
kubectl rollout status deployment/tdd-marketing-automation -n tdd-ma

# 回滚到上一个版本
kubectl rollout undo deployment/tdd-marketing-automation -n tdd-ma
```

### 更新配置

```bash
# 1. 修改 YAML 文件
vim app-deployment.yaml

# 2. 应用更新
kubectl apply -f app-deployment.yaml

# 3. 如果修改了环境变量或配置，需要重启
kubectl rollout restart deployment/tdd-marketing-automation -n tdd-ma
```

---

## 📊 监控和维护

### 查看资源使用情况

```bash
# 查看 Pod 状态
kubectl get pods -n tdd-ma

# 查看资源使用（需要 metrics-server）
kubectl top pods -n tdd-ma

# 查看 HPA 状态
kubectl get hpa -n tdd-ma

# 查看事件
kubectl get events -n tdd-ma --sort-by='.lastTimestamp'
```

### 查看日志

```bash
# 查看应用日志（实时）
kubectl logs -f deployment/tdd-marketing-automation -n tdd-ma

# 查看所有副本的日志
kubectl logs -l app=tdd-ma -n tdd-ma --tail=100

# 查看 MySQL 日志
kubectl logs -l app=mysql -n tdd-ma

# 查看 Redis 日志
kubectl logs -l app=redis -n tdd-ma
```

### 调试问题

```bash
# 进入容器
kubectl exec -it <pod-name> -n tdd-ma -- /bin/sh

# 端口转发到本地（用于本地调试）
kubectl port-forward svc/tdd-ma-service 8080:8080 -n tdd-ma

# 测试数据库连接
kubectl run -it --rm mysql-client --image=mysql:8.0 --restart=Never -n tdd-ma -- \
  mysql -h mysql-service -u root -p
```

---

## 🗑️ 清理资源

### 删除应用（保留数据库）

```bash
kubectl delete deployment tdd-marketing-automation -n tdd-ma
kubectl delete service tdd-ma-service -n tdd-ma
kubectl delete ingress tdd-ma-ingress -n tdd-ma
kubectl delete hpa tdd-ma-hpa -n tdd-ma
```

### 删除所有资源（包括数据库，危险！）

```bash
# ⚠️ 警告: 这会删除所有数据！
kubectl delete namespace tdd-ma
```

---

## 🛠️ 故障排查

### 常见问题

1. **Pod 一直 Pending**
   - 原因：资源不足或 PVC 无法绑定
   - 解决：`kubectl describe pod <pod-name> -n tdd-ma` 查看详情

2. **Pod 一直重启（CrashLoopBackOff）**
   - 原因：应用启动失败
   - 解决：`kubectl logs <pod-name> -n tdd-ma --previous` 查看日志

3. **无法访问应用（502/504）**
   - 原因：应用未就绪或 Ingress 配置错误
   - 解决：检查 Pod、Service、Ingress 状态

4. **数据库连接失败**
   - 原因：密码错误或数据库未就绪
   - 解决：检查 Secret 和数据库 Pod 状态

详细故障排查请参考 [完整文档](../docs/SEALOS_DEPLOYMENT.md#故障排查)

---

## 📚 参考资源

- [Sealos 官方文档](https://sealos.io/docs)
- [Kubernetes 官方文档](https://kubernetes.io/docs)
- [完整部署文档](../docs/SEALOS_DEPLOYMENT.md)
- [快速部署指南](./QUICK_START.md)

---

## 💡 提示

- 🔍 **部署前**：先阅读 [QUICK_START.md](./QUICK_START.md)
- 🔐 **安全第一**：不要提交 `secrets.yaml` 到 Git
- 📊 **监控**：使用 `kubectl get pods -n tdd-ma` 随时查看状态
- 💬 **遇到问题**：查看 [故障排查章节](../docs/SEALOS_DEPLOYMENT.md#故障排查)

---

🎉 **祝部署顺利！**
