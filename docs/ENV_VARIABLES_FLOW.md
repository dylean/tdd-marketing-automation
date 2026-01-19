# 🔄 环境变量传递流程详解

本文档详细说明环境变量如何从 GitHub Secrets 传递到 Spring Boot 应用。

---

## 📊 完整流程图

```
┌─────────────────────────────────────────────────────────────┐
│  1️⃣  GitHub Secrets (在 GitHub 仓库设置)                   │
│                                                              │
│  EXTERNAL_MYSQL_URL = jdbc:mysql://host:3306/db?...        │
│  EXTERNAL_MYSQL_USERNAME = root                             │
│  EXTERNAL_MYSQL_PASSWORD = MyP@ssw0rd                       │
│  EXTERNAL_REDIS_HOST = redis.example.com                    │
│  EXTERNAL_REDIS_PORT = 6379                                 │
│  EXTERNAL_REDIS_PASSWORD = R3d!sP@ss                        │
└─────────────┬───────────────────────────────────────────────┘
              ↓
┌─────────────────────────────────────────────────────────────┐
│  2️⃣  GitHub Actions (CI/CD 流水线)                         │
│                                                              │
│  从 Secrets 读取值，解析 JDBC URL，创建 K8s Secret:        │
│                                                              │
│  kubectl create secret generic external-db-secret \         │
│    --from-literal=mysql-host="host" \                       │
│    --from-literal=mysql-port="3306" \                       │
│    --from-literal=mysql-database="db" \                     │
│    --from-literal=mysql-username="root" \                   │
│    --from-literal=mysql-password="MyP@ssw0rd" \             │
│    --from-literal=redis-host="redis.example.com" \          │
│    --from-literal=redis-port="6379" \                       │
│    --from-literal=redis-password="R3d!sP@ss"                │
└─────────────┬───────────────────────────────────────────────┘
              ↓
┌─────────────────────────────────────────────────────────────┐
│  3️⃣  Kubernetes Secret (在 Sealos 集群中)                  │
│                                                              │
│  apiVersion: v1                                              │
│  kind: Secret                                                │
│  metadata:                                                   │
│    name: external-db-secret                                 │
│  data:                                                       │
│    mysql-host: aG9zdA==           (Base64 编码)             │
│    mysql-port: MzMwNg==                                     │
│    mysql-database: ZGI=                                     │
│    mysql-username: cm9vdA==                                 │
│    mysql-password: TXlQQHNzdzByZA==                         │
│    redis-host: cmVkaXMuZXhhbXBsZS5jb20=                     │
│    redis-port: NjM3OQ==                                     │
│    redis-password: UjNkIXNQQHNz                             │
└─────────────┬───────────────────────────────────────────────┘
              ↓
┌─────────────────────────────────────────────────────────────┐
│  4️⃣  Deployment (app-deployment.yaml)                       │
│                                                              │
│  env:                                                        │
│  - name: DB_URL                   ← 从 Secret 读取          │
│    valueFrom:                                                │
│      secretKeyRef:                                           │
│        name: external-db-secret                             │
│        key: mysql-host                                      │
│                                                              │
│  - name: DB_PORT                                            │
│    valueFrom:                                                │
│      secretKeyRef:                                           │
│        key: mysql-port                                      │
│                                                              │
│  - name: DB_NAME                                            │
│    valueFrom:                                                │
│      secretKeyRef:                                           │
│        key: mysql-database                                  │
│                                                              │
│  - name: DB_USERNAME                                        │
│    valueFrom:                                                │
│      secretKeyRef:                                           │
│        key: mysql-username                                  │
│                                                              │
│  - name: DB_PASSWORD                                        │
│    valueFrom:                                                │
│      secretKeyRef:                                           │
│        key: mysql-password                                  │
│                                                              │
│  ... (Redis 类似)                                           │
└─────────────┬───────────────────────────────────────────────┘
              ↓
┌─────────────────────────────────────────────────────────────┐
│  5️⃣  Pod 运行时环境变量 (容器内部)                         │
│                                                              │
│  export DB_URL="host"                                       │
│  export DB_PORT="3306"                                      │
│  export DB_NAME="db"                                        │
│  export DB_USERNAME="root"                                  │
│  export DB_PASSWORD="MyP@ssw0rd"                            │
│  export REDIS_HOST="redis.example.com"                     │
│  export REDIS_PORT="6379"                                   │
│  export REDIS_PASSWORD="R3d!sP@ss"                         │
└─────────────┬───────────────────────────────────────────────┘
              ↓
┌─────────────────────────────────────────────────────────────┐
│  6️⃣  Spring Boot 应用启动                                  │
│                                                              │
│  读取 application.yml，解析占位符 ${ENV_VAR:default}       │
└─────────────┬───────────────────────────────────────────────┘
              ↓
┌─────────────────────────────────────────────────────────────┐
│  7️⃣  application.yml (配置文件)                            │
│                                                              │
│  spring:                                                     │
│    datasource:                                               │
│      url: jdbc:mysql://${DB_URL}:${DB_PORT}/${DB_NAME}?...  │
│      username: ${DB_USERNAME:root}  ← 读取环境变量          │
│      password: ${DB_PASSWORD:root}                          │
│                                                              │
│    data:                                                     │
│      redis:                                                  │
│        host: ${REDIS_HOST:localhost}                        │
│        port: ${REDIS_PORT:6379}                             │
│        password: ${REDIS_PASSWORD:}                         │
└─────────────┬───────────────────────────────────────────────┘
              ↓
┌─────────────────────────────────────────────────────────────┐
│  8️⃣  最终生效的配置                                        │
│                                                              │
│  spring:                                                     │
│    datasource:                                               │
│      url: jdbc:mysql://host:3306/db?...                     │
│      username: root                                          │
│      password: MyP@ssw0rd                                   │
│                                                              │
│    data:                                                     │
│      redis:                                                  │
│        host: redis.example.com                              │
│        port: 6379                                            │
│        password: R3d!sP@ss                                  │
└─────────────────────────────────────────────────────────────┘
```

