# 🔐 GitHub Secrets 配置指南

本文档详细说明如何获取和配置项目所需的所有 GitHub Secrets。

---

## 📋 Secrets 清单

项目需要配置 **9 个 GitHub Secrets**：

| # | Secret 名称 | 用途 | 必需 |
|---|------------|------|------|
| 1 | `DOCKER_HUB_USERNAME` | Docker Hub 用户名 | ✅ |
| 2 | `DOCKER_HUB_TOKEN` | Docker Hub 访问令牌 | ✅ |
| 3 | `SEALOS_KUBECONFIG` | Sealos 集群连接配置 | ✅ |
| 4 | `EXTERNAL_MYSQL_URL` | MySQL JDBC URL | ✅ |
| 5 | `EXTERNAL_MYSQL_USERNAME` | MySQL 用户名 | ✅ |
| 6 | `EXTERNAL_MYSQL_PASSWORD` | MySQL 密码 | ✅ |
| 7 | `EXTERNAL_REDIS_HOST` | Redis 主机地址 | ✅ |
| 8 | `EXTERNAL_REDIS_PORT` | Redis 端口 | ✅ |
| 9 | `EXTERNAL_REDIS_PASSWORD` | Redis 密码 | ✅ |

---

## 🚀 快速配置（一次性完成）

### 步骤 1: 访问 GitHub Secrets 设置页面

```
https://github.com/dylean/tdd-marketing-automation/settings/secrets/actions
```

或者手动导航：
1. 打开 GitHub 仓库
2. 点击 **Settings**（设置）
3. 左侧菜单选择 **Secrets and variables** → **Actions**
4. 点击 **New repository secret**

---

## 🔑 各 Secret 获取方式

### 1. DOCKER_HUB_USERNAME

**说明**：你的 Docker Hub 用户名

**获取方式**：

```bash
# 你的 Docker Hub 用户名（不是邮箱）
# 登录 https://hub.docker.com 后，右上角显示的用户名
```

**示例值**：
```
dylean
```

**配置步骤**：
1. 点击 **New repository secret**
2. **Name**: `DOCKER_HUB_USERNAME`
3. **Secret**: 输入你的 Docker Hub 用户名
4. 点击 **Add secret**

---

### 2. DOCKER_HUB_TOKEN

**说明**：Docker Hub 访问令牌（Personal Access Token）

**⚠️ 重要**：必须选择 **Read, Write** 权限，否则无法推送镜像！

**获取方式**：

#### 步骤 1: 登录 Docker Hub
访问：https://hub.docker.com/settings/security

#### 步骤 2: 创建新 Token
1. 点击 **New Access Token**
2. 填写信息：
   - **Access Token description**: `GitHub Actions TDD MA`
   - **Access permissions**: 选择 **Read, Write, Delete** ✅
3. 点击 **Generate**

#### 步骤 3: 复制 Token
⚠️ **Token 只显示一次，请立即复制保存！**

**示例值**：
```
dckr_pat_xxxxxxxxxxxxxxxxxxxxxxxxxxxxx
```

**配置步骤**：
1. 点击 **New repository secret**
2. **Name**: `DOCKER_HUB_TOKEN`
3. **Secret**: 粘贴刚才复制的 Token
4. 点击 **Add secret**

**如果之前配置过 Token 但权限不足**：
1. 找到旧的 `DOCKER_HUB_TOKEN`
2. 点击 **Update**
3. 粘贴新 Token
4. 点击 **Update secret**

---

### 3. SEALOS_KUBECONFIG

**说明**：Sealos Kubernetes 集群连接配置（Base64 编码）

**获取方式**：

#### 步骤 1: 登录 Sealos
访问：https://cloud.sealos.io

#### 步骤 2: 下载 Kubeconfig
1. 点击右上角用户头像
2. 选择 **账户设置**
3. 找到 **Kubeconfig** 部分
4. 点击 **下载** 按钮
5. 文件会保存到 `~/Downloads/kubeconfig`

#### 步骤 3: Base64 编码

**macOS / Linux**：
```bash
# Base64 编码（不换行）
cat ~/Downloads/kubeconfig | base64

# 如果是 Linux，使用:
cat ~/Downloads/kubeconfig | base64 -w 0
```

