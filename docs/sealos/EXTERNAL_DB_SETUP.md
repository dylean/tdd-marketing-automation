# 🔗 外部数据库配置指南

本项目使用**外部数据库**（MySQL 和 Redis），而不是在 Sealos 集群内部署数据库。

---

## 📋 前置条件

确保你已经有可用的：

- ✅ **MySQL 8.0+** 数据库（已创建数据库 `marketing_automation`）
- ✅ **Redis 7.0+** 实例
- ✅ 数据库可以从 Sealos 集群访问（网络连通）

---

## 🔧 配置步骤

### 方式一：手动部署（kubectl）

#### 步骤 1: 准备配置信息

收集以下信息：

| 配置项 | 示例值 | 说明 |
|--------|--------|------|
| **MySQL 地址** | `mysql.example.com:3306` | MySQL 服务器地址和端口 |
| **MySQL 数据库** | `marketing_automation` | 数据库名称 |
| **MySQL 用户名** | `root` 或 `your_user` | 数据库用户 |
| **MySQL 密码** | `YourPassword123!` | 数据库密码 |
| **Redis 地址** | `redis.example.com` | Redis 服务器地址 |
| **Redis 端口** | `6379` | Redis 端口 |
| **Redis 密码** | `YourRedisPass!` | Redis 密码（如果有） |

#### 步骤 2: 创建配置文件

```bash
cd sealos

# 复制模板
cp external-db-secret-template.yaml external-db-secret.yaml

# 编辑配置文件
vim external-db-secret.yaml
# 或
code external-db-secret.yaml
```

**编辑内容**（替换占位符）：

```yaml
apiVersion: v1
kind: Secret
metadata:
  name: external-db-secret
  namespace: tdd-ma
type: Opaque
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

#### 步骤 3: 应用配置

```bash
# 创建 namespace（如果还没有）
kubectl create namespace tdd-ma

# 应用外部数据库配置
kubectl apply -f external-db-secret.yaml

# 验证 Secret 创建成功
kubectl get secret external-db-secret -n tdd-ma
```

#### 步骤 4: 部署应用

```bash
# 部署应用
kubectl apply -f app-deployment.yaml

# 查看部署状态
kubectl rollout status deployment/tdd-marketing-automation -n tdd-ma

# 查看 Pod 日志
kubectl logs -f deployment/tdd-marketing-automation -n tdd-ma
```

---

### 方式二：GitHub Actions 自动部署

#### 配置 GitHub Secrets

在 GitHub 仓库中添加以下 Secrets：

访问：`https://github.com/your-username/tdd-marketing-automation/settings/secrets/actions`

| Secret 名称 | 示例值 | 说明 |
|------------|--------|------|
| `EXTERNAL_MYSQL_URL` | `jdbc:mysql://mysql.example.com:3306/marketing_automation?useSSL=false&serverTimezone=Asia/Shanghai&characterEncoding=utf8` | MySQL JDBC URL |
| `s` | `root` | MySQL 用户名 |
| `EXTERNAL_MYSQL_PASSWORD` | `YourPassword123!` | MySQL 密码 |
| `EXTERNAL_REDIS_HOST` | `redis.example.com` | Redis 主机地址 |
| `EXTERNAL_REDIS_PORT` | `6379` | Redis 端口 |
| `EXTERNAL_REDIS_PASSWORD` | `YourRedisPass!` | Redis 密码（如果没有密码，留空） |

#### 触发部署

```bash
# 推送代码触发自动部署
git push origin main
```

GitHub Actions 会自动：
1. 创建 `external-db-secret`（如果不存在）
2. 部署应用
3. 应用会自动连接到你的外部数据库

---

## ✅ 验证连接

### 1. 查看应用日志

```bash
kubectl logs -f deployment/tdd-marketing-automation -n tdd-ma
```

**成功标志**：
```
Started MarketingAutomationApplication in X.XXX seconds
```

**失败标志**：
```
CommunicationsException: Communications link failure  # MySQL 连接失败
RedisConnectionException: Unable to connect to Redis  # Redis 连接失败
```

### 2. 测试健康检查

```bash
# 获取 Ingress 地址
INGRESS_HOST=$(kubectl get ingress tdd-ma-ingress -n tdd-ma -o jsonpath='{.spec.rules[0].host}')

# 测试健康检查
curl https://${INGRESS_HOST}/actuator/health
```

**预期输出**：
```json
{"status":"UP"}
```

### 3. 检查数据库连接

```bash
# 进入 Pod
kubectl exec -it <pod-name> -n tdd-ma -- /bin/sh

# 测试 MySQL 连接（在容器内）
nc -zv your-mysql-host 3306

# 测试 Redis 连接（在容器内）
nc -zv your-redis-host 6379
```

---

## 🔧 故障排查

### 问题 1: 无法连接 MySQL

**错误日志**：
```
CommunicationsException: Communications link failure
```

**可能原因**：
1. MySQL 服务器地址或端口错误
2. MySQL 防火墙阻止了 Sealos 集群的 IP
3. MySQL 用户权限不足
4. 网络不通

**解决方案**：

```bash
# 1. 检查 Secret 配置
kubectl get secret external-db-secret -n tdd-ma -o yaml

# 2. 从 Pod 内测试连接
kubectl exec -it <pod-name> -n tdd-ma -- /bin/sh
nc -zv your-mysql-host 3306

# 3. 检查 MySQL 防火墙规则
# 在 MySQL 服务器上允许 Sealos 集群 IP 访问

# 4. 检查 MySQL 用户权限
mysql -u root -p
GRANT ALL PRIVILEGES ON marketing_automation.* TO 'your_user'@'%' IDENTIFIED BY 'password';
FLUSH PRIVILEGES;
```

