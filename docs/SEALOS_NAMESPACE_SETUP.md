# 🏷️ Sealos Namespace 配置指南

在 Sealos 中，namespace 是资源隔离的基本单位。本文档说明如何在 Sealos 中使用 namespace。

---

## 🎯 关键概念

### Sealos Namespace 说明

在 Sealos 中：
- ✅ **每个用户有一个默认的 namespace**（通常是用户 ID，如 `ns-xxxxx`）
- ✅ **应用部署在用户的 namespace 中**
- ❌ **用户不能通过 kubectl 创建新的 namespace**（权限限制）
- ✅ **可以通过 Sealos 控制台管理资源**

---

## 🔍 查看你的 Namespace

### 方式 1: 通过 Sealos 控制台

1. 登录 https://cloud.sealos.io
2. 打开任意应用或数据库
3. 查看 URL，你会看到类似：`https://cloud.sealos.io/ns/ns-xxxxx/...`
4. `ns-xxxxx` 就是你的 namespace

### 方式 2: 通过 kubectl

```bash
# 查看你有权限访问的 namespace
kubectl get namespace

# 或者查看当前上下文的 namespace
kubectl config view --minify | grep namespace
```

**输出示例**：
```
NAME              STATUS   AGE
ns-l34pu8d4       Active   30d
```

---

## ⚙️ 配置项目使用你的 Namespace

### 步骤 1: 确定你的 Namespace

```bash
# 查看你的 namespace
kubectl get namespace

# 假设输出是: ns-l34pu8d4
```

### 步骤 2: 修改部署配置

编辑 `sealos/app-deployment.yaml`，将所有的 `namespace: tdd-ma` 替换为你的实际 namespace：

```bash
# 全局替换（Mac/Linux）
cd sealos
sed -i '' 's/namespace: tdd-ma/namespace: ns-l34pu8d4/g' app-deployment.yaml
sed -i '' 's/namespace: tdd-ma/namespace: ns-l34pu8d4/g' database-deployment.yaml
sed -i '' 's/-n tdd-ma/-n ns-l34pu8d4/g' *.yaml

# 或者手动编辑文件
vim app-deployment.yaml
# 按 :%s/tdd-ma/ns-l34pu8d4/g 回车
```

**修改前**：
```yaml
apiVersion: v1
kind: Namespace
metadata:
  name: tdd-ma
---
apiVersion: apps/v1
kind: Deployment
metadata:
  name: tdd-marketing-automation
  namespace: tdd-ma
```

**修改后**：
```yaml
# 删除 Namespace 定义（不需要创建）
# ---
apiVersion: apps/v1
kind: Deployment
metadata:
  name: tdd-marketing-automation
  namespace: ns-l34pu8d4  # 改为你的实际 namespace
```

### 步骤 3: 修改 CI/CD 配置

编辑 `.github/workflows/ci-cd.yml`，替换所有的 `tdd-ma` 为你的实际 namespace：

```yaml
# 查找并替换
-n tdd-ma  →  -n ns-l34pu8d4
namespace tdd-ma  →  namespace ns-l34pu8d4
```

或使用命令：
```bash
# 在项目根目录
sed -i '' 's/tdd-ma/ns-l34pu8d4/g' .github/workflows/ci-cd.yml
```

---

## 🚀 推荐配置方案

### 方案 A: 使用你的默认 Namespace（推荐）⭐

**优势**：
- ✅ 不需要创建 namespace
- ✅ 权限已配置好
- ✅ 与其他 Sealos 应用隔离

**步骤**：
1. 查看你的 namespace：`kubectl get namespace`
2. 修改配置文件中的 namespace
3. 部署应用

---

### 方案 B: 通过 Sealos 控制台部署（最简单）⭐⭐⭐

**不使用 kubectl 和 CI/CD，直接通过 Sealos UI 部署**：

#### 步骤 1: 部署数据库（如果需要）

1. 登录 Sealos 控制台
2. 点击 **数据库** → **创建数据库**
3. 选择 **MySQL 8.0** 和 **Redis 7.0**
4. 配置资源和密码
5. 点击创建

#### 步骤 2: 部署应用

1. 点击 **应用管理** → **创建应用**
2. 填写应用信息：
   - **应用名称**：`tdd-marketing-automation`
   - **镜像地址**：`你的Docker Hub用户名/tdd-marketing-automation:latest`
   - **CPU**：0.5 核
   - **内存**：1Gi
   - **实例数量**：2
3. 添加环境变量（参考 `app-deployment.yaml`）
4. 配置健康检查：
   - 路径：`/actuator/health`
   - 端口：8080
5. 点击 **部署**

**优势**：
- ✅ 无需配置 namespace
- ✅ 无需配置 kubectl
- ✅ 界面友好，易于操作
- ✅ 自动生成域名和 HTTPS

---

### 方案 C: 使用环境变量配置 Namespace

修改部署配置，使用环境变量：

