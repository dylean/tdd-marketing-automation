# 故事卡：营销活动数据分析

> 🤖 **用于 AI + Cursor TDD 演示**  
> **场景**：通过 FeignClient 调用外部分析服务，获取活动效果数据并缓存

---

## 用户故事

**作为** 运营人员  
**我希望** 能够查看营销活动的实时效果数据  
**以便于** 评估活动效果，及时优化活动策略

---

## 业务背景

项目已经集成了：
- ✅ Redis 缓存（`RedisCampaignCacheService`）
- ✅ FeignClient 调用外部服务（`AudienceFeignClient`）
- ✅ DDD 分层架构

现在需要添加活动数据分析能力，通过调用外部数据分析服务获取活动效果指标。

---

## 验收标准 (Acceptance Criteria)

### AC1: 获取运行中活动的效果数据
- **Given** 存在一个运行中的活动（ID=100）
- **When** 获取该活动的效果数据
- **Then** 返回包含曝光量、点击量、转化量的数据

### AC2: 首次查询时调用外部服务
- **Given** Redis 中没有缓存
- **When** 获取活动效果数据
- **Then** 调用外部分析服务，并将结果缓存到 Redis（TTL=5分钟）

### AC3: 缓存命中时不调用外部服务
- **Given** Redis 中已有缓存数据
- **When** 获取活动效果数据
- **Then** 直接返回缓存数据，不调用外部服务

### AC4: 只能查询运行中或已完成的活动
- **Given** 一个草稿状态的活动
- **When** 获取效果数据
- **Then** 抛出 `BusinessException`，提示"只有运行中或已完成的活动才有效果数据"

### AC5: 外部服务调用失败时的降级处理
- **Given** 外部分析服务不可用
- **When** 获取效果数据
- **Then** 返回空数据（0曝光、0点击、0转化），并记录日志

---

## 测试清单 📝

### Application 层测试 (CampaignAnalyticsServiceTest)

```markdown
活动数据分析服务
- [ ] 获取运行中活动的效果数据成功
- [ ] 缓存命中时不调用外部服务
- [ ] 缓存未命中时调用外部服务并缓存结果
- [ ] 查询草稿活动的效果数据，抛出 BusinessException
- [ ] 外部服务调用失败，返回空数据
- [ ] 活动不存在，抛出 BusinessException
```

### Infrastructure 层测试 (AnalyticsFeignClientTest)

```markdown
外部分析服务客户端
- [ ] 成功调用外部服务获取数据
- [ ] 服务返回404时抛出 FeignException
- [ ] 服务超时时抛出 FeignException
```

---

## 技术设计

### Domain 层（新增值对象）

```java
// domain/campaign/CampaignAnalytics.java
public class CampaignAnalytics {
    private Long campaignId;
    private Long impressions;      // 曝光量
    private Long clicks;            // 点击量
    private Long conversions;       // 转化量
    private Double clickRate;       // 点击率 (%)
    private Double conversionRate;  // 转化率 (%)
    private LocalDateTime updatedAt;
}
```

### Application 层（新增服务）

```java
// application/campaign/CampaignAnalyticsService.java
@Service
public class CampaignAnalyticsService {
    
    private final CampaignRepository campaignRepository;
    private final AnalyticsServiceClient analyticsClient;
    private final AnalyticsCacheService cacheService;
    
    public CampaignAnalyticsDTO getAnalytics(Long campaignId) {
        // 1. 查询活动，校验状态
        // 2. 尝试从缓存获取
        // 3. 缓存未命中，调用外部服务
        // 4. 缓存结果
        // 5. 返回 DTO
    }
}
```

### Infrastructure 层（新增 FeignClient）

```java
// infrastructure/external/AnalyticsFeignClient.java
@FeignClient(name = "analytics-service", url = "${analytics.service.url}")
public interface AnalyticsFeignClient {
    
    @GetMapping("/api/analytics/campaigns/{campaignId}")
    AnalyticsResponse getAnalytics(@PathVariable Long campaignId);
}

// infrastructure/external/AnalyticsResponse.java
public record AnalyticsResponse(
    Long campaignId,
    Long impressions,
    Long clicks,
    Long conversions,
    LocalDateTime timestamp
) {}
```

### Cache 层（新增缓存服务）

```java
// infrastructure/cache/AnalyticsCacheService.java
@Service
public class RedisAnalyticsCacheService {
    
    private static final String CACHE_KEY_PREFIX = "analytics:campaign:";
    private static final long CACHE_TTL_MINUTES = 5;
    
    public CampaignAnalytics get(Long campaignId) { }
    public void put(Long campaignId, CampaignAnalytics analytics) { }
}
```