---

## 🔑 关键概念

### 1. GitHub Secrets

**作用**：安全存储敏感信息

**配置位置**：
```
https://github.com/your-username/tdd-marketing-automation/settings/secrets/actions
```

**示例**：
```
EXTERNAL_MYSQL_URL = jdbc:mysql://10.0.1.100:3306/marketing_automation?...
EXTERNAL_MYSQL_USERNAME = root
EXTERNAL_MYSQL_PASSWORD = MySecretPassword123!
```

---

### 2. GitHub Actions

**作用**：读取 Secrets，创建 Kubernetes Secret

**配置文件**：`.github/workflows/ci-cd.yml`

**关键代码**：
```yaml
- name: 配置外部数据库连接
  run: |
    # 从 JDBC URL 中提取信息
    MYSQL_URL="${{ secrets.EXTERNAL_MYSQL_URL }}"
    MYSQL_HOST=$(echo "$MYSQL_URL" | sed -n 's/.*:\/\/\([^:]*\).*/\1/p')
    
    # 创建 Kubernetes Secret
    kubectl create secret generic external-db-secret \
      --from-literal=mysql-host="$MYSQL_HOST" \
      --from-literal=mysql-username="${{ secrets.EXTERNAL_MYSQL_USERNAME }}" \
      ...
```

**工作流程**：
1. GitHub Actions 从 Secrets 读取值
2. 解析 JDBC URL，提取主机、端口、数据库名
3. 在 Kubernetes 集群中创建 Secret

---

### 3. Kubernetes Secret

**作用**：在 K8s 集群中存储敏感数据

**格式**：
```yaml
apiVersion: v1
kind: Secret
metadata:
  name: external-db-secret
  namespace: ns-l34pu8d4
type: Opaque
data:
  mysql-host: aG9zdA==           # Base64 编码的 "host"
  mysql-username: cm9vdA==       # Base64 编码的 "root"
  mysql-password: TXlQQHNzdzByZA==  # Base64 编码的密码
```

**查看 Secret**：
```bash
kubectl get secret external-db-secret -n ns-l34pu8d4 -o yaml
```

---

### 4. Deployment 环境变量

**作用**：从 Secret 读取值，设置为容器环境变量

**配置文件**：`sealos/app-deployment.yaml`

**关键配置**：
```yaml
env:
- name: DB_USERNAME              # ← 环境变量名称（与 application.yml 匹配）
  valueFrom:
    secretKeyRef:
      name: external-db-secret   # ← Secret 名称
      key: mysql-username        # ← Secret 中的 key
```

**工作流程**：
1. Kubernetes 从 Secret 读取 `mysql-username` 的值
2. Base64 解码
3. 设置为容器的环境变量 `DB_USERNAME`

---

### 5. 容器环境变量

**作用**：在容器内部可用的系统环境变量

**验证**：
```bash
# 进入容器
kubectl exec -it <pod-name> -n ns-l34pu8d4 -- /bin/sh

# 查看环境变量
env | grep DB_
env | grep REDIS_

# 输出示例：
# DB_URL=10.0.1.100
# DB_PORT=3306
# DB_NAME=marketing_automation
# DB_USERNAME=root
# DB_PASSWORD=MyP@ssw0rd
# REDIS_HOST=redis.example.com
# REDIS_PORT=6379
# REDIS_PASSWORD=R3d!sP@ss
```

