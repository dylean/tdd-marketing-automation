# 故事卡：创建营销活动

> 📋 **用于手动 TDD 演示**

---

## 用户故事

**作为** 运营人员  
**我希望** 能够创建营销活动  
**以便于** 后续进行活动配置和执行

---

## 验收标准 (Acceptance Criteria)

### AC1: 创建活动成功
- **Given** 活动名称为"双十一大促"，开始时间为7天后
- **When** 创建活动
- **Then** 返回活动信息，包含 ID、名称、开始时间
- **And** 活动状态为 DRAFT（草稿）

### AC2: 活动名称不能为空
- **Given** 活动名称为空
- **When** 创建活动
- **Then** 抛出异常，提示"活动名称不能为空"

### AC3: 开始时间必须晚于当前时间
- **Given** 开始时间为昨天
- **When** 创建活动
- **Then** 抛出异常，提示"活动开始时间必须晚于当前时间"

### AC4: 活动名称必须唯一
- **Given** 已存在名为"双十一大促"的活动
- **When** 再次创建同名活动
- **Then** 抛出业务异常，提示"活动名称已存在"

---

## 测试清单 📝

```markdown
- [ ] 创建活动成功，返回活动信息（含ID）
- [ ] 创建活动成功，状态为 DRAFT
- [ ] 活动名称为空时，抛出 IllegalArgumentException
- [ ] 开始时间早于当前时间，抛出 IllegalArgumentException
- [ ] 活动名称已存在，抛出 BusinessException
```

---

## 技术备注

- **实体**: `Campaign`
- **DTO**: `CreateCampaignRequest(name, startTime)`
- **枚举**: `CampaignStatus { DRAFT, RUNNING, PAUSED, COMPLETED }`
- **Service**: `CampaignService.createCampaign(request)`

---

## TDD 演示步骤

1. 🔴 **Red**: 写第一个测试 - 创建活动成功
2. 🟢 **Green**: 实现 Campaign、CreateCampaignRequest、CampaignService
3. 🔴 **Red**: 写第二个测试 - 名称为空
4. 🟢 **Green**: 添加名称校验
5. 🔴 **Red**: 写第三个测试 - 时间校验
6. 🟢 **Green**: 添加时间校验
7. 🔴 **Red**: 写第四个测试 - 唯一性校验
8. 🟢 **Green**: 添加唯一性校验
9. 🔵 **Refactor**: 提取 CampaignValidator
