# Checkstyle 配置说明

## 配置文件

- `checkstyle.xml` - 主配置文件，基于 Google Java Style Guide
- `suppressions.xml` - 抑制规则，排除某些文件或检查

## 主要检查项

### 命名规范
- 包名：小写字母，点分隔
- 类名：大驼峰（PascalCase）
- 方法名：小驼峰（camelCase）
- 常量：大写字母，下划线分隔

### 代码格式
- 行长度：最多 120 字符
- 方法长度：最多 150 行
- 参数数量：最多 7 个
- 使用空格缩进，禁止 Tab

### 代码质量
- 禁止星号导入（`import java.util.*`）
- 移除未使用的导入
- 简化布尔表达式
- 使用 `equals()` 比较字符串

## 运行检查

```bash
# 检查所有代码
./gradlew checkstyleMain checkstyleTest

# 查看报告
open build/reports/checkstyle/main.html
```

## 忽略检查

如果需要临时忽略某些检查：

```java
// CHECKSTYLE.OFF: LineLength
public void veryLongMethodName() {
    // 这里的代码会忽略 LineLength 检查
}
// CHECKSTYLE.ON: LineLength
```

## CI 集成

在 CI 中会自动运行 Checkstyle 检查，如果有违规会导致构建失败。