**Windows (PowerShell)**：
```powershell
[Convert]::ToBase64String([IO.File]::ReadAllBytes("$env:USERPROFILE\Downloads\kubeconfig"))
```

#### 步骤 4: 复制输出
复制整个 Base64 编码的字符串（非常长的一行）

**示例值**：
```
YXBpVmVyc2lvbjogdjEKY2x1c3RlcnM6Ci0gY2x1c3Rlcj...（很长的字符串）
```

**配置步骤**：
1. 点击 **New repository secret**
2. **Name**: `SEALOS_KUBECONFIG`
3. **Secret**: 粘贴 Base64 编码的字符串
4. 点击 **Add secret**

---

### 4. EXTERNAL_MYSQL_URL

**说明**：MySQL 数据库的完整 JDBC 连接 URL

**格式**：
```
jdbc:mysql://主机地址:端口/数据库名?参数
```

**完整示例**：
```
jdbc:mysql://mysql.example.com:3306/marketing_automation?useSSL=false&serverTimezone=Asia/Shanghai&characterEncoding=utf8
```

**参数说明**：

| 部分 | 说明 | 示例 |
|------|------|------|
| `主机地址` | MySQL 服务器地址 | `mysql.example.com` 或 `10.0.1.100` |
| `端口` | MySQL 端口（通常 3306） | `3306` |
| `数据库名` | 数据库名称 | `marketing_automation` |
| `useSSL=false` | 不使用 SSL（内网可用） | 固定参数 |
| `serverTimezone` | 时区 | `Asia/Shanghai` |
| `characterEncoding` | 字符编码 | `utf8` |

**如何获取**：

#### 方式 1: 如果你有 MySQL 管理员权限

```bash
# 登录 MySQL
mysql -u root -p

# 查看主机地址（如果是本机）
SELECT @@hostname;

# 查看端口
SHOW VARIABLES LIKE 'port';

# 查看数据库
SHOW DATABASES LIKE 'marketing_automation';
```

#### 方式 2: 询问数据库管理员

需要提供：
- 数据库服务器地址（外网可访问）
- 端口（默认 3306）
- 数据库名称
- 用户名和密码（见下面的 Secret 5 和 6）

**示例值**：
```
jdbc:mysql://47.xxx.xxx.xxx:3306/marketing_automation?useSSL=false&serverTimezone=Asia/Shanghai&characterEncoding=utf8
```

**配置步骤**：
1. 点击 **New repository secret**
2. **Name**: `EXTERNAL_MYSQL_URL`
3. **Secret**: 输入完整的 JDBC URL
4. 点击 **Add secret**

**⚠️ 特殊字符处理**：

如果密码包含特殊字符，**不要**把密码放在 URL 中，而是使用独立的用户名/密码配置（推荐）。

---

### 5. EXTERNAL_MYSQL_USERNAME

**说明**：MySQL 数据库用户名

**如何获取**：

从数据库管理员处获取，或者：

```sql
-- 登录 MySQL
mysql -u root -p

-- 查看所有用户
SELECT User, Host FROM mysql.user;

-- 创建专用用户（推荐）
CREATE USER 'tdd_ma_user'@'%' IDENTIFIED BY 'StrongPassword123!';
GRANT SELECT, INSERT, UPDATE, DELETE ON marketing_automation.* TO 'tdd_ma_user'@'%';
FLUSH PRIVILEGES;
```

**示例值**：
```
tdd_ma_user
```
或
```
root
```

**配置步骤**：
1. 点击 **New repository secret**
2. **Name**: `EXTERNAL_MYSQL_USERNAME`
3. **Secret**: 输入 MySQL 用户名
4. 点击 **Add secret**

---

### 6. EXTERNAL_MYSQL_PASSWORD

**说明**：MySQL 数据库密码

**如何获取**：

从数据库管理员处获取，或者查看 MySQL 配置文件。

**示例值**：
```
MyS3cr3tP@ssw0rd!2024
```

**密码要求**：
- 建议至少 12 位
- 包含大小写字母、数字、特殊字符
- 不要使用简单密码如 `123456`、`password`