---

## AI TDD 演示步骤

### Step 1: 生成领域对象测试 🤖

**Prompt 1:**
```
基于现有的 DDD 架构，为营销活动数据分析功能创建领域值对象 CampaignAnalytics。

需求：
1. 包含字段：campaignId, impressions, clicks, conversions, updatedAt
2. 提供点击率和转化率的计算方法（避免除零错误）
3. 使用 @Test 和 @DisplayName 注解

请先生成测试类 CampaignAnalyticsTest
```

**验证**：
- 查看生成的测试是否符合项目规范
- 运行测试，确认失败原因

---

### Step 2: 生成领域对象实现 🤖

**Prompt 2:**
```
实现 CampaignAnalytics 值对象，通过所有测试。

要求：
1. 使用 @Getter 和 final 字段保证不可变性
2. 点击率 = (clicks / impressions) * 100，保留2位小数
3. 转化率 = (conversions / clicks) * 100，保留2位小数
4. 当分母为0时，比率返回 0.0
```

---

### Step 3: 生成应用服务测试 🤖

**Prompt 3:**
```
为 CampaignAnalyticsService 生成测试类。

参考现有的 CampaignApplicationServiceTest 的风格，使用：
- @ExtendWith(MockitoExtension.class)
- @Mock 注解模拟依赖
- @DisplayName 和 @Nested 组织测试

测试场景参考故事卡的验收标准。
```

---

### Step 4: 生成应用服务实现 🤖

**Prompt 4:**
```
实现 CampaignAnalyticsService，通过所有测试。

要求：
1. 注入 CampaignRepository, AnalyticsFeignClient, AnalyticsCacheService
2. 使用 @Transactional(readOnly = true)
3. 异常处理使用 try-catch，失败时返回空数据
4. 缓存 TTL 5分钟
```

---

### Step 5: 生成 FeignClient 和测试 🤖

**Prompt 5:**
```
创建 AnalyticsFeignClient 接口和对应的测试。

参考现有的 AudienceFeignClient 的结构：
1. 使用 @FeignClient 注解
2. 配置 URL 从配置文件读取
3. 创建 AnalyticsResponse record
4. 生成相应的测试类（可选，主要演示 AI 生成能力）
```

---

### Step 6: 集成并验证 🤖

**Prompt 6:**
```
检查以下内容是否完整：
1. 是否需要添加 REST Controller
2. 是否需要更新 application.yml 配置
3. 代码是否符合现有的架构规范（ArchUnit 测试）
4. 是否有改进建议（缓存策略、异常处理等）
```

---

## 演示重点

1. **AI 辅助效率**：展示 AI 如何快速生成测试和实现
2. **代码质量把控**：验证 AI 生成的代码是否符合规范
3. **迭代优化**：通过多轮 Prompt 优化代码质量
4. **人机协作**：AI 生成初版，人工 Review 和调整
5. **测试覆盖**：确保 AI 生成的代码有完整的测试覆盖

---

## 配置文件更新

### application.yml

```yaml
# 分析服务配置
analytics:
  service:
    url: ${ANALYTICS_SERVICE_URL:http://localhost:8082}

# Feign 配置
feign:
  client:
    config:
      analytics-service:
        connectTimeout: 3000
        readTimeout: 5000
```

### application-test.yml

```yaml
analytics:
  service:
    url: http://localhost:8082
```

---

## Mock 服务（测试用）

```java
// test/.../MockAnalyticsService.java
@Service
@Primary
@Profile("test")
public class MockAnalyticsService implements AnalyticsServiceClient {
    
    @Override
    public AnalyticsResponse getAnalytics(Long campaignId) {
        return new AnalyticsResponse(
            campaignId,
            10000L,  // 曝光
            500L,    // 点击
            50L,     // 转化
            LocalDateTime.now()
        );
    }
}
```

---

## 预期时间

- AI 生成代码：15-20分钟
- 人工 Review 和调整：10-15分钟
- 集成测试：5-10分钟
- 总计：**30-45分钟**

---

## AI Prompt 技巧总结

1. **明确上下文**：告诉 AI 当前项目的架构和规范
2. **分步生成**：先测试后实现，逐步推进
3. **提供示例**：引用现有代码作为参考
4. **明确要求**：指定注解、命名规范、异常处理方式
5. **迭代优化**：第一版不完美没关系，通过多轮对话改进
