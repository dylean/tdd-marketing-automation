# 代码质量工具使用指南

项目已集成 **Checkstyle** 和 **SpotBugs** 两个代码质量检查工具。

---

## 🎯 工具说明

### Checkstyle
**用途**: 代码风格检查  
**规则**: Google Java Style Guide  
**检查内容**:
- 命名规范
- 代码格式（缩进、空格、换行）
- 导入语句
- Javadoc 注释

### SpotBugs
**用途**: 静态代码分析，发现潜在 bug  
**检查内容**:
- 空指针风险
- 资源泄漏
- 性能问题
- 安全漏洞
- 多线程问题

---

## 🚀 使用方式

### 检查所有代码

```bash
# 运行所有代码质量检查
./gradlew check -x test

# 只运行 Checkstyle
./gradlew checkstyleMain checkstyleTest

# 只运行 SpotBugs
./gradlew spotbugsMain spotbugsTest
```

### 查看报告

检查完成后，打开 HTML 报告查看详情：

```bash
# Checkstyle 报告
open build/reports/checkstyle/main.html
open build/reports/checkstyle/test.html

# SpotBugs 报告
open build/reports/spotbugs/main.html
open build/reports/spotbugs/test.html
```

---

## 📊 当前状态

运行 `./gradlew checkstyleMain spotbugsMain` 后:

| 工具 | 状态 | 警告数 | 说明 |
|------|------|--------|------|
| **Checkstyle** | ⚠️ 警告 | 439 | 主要是格式问题 |
| **SpotBugs** | ⚠️ 警告 | 若干 | 潜在 bug |

**注意**: 当前配置为 `ignoreFailures = true`，不会中断构建。

---

## 🔧 常见问题修复

### Checkstyle 警告

#### 1. 缩进问题
```java
// ❌ 错误：4空格缩进
public class Example {
    void method() {
        System.out.println("Hello");
    }
}

// ✅ 正确：2空格缩进（Google Style）
public class Example {
  void method() {
    System.out.println("Hello");
  }
}
```

**IDEA 配置**:
1. `Settings` → `Editor` → `Code Style` → `Java`
2. `Tabs and Indents`:
   - Tab size: 2
   - Indent: 2
   - Continuation indent: 4
3. `Scheme` → `Import Scheme` → 选择 `GoogleStyle`

#### 2. Javadoc 问题
```java
// ❌ 错误：缺少结束符
/**
 * REST 控制器
 */

// ✅ 正确：有结束符
/**
 * REST 控制器.
 */
```

#### 3. 导入顺序
```java
// ❌ 错误：星号导入
import java.util.*;

// ✅ 正确：明确导入
import java.util.List;
import java.util.ArrayList;
```

### SpotBugs 警告

#### 1. 暴露内部表示 (EI_EXPOSE_REP)
```java
// ❌ 问题：返回可变对象的引用
public LocalDateTime getCreatedAt() {
    return createdAt;
}

// ✅ 方案1：返回副本
public LocalDateTime getCreatedAt() {
    return LocalDateTime.from(createdAt);
}

// ✅ 方案2：使用不可变对象（推荐）
// LocalDateTime 本身是不可变的，已在排除规则中
```

#### 2. 未使用的返回值
```java
// ❌ 问题：忽略返回值
list.stream().filter(x -> x > 0);

// ✅ 正确：使用返回值
List<Integer> result = list.stream()
    .filter(x -> x > 0)
    .collect(Collectors.toList());
```

---

## 🎨 IDE 集成

### IntelliJ IDEA

#### 安装 Checkstyle 插件
1. `Settings` → `Plugins`
2. 搜索 "CheckStyle-IDEA"
3. 安装并重启

#### 配置 Checkstyle
1. `Settings` → `Tools` → `Checkstyle`
2. 点击 "+" 添加配置
3. 选择 `Use a Checkstyle configuration accessible via HTTP`
4. URL: `https://raw.githubusercontent.com/checkstyle/checkstyle/checkstyle-10.12.7/src/main/resources/google_checks.xml`
5. 勾选为 Active

#### 安装 SpotBugs 插件
1. `Settings` → `Plugins`
2. 搜索 "SpotBugs"
3. 安装并重启

### VS Code

#### Checkstyle 扩展
1. 安装 "Checkstyle for Java" 扩展
2. 在 `.vscode/settings.json` 添加:
```json
{
  "java.checkstyle.configuration": "google_checks",
  "java.checkstyle.version": "10.12.7"
}
```

---

## 🔄 CI 集成

代码质量检查已集成到 GitHub Actions CI 流程：

```yaml
# .github/workflows/ci.yml
- name: 代码质量检查（跳过测试）
  run: ./gradlew check -x test --no-daemon
  continue-on-error: true
```

**特点**:
- ✅ 自动运行
- ✅ 生成报告
- ⚠️ 不中断构建（`continue-on-error: true`）

**建议**: 在本地修复大部分问题后再推送代码。

---

## 📈 改进建议

### 短期（1-2周）
- [ ] 修复高优先级的 SpotBugs 警告
- [ ] 统一代码格式（运行 `./gradlew spotlessApply` - 待添加）
- [ ] 为公共 API 添加 Javadoc

### 中期（1个月）
- [ ] 将 Checkstyle 设置为 `ignoreFailures = false`
- [ ] 减少警告数量到 < 100
- [ ] 添加自定义 Checkstyle 规则

### 长期
- [ ] 集成 SonarQube
- [ ] 添加代码覆盖率检查
- [ ] 设置质量门禁

---

## 🛠️ 自定义配置

### 修改 Checkstyle 规则

当前使用 Google Style，如需自定义：

```kotlin
// build.gradle.kts
checkstyle {
    // 使用本地配置文件
    configFile = file("${project.rootDir}/config/checkstyle/checkstyle.xml")
}
```

### 排除文件

编辑 `config/checkstyle/suppressions.xml`:
```xml
<suppress checks=".*" files=".*Test\.java"/>
```

### 调整 SpotBugs 级别

```kotlin
spotbugs {
    // LOW, MEDIUM, HIGH
    reportLevel.set(com.github.spotbugs.snom.Confidence.HIGH)
}
```

---

## 📚 参考资源

- [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html)
- [Checkstyle 官方文档](https://checkstyle.org/)
- [SpotBugs 官方文档](https://spotbugs.readthedocs.io/)
- [Checkstyle 规则列表](https://checkstyle.org/checks.html)
- [SpotBugs Bug 描述](https://spotbugs.readthedocs.io/en/stable/bugDescriptions.html)

---

**维护者**: Code Quality Team  
**最后更新**: 2026-01-19  
**版本**: v1.0