**配置步骤**：
1. 点击 **New repository secret**
2. **Name**: `EXTERNAL_MYSQL_PASSWORD`
3. **Secret**: 输入 MySQL 密码
4. 点击 **Add secret**

**⚠️ 安全提示**：
- 不要使用 root 用户的密码
- 创建专用用户并只授予必要权限
- 定期更换密码

---

### 7. EXTERNAL_REDIS_HOST

**说明**：Redis 服务器主机地址

**如何获取**：

#### 方式 1: 从 Redis 配置文件

```bash
# 查看 Redis 配置
cat /etc/redis/redis.conf | grep bind

# 或者查看 Redis 进程
ps aux | grep redis
```

#### 方式 2: 从 Redis 管理员处获取

需要提供：
- Redis 服务器外网地址（如果在公网）
- 或内网地址（如果在同一网络）

#### 方式 3: 测试连接

```bash
# 尝试连接 Redis
redis-cli -h your-redis-host -p 6379 ping

# 如果返回 PONG，说明地址正确
```

**示例值**：
```
redis.example.com
```
或
```
10.0.1.101
```
或
```
47.xxx.xxx.xxx
```

**配置步骤**：
1. 点击 **New repository secret**
2. **Name**: `EXTERNAL_REDIS_HOST`
3. **Secret**: 输入 Redis 主机地址
4. 点击 **Add secret**

---

### 8. EXTERNAL_REDIS_PORT

**说明**：Redis 服务器端口

**默认值**：`6379`

**如何获取**：

```bash
# 查看 Redis 端口
cat /etc/redis/redis.conf | grep port

# 或者
netstat -tlnp | grep redis
```

**示例值**：
```
6379
```

**配置步骤**：
1. 点击 **New repository secret**
2. **Name**: `EXTERNAL_REDIS_PORT`
3. **Secret**: 输入 Redis 端口（通常是 `6379`）
4. 点击 **Add secret**

---

### 9. EXTERNAL_REDIS_PASSWORD

**说明**：Redis 密码（如果 Redis 配置了密码）

**如何获取**：

#### 方式 1: 从 Redis 配置文件

```bash
# 查看 Redis 密码配置
cat /etc/redis/redis.conf | grep requirepass
```

输出示例：
```
requirepass YourRedisPassword123!
```

#### 方式 2: 测试连接

```bash
# 尝试连接 Redis
redis-cli -h your-redis-host -p 6379

# 在 Redis CLI 中：
AUTH your_password

# 如果返回 OK，说明密码正确
```

#### 方式 3: 从 Redis 管理员处获取

**如果 Redis 没有密码**：
- 设置为空字符串 `""`（直接留空）
- 但**强烈建议**为 Redis 设置密码！

**示例值**：
```
R3d!sP@ssw0rd!2024
```

**配置步骤**：
1. 点击 **New repository secret**
2. **Name**: `EXTERNAL_REDIS_PASSWORD`
3. **Secret**: 输入 Redis 密码（如果没有密码，留空）
4. 点击 **Add secret**

---

## ✅ 验证配置

### 检查 Secrets 是否配置完整

访问：`https://github.com/your-username/tdd-marketing-automation/settings/secrets/actions`

你应该能看到以下 9 个 Secrets：

- [x] DOCKER_HUB_USERNAME
- [x] DOCKER_HUB_TOKEN
- [x] SEALOS_KUBECONFIG
- [x] EXTERNAL_MYSQL_URL
- [x] EXTERNAL_MYSQL_USERNAME
- [x] EXTERNAL_MYSQL_PASSWORD
- [x] EXTERNAL_REDIS_HOST
- [x] EXTERNAL_REDIS_PORT
- [x] EXTERNAL_REDIS_PASSWORD

---

## 🧪 测试配置

### 方式 1: 触发 GitHub Actions

```bash
# 推送一个空提交触发 CI/CD
git commit --allow-empty -m "ci: test github secrets configuration"
git push origin main
```

### 方式 2: 手动触发工作流

1. 访问：`https://github.com/your-username/tdd-marketing-automation/actions`
2. 选择 **CI/CD Pipeline** 工作流
3. 点击 **Run workflow**
4. 选择分支 `main`
5. 点击 **Run workflow**

