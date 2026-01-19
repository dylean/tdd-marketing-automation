# 故事卡：启动营销活动

> 🤖 **用于 AI + Cursor TDD 演示**

---

## 用户故事

**作为** 运营人员  
**我希望** 能够启动营销活动  
**以便于** 活动正式开始执行

---

## 验收标准 (Acceptance Criteria)

### AC1: 启动草稿状态的活动
- **Given** 存在一个状态为 DRAFT 的活动
- **When** 启动该活动
- **Then** 活动状态变为 RUNNING

### AC2: 只有草稿状态的活动才能启动
- **Given** 存在一个状态为 RUNNING 的活动
- **When** 启动该活动
- **Then** 抛出业务异常，提示"只有草稿状态的活动才能启动"

### AC3: 启动不存在的活动
- **Given** 活动 ID 不存在
- **When** 启动该活动
- **Then** 抛出异常，提示"活动不存在"

---

## 测试清单 📝

```markdown
- [ ] 启动草稿状态的活动，状态变为 RUNNING
- [ ] 启动非草稿状态的活动，抛出 BusinessException
- [ ] 启动不存在的活动，抛出异常
```

---

## 技术备注

- **方法**: `CampaignService.startCampaign(campaignId)`
- **状态流转**: `DRAFT → RUNNING`
- **返回值**: 更新后的 `Campaign` 对象

---

## AI TDD 演示步骤

### Step 1: 生成测试 🔴

**Prompt:**
> 为 CampaignService 添加 startCampaign 方法的测试，需要覆盖以下场景：
> 1. 启动草稿状态的活动，状态变为 RUNNING
> 2. 启动非草稿状态的活动，抛出 BusinessException
> 3. 启动不存在的活动，抛出异常

### Step 2: 运行测试，观察失败

- 确认测试编译通过
- 确认测试失败原因正确

### Step 3: 实现功能 🟢

**Prompt:**
> 实现 startCampaign 方法，通过所有测试

### Step 4: 重构 🔵

**Prompt:**
> 检查 CampaignService 的代码，有什么可以优化的地方？

---

## 预期代码结构

```java
// 测试
@Test
@DisplayName("启动草稿状态的活动，状态变为 RUNNING")
void should_start_draft_campaign() {
    // Given: 创建一个草稿活动
    var campaign = createDraftCampaign();
    
    // When: 启动活动
    var result = campaignService.startCampaign(campaign.getId());
    
    // Then: 状态变为 RUNNING
    assertThat(result.getStatus()).isEqualTo(CampaignStatus.RUNNING);
}

// 实现
public Campaign startCampaign(Long campaignId) {
    var campaign = campaignMapper.selectById(campaignId);
    if (campaign == null) {
        throw new IllegalArgumentException("活动不存在");
    }
    if (campaign.getStatus() != CampaignStatus.DRAFT) {
        throw new BusinessException("只有草稿状态的活动才能启动");
    }
    campaign.setStatus(CampaignStatus.RUNNING);
    campaignMapper.updateById(campaign);
    return campaign;
}
```
