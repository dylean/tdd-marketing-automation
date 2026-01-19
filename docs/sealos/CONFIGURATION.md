# 📦 Sealos 部署配置

本目录包含将应用部署到 Sealos 云平台所需的所有 Kubernetes 配置文件。

---

## 📁 文件说明

| 文件 | 说明 |
|------|------|
| `app-deployment.yaml` | 应用部署配置（Deployment、Service、Ingress、HPA） |
| `database-deployment.yaml` | 数据库部署配置（MySQL、Redis）- **已弃用，使用外部数据库** |
| `external-db-secret-template.yaml` | 外部数据库配置模板（需复制为 `external-db-secret.yaml` 并填写实际信息） |
| `secrets-template.yaml` | 集群内数据库密码模板 - **已弃用，使用外部数据库** |

---

## 📚 文档

所有文档已移至 `docs/sealos/` 目录：

| 文档 | 路径 | 说明 |
|------|------|------|
| **快速指南** | [QUICK_START_EXTERNAL_DB.md](QUICK_START_EXTERNAL_DB.md) | 5 分钟快速部署（外部数据库） |
| **完整配置** | [EXTERNAL_DB_SETUP.md](EXTERNAL_DB_SETUP.md) | 外部数据库详细配置指南 |
| **Sealos 说明** | [README.md](README.md) | Sealos 配置文件说明 |
| **完整部署文档** | [../SEALOS_DEPLOYMENT.md](../SEALOS_DEPLOYMENT.md) | Sealos 完整部署文档 |

---

## 🚀 快速开始

### 使用外部数据库部署（推荐）⭐

```bash
# 1. 配置外部数据库连接
cp external-db-secret-template.yaml external-db-secret.yaml
vim external-db-secret.yaml  # 修改为你的数据库信息

# 2. 部署
kubectl create namespace tdd-ma
kubectl apply -f external-db-secret.yaml
kubectl apply -f app-deployment.yaml

# 3. 查看状态
kubectl get pods -n tdd-ma
```

**详细步骤**：查看 [快速部署指南](QUICK_START_EXTERNAL_DB.md)

---

## 🔐 安全提示

- ✅ `external-db-secret.yaml` 已在 `.gitignore` 中，不会被提交
- ✅ 使用强密码（至少 12 位，包含大小写字母、数字、特殊字符）
- ✅ 定期轮换密码（建议每 3-6 个月）

---

## 📖 更多信息

- [Sealos 官方文档](https://sealos.io/docs)
- [Kubernetes 官方文档](https://kubernetes.io/docs)
- [项目主 README](../../README.md)
