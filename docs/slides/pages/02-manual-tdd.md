---
layout: center
class: text-center
---

# 💻 手动 TDD 实战

<div class="text-2xl mt-8 text-gray-400">
创建营销活动 — 演示经典 TDD 流程
</div>

---

# 需求 & 测试清单

<div class="grid grid-cols-2 gap-8 mt-6">

<div class="p-6 bg-gradient-to-r from-violet-500/20 to-fuchsia-500/20 rounded-2xl">

### 需求

> 作为运营人员，我需要能够创建营销活动，
> 活动名称必须唯一，开始时间必须晚于当前时间

</div>

<div class="p-6 bg-blue-500/20 rounded-2xl">

### 测试清单 📝

```markdown
- [ ] 创建活动成功，返回活动信息
- [ ] 活动名称为空时，抛出异常
- [ ] 开始时间早于当前时间，抛出异常
- [ ] 活动名称已存在，抛出异常
```

</div>

</div>

<v-click>

<div class="mt-6 p-4 bg-yellow-500/20 rounded-lg text-center">

💡 先列出所有场景，这就是你的开发路线图！

</div>

</v-click>

---

# 🔴 第一个测试：创建活动成功

```java {all|1-3|8-14|16-20}
@SpringBootTest
@Transactional
class CampaignServiceTest {

    @Autowired
    private CampaignService campaignService;

    @Test
    @DisplayName("创建营销活动成功，返回活动信息")
    void should_create_campaign_successfully() {
        // Given
        var request = new CreateCampaignRequest(
            "双十一大促", 
            LocalDateTime.now().plusDays(7)
        );

        // When
        var campaign = campaignService.createCampaign(request);

        // Then
        assertThat(campaign.getId()).isNotNull();
        assertThat(campaign.getName()).isEqualTo("双十一大促");
        assertThat(campaign.getStatus()).isEqualTo(CampaignStatus.DRAFT);
    }
}
```

---
layout: center
class: text-center
---

# 运行测试 → 🔴 失败！

<div class="text-red-400 text-2xl mt-8 font-mono">
❌ 编译失败：CampaignService 不存在
</div>

<div class="mt-8 text-xl text-gray-400">
这就对了！测试先行，代码后写
</div>

---

# 🟢 实现代码

<div class="grid grid-cols-2 gap-6">

<div>

### 实体 & DTO

```java
@Data
@TableName("t_campaign")
public class Campaign {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private LocalDateTime startTime;
    private CampaignStatus status;
    private LocalDateTime createdAt;
}
```

```java
public record CreateCampaignRequest(
    String name, 
    LocalDateTime startTime
) {}
```

</div>

<div>

### Service

```java
@Service
@RequiredArgsConstructor
public class CampaignService {

    private final CampaignMapper mapper;

    public Campaign createCampaign(
            CreateCampaignRequest request) {
        var campaign = new Campaign();
        campaign.setName(request.name());
        campaign.setStartTime(request.startTime());
        campaign.setStatus(CampaignStatus.DRAFT);
        campaign.setCreatedAt(LocalDateTime.now());
        
        mapper.insert(campaign);
        return campaign;
    }
}
```

</div>

</div>

---
layout: center
class: text-center
---

# 运行测试 → 🟢 通过！

<div class="text-green-400 text-6xl mt-8">✅</div>

<div class="mt-8 text-2xl">继续下一个测试...</div>

---

# 🔴🟢 测试2：名称为空

<div class="grid grid-cols-2 gap-6">

<div>

### 测试

```java
@Test
@DisplayName("活动名称为空时，抛出异常")
void should_throw_when_name_blank() {
    var request = new CreateCampaignRequest(
        "", 
        LocalDateTime.now().plusDays(1)
    );

    assertThatThrownBy(() -> 
        campaignService.createCampaign(request))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("活动名称不能为空");
}
```

</div>

<v-click>

<div>

### 实现

```java
public Campaign createCampaign(
        CreateCampaignRequest request) {
    // 校验
    if (request.name() == null 
        || request.name().isBlank()) {
        throw new IllegalArgumentException(
            "活动名称不能为空");
    }
    
    // ... 创建逻辑
}
```

<div class="mt-4 text-green-400 text-xl">🟢 通过！</div>

</div>

</v-click>

</div>

---

# 🔴🟢 测试3 & 4：时间校验 & 唯一性

```java
@Test
void should_throw_when_start_time_is_past() {
    var request = new CreateCampaignRequest("活动", LocalDateTime.now().minusDays(1));
    
    assertThatThrownBy(() -> campaignService.createCampaign(request))
        .hasMessage("活动开始时间必须晚于当前时间");
}

@Test
void should_throw_when_name_exists() {
    // 先创建一个活动
    campaignService.createCampaign(new CreateCampaignRequest("双十一", futureTime));
    
    // 尝试创建同名活动
    assertThatThrownBy(() -> 
        campaignService.createCampaign(new CreateCampaignRequest("双十一", futureTime)))
        .isInstanceOf(BusinessException.class)
        .hasMessage("活动名称已存在");
}
```

---

# 🔵 重构：提取校验器

```java {all|5-6|8-11}
@Service
@RequiredArgsConstructor
public class CampaignService {
    private final CampaignMapper mapper;
    private final CampaignValidator validator;

    public Campaign createCampaign(CreateCampaignRequest request) {
        validator.validate(request);           // 校验参数
        checkNameUnique(request.name());       // 校验唯一性
        
        return saveCampaign(request);          // 保存
    }
    
    private void checkNameUnique(String name) {
        if (mapper.existsByName(name)) {
            throw new BusinessException("活动名称已存在");
        }
    }
}
```

<v-click>

<div class="mt-4 text-green-400 text-xl text-center">
重构后运行测试 → ✅ ✅ ✅ ✅ 全部通过！
</div>

</v-click>
