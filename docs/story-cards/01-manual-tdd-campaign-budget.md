# 故事卡：营销活动预算管理

> 📋 **用于手动 TDD 演示**  
> **场景**：在既有的 `Campaign` 聚合根上添加预算管理能力

---

## 用户故事

**作为** 运营人员  
**我希望** 能够为营销活动设置和管理预算  
**以便于** 控制活动成本，避免超支

---

## 业务背景

现有的 `Campaign` 聚合根已经实现了基础的生命周期管理（创建、启动、暂停、完成），现在需要添加预算管理能力，包括：
- 设置活动预算
- 记录已花费金额
- 检查预算是否超支
- 冻结超支活动

---

## 验收标准 (Acceptance Criteria)

### AC1: 设置活动预算
- **Given** 一个草稿状态的活动
- **When** 设置预算为 10000.00 元
- **Then** 活动预算为 10000.00 元，已花费为 0.00 元

### AC2: 预算金额必须为正数
- **Given** 一个草稿状态的活动
- **When** 设置预算为 -100.00 元
- **Then** 抛出 `IllegalArgumentException`，提示"预算金额必须大于0"

### AC3: 只有草稿状态的活动可以设置预算
- **Given** 一个运行中的活动
- **When** 尝试设置预算
- **Then** 抛出 `BusinessException`，提示"只有草稿状态的活动才能设置预算"

### AC4: 记录活动花费
- **Given** 活动预算为 10000 元，已花费 3000 元
- **When** 记录新花费 2000 元
- **Then** 已花费变为 5000 元

### AC5: 花费金额必须为正数
- **Given** 一个活动
- **When** 记录花费为 -100 元
- **Then** 抛出 `IllegalArgumentException`，提示"花费金额必须大于0"

### AC6: 检查是否超支
- **Given** 活动预算为 10000 元，已花费 12000 元
- **When** 检查是否超支
- **Then** 返回 true

### AC7: 暂停超支的活动
- **Given** 运行中的活动，预算为 10000 元，已花费 10500 元
- **When** 暂停超支活动
- **Then** 活动状态变为 PAUSED

---

## 测试清单 📝

### Domain 层测试 (CampaignTest)

```markdown
Campaign 预算管理
- [ ] 设置预算成功，预算为指定金额，已花费为0
- [ ] 设置预算为负数，抛出 IllegalArgumentException
- [ ] 设置预算为0，抛出 IllegalArgumentException
- [ ] 运行中的活动不能设置预算，抛出 BusinessException
- [ ] 记录花费成功，已花费累加
- [ ] 记录花费为负数，抛出 IllegalArgumentException
- [ ] 预算为10000，花费12000，超支返回 true
- [ ] 预算为10000，花费8000，不超支返回 false
- [ ] 未设置预算的活动记录花费，抛出 BusinessException
```

---

## 技术设计

### Domain 层修改

在 `Campaign` 聚合根中添加：

```java
// 新增字段
private BigDecimal budget;           // 预算金额
private BigDecimal spentAmount;      // 已花费金额

// 新增方法
public void setBudget(BigDecimal budget) {
    // 校验预算金额
    // 校验活动状态
}

public void recordSpending(BigDecimal amount) {
    // 校验花费金额
    // 校验是否已设置预算
    // 累加已花费金额
}

public boolean isOverBudget() {
    // 判断是否超支
}

public void pauseIfOverBudget() {
    // 如果超支则暂停活动
}
```

### Application 层添加

```java
// CampaignApplicationService 新增方法
public CampaignDTO setBudget(Long campaignId, BigDecimal budget);
public CampaignDTO recordSpending(Long campaignId, BigDecimal amount);
```

### DTO 更新

```java
// CampaignDTO 添加字段
BigDecimal budget;
BigDecimal spentAmount;
Boolean isOverBudget;
```

---

## TDD 演示步骤（红-绿-重构循环）

### 第一个迭代：设置预算 🔁

**🔴 Red - 写测试**
```java
@Test
@DisplayName("设置预算成功，预算为指定金额，已花费为0")
void should_set_budget_successfully() {
    // Given
    Campaign campaign = createDraftCampaign();
    BigDecimal budget = new BigDecimal("10000.00");
    
    // When
    campaign.setBudget(budget);
    
    // Then
    assertThat(campaign.getBudget()).isEqualTo(budget);
    assertThat(campaign.getSpentAmount()).isEqualTo(BigDecimal.ZERO);
}
```

**🟢 Green - 最简实现**
- 在 `Campaign` 添加字段和 getter
- 实现 `setBudget` 方法

**🔵 Refactor - 重构**
- 提取常量、优化代码结构

---

### 第二个迭代：预算校验 🔁

**🔴 Red - 写测试**
```java
@Test
@DisplayName("设置预算为负数，抛出异常")
void should_throw_when_budget_is_negative() {
    Campaign campaign = createDraftCampaign();
    assertThatThrownBy(() -> campaign.setBudget(new BigDecimal("-100")))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("预算金额必须大于0");
}

@Test
@DisplayName("运行中的活动不能设置预算")
void should_throw_when_set_budget_for_running_campaign() {
    Campaign campaign = createRunningCampaign();
    assertThatThrownBy(() -> campaign.setBudget(new BigDecimal("10000")))
        .isInstanceOf(BusinessException.class)
        .hasMessage("只有草稿状态的活动才能设置预算");
}
```

**🟢 Green - 添加校验逻辑**

**🔵 Refactor - 提取 `validateBudget` 方法**

---

### 第三个迭代：记录花费 🔁

**🔴 Red - 写测试**
```java
@Test
@DisplayName("记录花费成功，已花费累加")
void should_record_spending_successfully() {
    // Given
    Campaign campaign = createCampaignWithBudget(new BigDecimal("10000"));
    campaign.recordSpending(new BigDecimal("3000"));
    
    // When
    campaign.recordSpending(new BigDecimal("2000"));
    
    // Then
    assertThat(campaign.getSpentAmount()).isEqualTo(new BigDecimal("5000"));
}
```

**🟢 Green - 实现 `recordSpending` 方法**

**🔵 Refactor - 优化累加逻辑**

---

### 第四个迭代：超支检测 🔁

**🔴 Red - 写测试**
```java
@Test
@DisplayName("预算10000，花费12000，超支返回true")
void should_detect_over_budget() {
    Campaign campaign = createCampaignWithBudget(new BigDecimal("10000"));
    campaign.recordSpending(new BigDecimal("12000"));
    
    assertThat(campaign.isOverBudget()).isTrue();
}
```

**🟢 Green - 实现 `isOverBudget` 方法**

---

## 演示重点

1. **严格遵循 TDD 节奏**：红-绿-重构，不跳步
2. **测试驱动设计**：通过测试发现 API 设计问题
3. **小步前进**：每次只添加一个测试用例
4. **及时重构**：绿灯后立即重构
5. **测试即文档**：清晰的测试名称和结构

---

## 数据库迁移

```sql
-- V202601191500__add_campaign_budget.sql
ALTER TABLE t_campaign 
ADD COLUMN budget DECIMAL(15,2) COMMENT '预算金额',
ADD COLUMN spent_amount DECIMAL(15,2) DEFAULT 0.00 COMMENT '已花费金额';
```

---

## 预期时间

- Domain 层 TDD：30-40分钟
- Application 层集成：10分钟
- 总计：**40-50分钟**
