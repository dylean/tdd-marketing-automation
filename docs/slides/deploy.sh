#!/bin/bash

# TDD Slides - Vercel 部署脚本

set -e

echo "🚀 开始部署 TDD Slides 到 Vercel..."

# 检查是否安装了 Vercel CLI
if ! command -v vercel &> /dev/null; then
    echo "❌ Vercel CLI 未安装"
    echo "📦 正在安装 Vercel CLI..."
    npm i -g vercel
fi

# 构建项目
echo "🔨 构建项目..."
npm run build

# 部署
echo "📤 部署到 Vercel..."
if [ "$1" = "prod" ] || [ "$1" = "--prod" ]; then
    echo "🌟 部署到生产环境..."
    vercel --prod
else
    echo "🔍 部署到预览环境..."
    vercel
    echo ""
    echo "💡 提示: 使用 './deploy.sh prod' 部署到生产环境"
fi

echo "✅ 部署完成！"