---

### 问题 2: 无法连接 Redis

**错误日志**：
```
RedisConnectionException: Unable to connect to Redis
```

**可能原因**：
1. Redis 服务器地址或端口错误
2. Redis 密码错误
3. Redis 防火墙阻止连接
4. Redis 配置 `bind` 只允许本地连接

**解决方案**：

```bash
# 1. 检查 Secret 配置
kubectl get secret external-db-secret -n tdd-ma -o yaml

# 2. 从 Pod 内测试连接
kubectl exec -it <pod-name> -n tdd-ma -- /bin/sh
nc -zv your-redis-host 6379

# 3. 检查 Redis 配置（在 Redis 服务器上）
# 编辑 redis.conf
bind 0.0.0.0  # 允许远程连接
protected-mode no  # 关闭保护模式（或配置密码）

# 4. 重启 Redis
systemctl restart redis
```

---

### 问题 3: 应用启动后数据库表不存在

**错误日志**：
```
Table 'marketing_automation.t_campaign' doesn't exist
```

**原因**：Flyway 数据库迁移未执行

**解决方案**：

```bash
# 1. 检查 Flyway 是否启用（应用配置）
kubectl get configmap app-config -n tdd-ma -o yaml

# 2. 手动执行 SQL 创建表（如果 Flyway 失败）
# 在 MySQL 中执行 src/main/resources/db/migration/*.sql

# 3. 查看应用日志，确认 Flyway 执行情况
kubectl logs deployment/tdd-marketing-automation -n tdd-ma | grep -i flyway
```

---

### 问题 4: 密码包含特殊字符导致 URL 解析错误

**错误日志**：
```
IllegalArgumentException: URLDecoder: Illegal hex characters in escape (%) pattern
```

**原因**：MySQL JDBC URL 中的密码包含特殊字符未转义

**解决方案**：

URL 编码特殊字符：

| 字符 | URL 编码 |
|------|----------|
| `@` | `%40` |
| `#` | `%23` |
| `$` | `%24` |
| `%` | `%25` |
| `&` | `%26` |
| `+` | `%2B` |
| `,` | `%2C` |
| `/` | `%2F` |
| `:` | `%3A` |
| `;` | `%3B` |
| `=` | `%3D` |
| `?` | `%3F` |
| `[` | `%5B` |
| `]` | `%5D` |

**示例**：

```yaml
# 密码: MyP@ss#123
# 错误的 URL:
mysql-url: "jdbc:mysql://host:3306/db?password=MyP@ss#123"

# 正确的 URL（密码放在 URL 中）:
mysql-url: "jdbc:mysql://host:3306/db?password=MyP%40ss%23123"

# 或者使用独立的用户名/密码配置（推荐）:
mysql-url: "jdbc:mysql://host:3306/db?useSSL=false&serverTimezone=Asia/Shanghai"
mysql-username: "your_user"
mysql-password: "MyP@ss#123"  # 不需要转义
```

---

## 📊 网络连通性测试

### 从本地测试（部署前）

```bash
# 测试 MySQL 连接
mysql -h your-mysql-host -P 3306 -u your_user -p

# 测试 Redis 连接
redis-cli -h your-redis-host -p 6379 -a your_password
```

### 从 Sealos 集群测试

```bash
# 创建临时测试 Pod
kubectl run test-net --image=curlimages/curl -it --rm -n tdd-ma -- sh

# 在 Pod 内测试
nc -zv your-mysql-host 3306
nc -zv your-redis-host 6379
```

---

## 🔐 安全建议

1. ✅ **使用专用数据库用户**
   - 不要使用 root 用户
   - 只授予必要的权限（`SELECT`, `INSERT`, `UPDATE`, `DELETE`）

```sql
-- 创建专用用户
CREATE USER 'tdd_ma_user'@'%' IDENTIFIED BY 'StrongPassword123!';
GRANT SELECT, INSERT, UPDATE, DELETE ON marketing_automation.* TO 'tdd_ma_user'@'%';
FLUSH PRIVILEGES;
```

2. ✅ **限制访问 IP**
   - 获取 Sealos 集群出口 IP
   - 在数据库防火墙中只允许该 IP 访问

3. ✅ **使用 SSL/TLS 连接**（推荐）

```yaml
mysql-url: "jdbc:mysql://host:3306/db?useSSL=true&requireSSL=true&serverTimezone=Asia/Shanghai"
```

4. ✅ **定期轮换密码**
   - 建议每 3-6 个月更新一次数据库密码

```bash
# 更新 Secret
kubectl create secret generic external-db-secret -n tdd-ma \
  --from-literal=mysql-password=new-password \
  --dry-run=client -o yaml | kubectl apply -f -

# 重启应用使新密码生效
kubectl rollout restart deployment/tdd-marketing-automation -n tdd-ma
```

---

## 📚 相关文档

- [Sealos 快速部署指南](./QUICK_START.md)
- [完整部署文档](../docs/SEALOS_DEPLOYMENT.md)
- [MySQL 官方文档](https://dev.mysql.com/doc/)
- [Redis 官方文档](https://redis.io/docs/)

---

## 🆘 获取帮助

如果遇到问题：

1. **查看应用日志**：`kubectl logs -f deployment/tdd-marketing-automation -n tdd-ma`
2. **检查网络连通性**：使用 `nc` 或 `telnet` 测试
3. **验证配置**：`kubectl get secret external-db-secret -n tdd-ma -o yaml`
4. **提交 Issue**：在 GitHub 仓库提交问题

---

🎉 **祝配置顺利！**