**在 `app-deployment.yaml` 中**：
```yaml
# 不要硬编码 namespace
# 使用占位符
namespace: ${SEALOS_NAMESPACE}
```

**在 GitHub Secrets 中添加**：
- `SEALOS_NAMESPACE`: 你的实际 namespace（如 `ns-l34pu8d4`）

**在 CI/CD 中替换**：
```yaml
- name: 替换 namespace
  run: |
    sed -i "s/\${SEALOS_NAMESPACE}/${{ secrets.SEALOS_NAMESPACE }}/g" sealos/app-deployment.yaml
```

---

## 🔧 修复当前错误

你当前遇到的错误是因为 CI/CD 尝试创建 namespace，但没有权限。

### 快速修复步骤：

#### 1. 查看你的 Namespace

```bash
kubectl get namespace
```

假设输出是 `ns-l34pu8d4`

#### 2. 添加 GitHub Secret

访问：`https://github.com/dylean/tdd-marketing-automation/settings/secrets/actions`

添加新 Secret：
- **Name**: `SEALOS_NAMESPACE`
- **Secret**: `ns-l34pu8d4`（你的实际 namespace）

#### 3. 修改 CI/CD 配置

我已经修改了 `.github/workflows/ci-cd.yml`，将：
- `kubectl create namespace tdd-ma` 改为验证 namespace 存在
- 如果 namespace 不存在，会提示错误并退出

#### 4. 修改部署配置使用你的 Namespace

```bash
cd sealos

# 方式 1: 手动编辑
vim app-deployment.yaml
# 将所有 namespace: tdd-ma 改为 namespace: ns-l34pu8d4

# 方式 2: 使用 sed 批量替换
sed -i '' 's/namespace: tdd-ma/namespace: ns-l34pu8d4/g' *.yaml

# 提交更改
git add .
git commit -m "fix: use actual Sealos namespace ns-l34pu8d4"
git push origin main
```

#### 5. 重新触发部署

```bash
git commit --allow-empty -m "ci: retry deployment with correct namespace"
git push origin main
```

---

## 📝 命名规范

### Sealos Namespace 命名规则

Sealos 自动生成的 namespace 格式：
- `ns-xxxxxxx` - 用户的默认 namespace
- `user-system` - 系统 namespace（不要使用）

### 资源命名建议

在你的 namespace 中，资源命名建议：
- **Deployment**: `tdd-marketing-automation`
- **Service**: `tdd-ma-service`
- **Ingress**: `tdd-ma-ingress`
- **Secret**: `external-db-secret`

---

## ✅ 验证配置

### 1. 验证 Namespace 访问

```bash
# 测试是否能访问你的 namespace
kubectl get pods -n ns-l34pu8d4

# 测试是否能创建资源
kubectl run test --image=nginx -n ns-l34pu8d4
kubectl delete pod test -n ns-l34pu8d4
```

### 2. 验证部署配置

```bash
# 检查配置文件中的 namespace
grep "namespace:" sealos/*.yaml

# 应该全部显示你的实际 namespace
```

### 3. 验证 GitHub Secret

访问：`https://github.com/your-username/tdd-marketing-automation/settings/secrets/actions`

确认有这个 Secret：
- [x] `SEALOS_NAMESPACE`

---

## 🔍 故障排查

### 问题 1: 不知道自己的 Namespace

**解决方案**：
```bash
# 查看所有可访问的 namespace
kubectl get namespace

# 查看当前上下文
kubectl config view --minify
```

### 问题 2: 权限不足

**错误**：
```
Error from server (Forbidden): ... cannot create resource "namespaces"
```

**原因**：Sealos 不允许用户创建 namespace

**解决方案**：
- 使用你的默认 namespace
- 或通过 Sealos 控制台部署

### 问题 3: 找不到资源

**错误**：
```
Error from server (NotFound): namespaces "tdd-ma" not found
```

**原因**：配置中使用了不存在的 namespace

**解决方案**：
- 修改配置使用你的实际 namespace
- 使用 `kubectl get namespace` 查看可用的 namespace

---

## 📚 相关文档

- [Sealos 快速部署指南](./sealos/QUICK_START_EXTERNAL_DB.md)
- [外部数据库配置指南](./sealos/EXTERNAL_DB_SETUP.md)
- [GitHub Secrets 配置指南](./GITHUB_SECRETS_SETUP.md)

---

## 💡 最佳实践

1. ✅ **使用默认 Namespace**
   - 最简单，权限已配置
   - 与其他应用自然隔离

2. ✅ **通过 Sealos 控制台部署**
   - 适合不熟悉 Kubernetes 的用户
   - UI 友好，功能完整

3. ✅ **配置中使用环境变量**
   - 便于在不同环境部署
   - 避免硬编码

4. ❌ **不要尝试创建 Namespace**
   - Sealos 不允许
   - 会导致部署失败

---

**🎯 总结：在 Sealos 中，使用你的默认 namespace，不要尝试创建新的 namespace！**
