# SpotBugs 配置说明

## 配置文件

- `spotbugs-exclude.xml` - 排除规则，忽略某些类型的 bug 或文件

## 检查级别

- **Effort**: MAX - 最高检测级别
- **Confidence**: LOW - 报告低、中、高置信度的问题

## 主要检查项

### 代码缺陷
- 空指针引用
- 资源泄漏（未关闭的流、连接等）
- 无限循环
- 错误的异常处理

### 性能问题
- 低效的字符串拼接
- 装箱/拆箱
- 未使用的对象

### 安全问题
- SQL 注入风险
- 路径遍历漏洞
- 硬编码凭证

### 多线程问题
- 竞态条件
- 死锁风险
- 不安全的同步

## 运行检查

```bash
# 检查所有代码
./gradlew spotbugsMain spotbugsTest

# 查看报告
open build/reports/spotbugs/main.html
```

## 忽略特定 Bug

使用注解忽略特定类型的 bug：

```java
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressFBWarnings(
    value = "EI_EXPOSE_REP",
    justification = "DTO 对象，允许返回内部引用"
)
public LocalDateTime getCreatedAt() {
    return createdAt;
}
```

## 常见误报

### 1. EI_EXPOSE_REP / EI_EXPOSE_REP2
- **问题**: 暴露内部表示
- **原因**: Lombok 生成的 getter/setter
- **解决**: 已在 exclude.xml 中排除 domain 和 dto 包

### 2. SE_NO_SERIALVERSIONID
- **问题**: 缺少 serialVersionUID
- **原因**: DTO 不需要显式序列化版本
- **解决**: 已全局排除

### 3. SE_BAD_FIELD
- **问题**: 不可序列化的字段
- **原因**: Spring Configuration 类不需要序列化
- **解决**: 已排除 Configuration 类

## CI 集成

在 CI 中会自动运行 SpotBugs 检查，如果发现 bug 会导致构建失败。
