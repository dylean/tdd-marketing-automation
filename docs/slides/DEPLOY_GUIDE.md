# 快速部署指南

## 🚀 最快部署方式（推荐）

使用 Vercel CLI 直接部署，**无需 GitHub 仓库**：

```bash
# 进入目录
cd docs/slides

# 一键部署（首次会提示登录）
./deploy.sh

# 部署到生产环境
./deploy.sh prod
```

首次使用会要求登录 Vercel：
1. 浏览器会自动打开
2. 选择登录方式（GitHub/GitLab/Email）
3. 授权后回到终端继续

## 📋 详细步骤

### 方法1️⃣：使用部署脚本（最简单）

```bash
cd docs/slides
./deploy.sh          # 预览部署
./deploy.sh prod     # 生产部署
```

### 方法2️⃣：手动使用 Vercel CLI

```bash
# 1. 安装 CLI（如果未安装）
npm i -g vercel

# 2. 进入目录
cd docs/slides

# 3. 登录
vercel login

# 4. 构建
npm run build

# 5. 部署
vercel          # 预览环境
vercel --prod   # 生产环境
```

### 方法3️⃣：使用主项目 GitHub 仓库

如果你的项目已经在 GitHub：

1. 访问 https://vercel.com/dashboard
2. 点击 "Add New Project"
3. 选择 `tdd-marketing-automation` 仓库
4. **重要**：设置 Root Directory = `docs/slides`
5. 点击 Deploy

## ⚡️ 对比

| 方式 | 部署时间 | 需要 GitHub | 自动部署 |
|------|---------|------------|---------|
| **部署脚本** | 2分钟 | ❌ | ❌ |
| **Vercel CLI** | 2分钟 | ❌ | ❌ |
| **GitHub 集成** | 5分钟 | ✅ | ✅ |

## 🔄 更新已部署的项目

### CLI 方式
```bash
cd docs/slides
./deploy.sh prod
```

### GitHub 方式
```bash
git add .
git commit -m "Update slides"
git push
```
自动触发部署

## 🌐 访问

部署成功后，Vercel 会显示 URL：
```
✅ Production: https://your-project.vercel.app
```

## 💡 提示

- 首次部署建议用 `vercel`（预览），确认无误后用 `vercel --prod`
- CLI 部署不依赖 Git，适合快速迭代
- GitHub 集成适合团队协作和版本管理

## ❓ 常见问题

**Q: 需要购买 Vercel 吗？**  
A: 不需要，个人项目免费

**Q: 可以绑定自定义域名吗？**  
A: 可以，在 Vercel Dashboard 的项目设置中配置

**Q: 如何删除部署？**  
A: 在 Vercel Dashboard 的项目设置中删除

**Q: 部署失败怎么办？**  
A: 检查 `npm run build` 是否成功，查看 Vercel 日志