---

### 6. Spring Boot 占位符

**作用**：从环境变量读取配置值

**语法**：
```yaml
${ENV_VAR_NAME:default_value}
```

**示例**：
```yaml
username: ${DB_USERNAME:root}
```

**解析规则**：
1. 查找环境变量 `DB_USERNAME`
2. 如果存在，使用环境变量的值
3. 如果不存在，使用默认值 `root`

---

## 🔍 环境变量名称映射

### 必须匹配！

| application.yml 占位符 | Deployment 环境变量名 | Secret Key | GitHub Secret |
|------------------------|----------------------|------------|---------------|
| `${DB_URL}` | `DB_URL` | `mysql-host` | `EXTERNAL_MYSQL_URL` (解析后) |
| `${DB_PORT}` | `DB_PORT` | `mysql-port` | `EXTERNAL_MYSQL_URL` (解析后) |
| `${DB_NAME}` | `DB_NAME` | `mysql-database` | `EXTERNAL_MYSQL_URL` (解析后) |
| `${DB_USERNAME}` | `DB_USERNAME` | `mysql-username` | `EXTERNAL_MYSQL_USERNAME` |
| `${DB_PASSWORD}` | `DB_PASSWORD` | `mysql-password` | `EXTERNAL_MYSQL_PASSWORD` |
| `${REDIS_HOST}` | `REDIS_HOST` | `redis-host` | `EXTERNAL_REDIS_HOST` |
| `${REDIS_PORT}` | `REDIS_PORT` | `redis-port` | `EXTERNAL_REDIS_PORT` |
| `${REDIS_PASSWORD}` | `REDIS_PASSWORD` | `redis-password` | `EXTERNAL_REDIS_PASSWORD` |

---

## 🧪 验证配置

### 1. 查看 Kubernetes Secret

```bash
# 查看 Secret 是否存在
kubectl get secret external-db-secret -n ns-l34pu8d4

# 查看 Secret 内容（Base64 编码）
kubectl get secret external-db-secret -n ns-l34pu8d4 -o yaml

# 解码查看实际值
kubectl get secret external-db-secret -n ns-l34pu8d4 -o jsonpath='{.data.mysql-username}' | base64 -d
```

### 2. 查看 Pod 环境变量

```bash
# 进入 Pod
kubectl exec -it <pod-name> -n ns-l34pu8d4 -- /bin/sh

# 查看环境变量
env | grep -E "DB_|REDIS_"
```

### 3. 查看应用日志

```bash
# 查看启动日志
kubectl logs <pod-name> -n ns-l34pu8d4 | grep -i "datasource\|redis"

# 如果配置正确，应该能看到：
# Successfully acquired Connection
# Lettuce: Redis connection established
```

---

## ❓ 常见问题

### Q1: 为什么我配置了 Secret，但应用还是用默认值？

**答**：环境变量名称不匹配！

检查：
- `application.yml` 中的占位符名称
- `app-deployment.yaml` 中的 `env[].name`
- 确保两者完全一致（区分大小写）

---

### Q2: 如何查看环境变量是否正确传递？

**答**：进入容器查看

```bash
kubectl exec -it <pod-name> -n ns-l34pu8d4 -- env | grep DB_
```

---

### Q3: Secret 更新后，应用没有生效？

**答**：需要重启 Pod

```bash
kubectl rollout restart deployment/tdd-marketing-automation -n ns-l34pu8d4
```

---

### Q4: 如何测试数据库连接？

**答**：查看应用日志

```bash
kubectl logs <pod-name> -n ns-l34pu8d4 | grep -i "datasource\|connection"
```

成功的标志：
```
Successfully acquired Connection
HikariPool-1 - Start completed
```

失败的标志：
```
CommunicationsException: Communications link failure
Access denied for user
```

---

## 📚 相关文档

- [GitHub Secrets 配置指南](./GITHUB_SECRETS_SETUP.md)
- [外部数据库配置指南](./sealos/EXTERNAL_DB_SETUP.md)
- [Sealos 部署指南](./SEALOS_DEPLOYMENT.md)

---

## 💡 最佳实践

1. ✅ **环境变量名称统一**
   - 使用清晰、一致的命名规范
   - 避免歧义（如 `DB_HOST` vs `DATABASE_HOST`）

2. ✅ **提供默认值**
   - 便于本地开发
   - `${ENV_VAR:default}`

3. ✅ **敏感信息用 Secret**
   - 密码、Token、API Key
   - 不要硬编码在配置文件中

4. ✅ **验证配置**
   - 部署后查看环境变量
   - 查看应用启动日志
   - 测试数据库连接

---

**🎉 现在你应该清楚环境变量是如何传递的了！**
