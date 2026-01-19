# TDD 故事卡索引

> 基于营销自动化（MA）真实项目的 TDD 演示故事卡

---

## 📋 故事卡列表

### 1️⃣ 手动 TDD：营销活动预算管理
**文件**: `01-manual-tdd-campaign-budget.md`

**适合场景**:
- 向团队演示传统的 TDD 流程
- 教学红-绿-重构循环
- 强调测试驱动设计的思维方式

**功能**: 在现有 `Campaign` 聚合根上添加预算管理能力

**技术栈**:
- Domain 层：纯 Java 领域对象
- BigDecimal 金额计算
- 状态校验和业务规则

**难度**: ⭐⭐⭐ (中等)  
**预计时间**: 40-50 分钟

---

### 2️⃣ AI TDD：营销活动数据分析
**文件**: `02-ai-tdd-campaign-analytics.md`

**适合场景**:
- 演示 AI 辅助 TDD 的效率
- 展示如何使用 Cursor/Copilot 等工具
- 人机协作的最佳实践

**功能**: 通过 FeignClient 调用外部服务获取活动效果数据

**技术栈**:
- FeignClient 远程调用
- Redis 缓存策略
- DDD 分层架构
- 异常降级处理

**难度**: ⭐⭐⭐⭐ (中高)  
**预计时间**: 30-45 分钟

---

## 🔍 两种方式对比

| 维度 | 手动 TDD | AI TDD |
|------|---------|--------|
| **演示重点** | TDD 基本功 | AI 辅助效率 |
| **步骤** | 严格的红-绿-重构 | Prompt 驱动开发 |
| **适合人群** | TDD 初学者 | 有经验的开发者 |
| **技术复杂度** | 中等（Domain 层） | 较高（跨层集成） |
| **外部依赖** | 无 | FeignClient、Redis |
| **学习曲线** | 陡峭但扎实 | 平缓但需工具熟练度 |

---

## 🎯 使用建议

### 培训/分享建议

**1小时场景**（推荐）:
- 前 30 分钟：手动 TDD 演示预算管理
- 后 30 分钟：AI TDD 演示数据分析
- 对比两种方式的效率和代码质量

**2小时工作坊**:
- 手动 TDD：详细演示每一步（50分钟）
- AI TDD：详细演示 Prompt 工程（50分钟）
- 讨论和 Q&A（20分钟）

### 前置准备

**共同要求**:
- ✅ 项目已搭建（见项目 README）
- ✅ 测试环境可运行
- ✅ IDE 配置完成

**AI TDD 额外要求**:
- ✅ Cursor/GitHub Copilot 已安装
- ✅ Redis 环境准备好（或使用 Mock）
- ✅ 熟悉 AI 工具的基本用法

---

## 📖 项目结构

```
src/
├── main/java/com/tdd/ma/
│   ├── domain/              # 领域层
│   │   └── campaign/
│   │       ├── Campaign.java          ← 预算管理在这里
│   │       └── CampaignAnalytics.java ← 数据分析值对象
│   ├── application/         # 应用层
│   │   └── campaign/
│   │       ├── CampaignApplicationService.java
│   │       └── CampaignAnalyticsService.java  ← AI TDD 主要工作
│   ├── infrastructure/      # 基础设施层
│   │   ├── cache/           ← Redis 缓存
│   │   ├── external/        ← FeignClient
│   │   └── persistence/     ← MyBatis Plus
│   └── interfaces/          # 接口层
│       └── rest/            ← REST API
└── test/                    # 测试代码
```

---

## 🚀 快速开始

### 手动 TDD 演示流程

```bash
# 1. 打开故事卡
open docs/story-cards/01-manual-tdd-campaign-budget.md

# 2. 创建测试类
touch src/test/java/com/tdd/ma/domain/campaign/CampaignBudgetTest.java

# 3. 按照故事卡的 TDD 步骤进行
# - 写第一个测试（红）
# - 实现功能（绿）
# - 重构代码（蓝）
# - 重复循环

# 4. 运行测试
./gradlew test --tests "CampaignBudgetTest"
```

### AI TDD 演示流程

```bash
# 1. 打开故事卡
open docs/story-cards/02-ai-tdd-campaign-analytics.md

# 2. 使用故事卡中的 Prompt
# 在 Cursor 中使用 Cmd+K 或聊天窗口

# 3. 逐步生成代码
# - Step 1: 生成领域对象测试
# - Step 2: 生成领域对象实现
# - Step 3-6: 继续按步骤进行

# 4. 运行测试
./gradlew test --tests "CampaignAnalytics*"
```

---

## 💡 教学建议

### 对于手动 TDD

1. **慢下来**：不要急于实现，享受思考的过程
2. **小步前进**：每次只写一个测试用例
3. **观察失败**：确保测试失败的原因正确
4. **最简实现**：绿灯时用最简单的方法通过
5. **及时重构**：绿灯后立即审视代码质量

### 对于 AI TDD

1. **明确意图**：Prompt 要清晰、具体
2. **分步执行**：不要一次性生成所有代码
3. **人工 Review**：AI 生成的代码需要审查
4. **迭代优化**：通过多轮对话改进代码
5. **保持测试**：始终让测试引导开发

---

## 🎓 扩展练习

完成两个故事卡后，可以尝试：

1. **组合功能**: 结合预算管理和数据分析，实现"预算预警"功能
2. **性能优化**: 为数据分析添加批量查询接口
3. **监控告警**: 当活动超支时发送通知（集成消息队列）
4. **数据报表**: 生成活动效果分析报告（PDF/Excel）

---

## 📚 参考资源

- [Slidev 演示文稿](../slides/README.md) - TDD 理论和实践
- [项目 README](../../README.md) - 项目搭建和配置
- [ArchUnit 测试](../../src/test/java/com/tdd/ma/architecture/) - 架构守护

---

## ❓ 常见问题

**Q: 两个故事卡可以同时做吗？**  
A: 可以，但建议先完成手动 TDD，打好基础后再尝试 AI TDD。

**Q: AI TDD 必须用 Cursor 吗？**  
A: 不是，也可以用 GitHub Copilot、ChatGPT 等工具，Prompt 略有不同。

**Q: 如果测试失败怎么办？**  
A: 这正是 TDD 的精髓！仔细观察失败原因，这会引导你实现正确的功能。

**Q: 代码可以直接用于生产吗？**  
A: 这是教学用的演示代码，生产使用前需要补充：
   - 更完善的异常处理
   - 操作审计日志
   - 更多的边界测试
   - 性能测试和优化

---

**版本**: v1.0  
**更新时间**: 2026-01-19  
**维护者**: TDD Training Team
