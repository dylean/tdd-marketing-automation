# TDD 分享 - Slidev 演示文稿

## 本地开发

```bash
cd docs/slides
npm install
npm run dev
```

## 构建

```bash
npm run build
```

构建产物会输出到 `dist` 目录。

## Vercel 部署

### 方案一：使用主项目仓库部署（推荐）

如果主项目已经在 GitHub 上：

1. 访问 [Vercel Dashboard](https://vercel.com/dashboard)
2. 点击 "Add New Project"
3. 选择你的 `tdd-marketing-automation` 仓库
4. 配置项目：
   - **Root Directory**: 点击 "Edit"，输入 `docs/slides`
   - **Framework Preset**: Other
   - **Build Command**: `npm run build`（自动从 vercel.json 读取）
   - **Output Directory**: `dist`（自动从 vercel.json 读取）
   - **Install Command**: `npm install`
5. 点击 "Deploy"

> 💡 关键：设置 Root Directory 为 `docs/slides`，Vercel 会只构建这个子目录

### 方案二：使用 Vercel CLI 直接部署（无需 Git）

适合本地项目或不想使用 Git 的情况：

```bash
# 1. 安装 Vercel CLI
npm i -g vercel

# 2. 进入 slides 目录
cd docs/slides

# 3. 登录 Vercel（首次使用）
vercel login

# 4. 部署（预览环境）
vercel

# 5. 部署到生产环境
vercel --prod
```

CLI 部署特点：
- ✅ 不需要 GitHub 仓库
- ✅ 可以从本地直接部署
- ✅ 每次执行 `vercel --prod` 更新
- ⚠️ 需要手动执行命令，无法自动部署

### 方案三：创建独立的 Slides 仓库

如果希望 slides 独立管理：

```bash
# 1. 创建新的 Git 仓库
cd docs/slides
git init
git add .
git commit -m "Initial commit"

# 2. 在 GitHub 创建新仓库 tdd-slides
# 3. 推送代码
git remote add origin https://github.com/your-username/tdd-slides.git
git push -u origin main

# 4. 在 Vercel 导入该仓库
```

## 推荐方案

| 方案 | 适用场景 | 优点 | 缺点 |
|------|---------|------|------|
| **方案一** | 已有 GitHub 项目 | 自动部署，代码统一管理 | 需要配置 Root Directory |
| **方案二** | 本地项目，快速部署 | 无需 Git，简单快捷 | 手动部署，无版本控制 |
| **方案三** | Slides 需要独立管理 | 完全独立，清晰分离 | 需要维护两个仓库 |

## 验证部署

部署成功后，Vercel 会提供一个 URL，例如：
- `https://tdd-marketing-automation-slides.vercel.app`
- `https://your-project-xxx.vercel.app`

## 更新部署

- **方案一**：推送代码到 GitHub 自动部署
- **方案二**：执行 `vercel --prod` 手动更新
- **方案三**：推送到独立仓库自动部署

## 环境变量

如果需要配置环境变量，可以在 Vercel Dashboard 的项目设置中添加。

## 本地预览构建产物

```bash
npm run build
npx serve dist
```