### 查看执行结果

1. 在 Actions 页面查看工作流执行情况
2. 如果 **🐳 Docker 镜像推送** 失败：检查 `DOCKER_HUB_USERNAME` 和 `DOCKER_HUB_TOKEN`
3. 如果 **☁️ Sealos 部署** 失败：检查 `SEALOS_KUBECONFIG` 和数据库相关的 Secrets

---

## 🔒 安全最佳实践

### 1. ✅ 使用强密码

- 至少 12 位字符
- 包含大小写字母、数字、特殊字符
- 不要使用常见密码

**密码生成器**：
```bash
# 生成 16 位随机密码
openssl rand -base64 16

# 或使用在线工具
# https://passwordsgenerator.net/
```

### 2. ✅ 限制数据库权限

```sql
-- 只授予必要的权限
GRANT SELECT, INSERT, UPDATE, DELETE ON marketing_automation.* TO 'tdd_ma_user'@'%';

-- 不要使用 root 用户
-- 不要授予 ALL PRIVILEGES
```

### 3. ✅ 定期轮换密钥

- Docker Hub Token：每 6 个月更换
- 数据库密码：每 3-6 个月更换
- Sealos Kubeconfig：每年更换

### 4. ✅ 限制访问 IP

在数据库防火墙中：
- 只允许 Sealos 集群 IP 访问
- 禁止公网任意 IP 访问

### 5. ✅ 启用 SSL/TLS

```yaml
# MySQL URL 使用 SSL
mysql-url: "jdbc:mysql://host:3306/db?useSSL=true&requireSSL=true&serverTimezone=Asia/Shanghai"
```

---

## ❓ 常见问题

### Q1: 忘记 Docker Hub Token 怎么办？

**答**：Token 不能查看，只能重新生成：
1. 访问 https://hub.docker.com/settings/security
2. 删除旧 Token
3. 创建新 Token（记得选择 Read, Write 权限）
4. 在 GitHub Secrets 中更新 `DOCKER_HUB_TOKEN`

---

### Q2: 不知道 MySQL 的外网地址怎么办？

**答**：
1. 如果 MySQL 在云服务器上，查看云服务器的公网 IP
2. 如果 MySQL 在本地，需要配置端口转发或使用 VPN
3. 询问数据库管理员

---

### Q3: Redis 没有密码可以吗？

**答**：
- 技术上可以（留空）
- 但**强烈不推荐**！
- 建议为 Redis 设置密码：

```bash
# 编辑 Redis 配置
sudo vim /etc/redis/redis.conf

# 添加密码
requirepass YourStrongPassword123!

# 重启 Redis
sudo systemctl restart redis
```

---

### Q4: 数据库在 Sealos 内部怎么办？

**答**：
如果数据库也部署在 Sealos 集群内：
- **MySQL URL**: `jdbc:mysql://mysql-service.tdd-ma.svc.cluster.local:3306/marketing_automation?...`
- **Redis Host**: `redis-service.tdd-ma.svc.cluster.local`

---

### Q5: 如何测试数据库连接？

**答**：
```bash
# 测试 MySQL
mysql -h your-mysql-host -P 3306 -u your_user -p

# 测试 Redis
redis-cli -h your-redis-host -p 6379 -a your_password ping
```

---

## 📚 相关文档

- [外部数据库配置指南](./sealos/EXTERNAL_DB_SETUP.md)
- [快速部署指南](./sealos/QUICK_START_EXTERNAL_DB.md)
- [CI/CD 配置指南](./CI-CD-SETUP.md)
- [Sealos 完整部署文档](./SEALOS_DEPLOYMENT.md)

---

## 🆘 遇到问题？

1. **查看 Actions 日志**：GitHub Actions 页面会显示详细错误信息
2. **测试数据库连接**：使用上面的测试命令验证数据库可访问性
3. **检查 Secret 拼写**：确保 Secret 名称完全一致（区分大小写）
4. **提交 Issue**：在 GitHub 仓库提交问题

---

**🎉 配置完成后，推送代码即可自动部署！**

```bash
git push origin main
```

访问 GitHub Actions 查看部署进度：
```
https://github.com/your-username/tdd-marketing-automation/actions
```
