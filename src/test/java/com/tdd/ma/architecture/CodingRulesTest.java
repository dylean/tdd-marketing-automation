package com.tdd.ma.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.GeneralCodingRules.*;

/**
 * 编码规范架构守护测试
 * 
 * 确保代码遵循通用的编码规范
 */
@DisplayName("编码规范架构守护测试")
class CodingRulesTest {

    private static final String BASE_PACKAGE = "com.tdd.ma";
    private static JavaClasses importedClasses;

    @BeforeAll
    static void setup() {
        importedClasses = new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages(BASE_PACKAGE);
    }

    @Test
    @DisplayName("不应该使用System.out/System.err打印")
    void no_classes_should_use_standard_streams() {
        NO_CLASSES_SHOULD_ACCESS_STANDARD_STREAMS.check(importedClasses);
    }

    @Test
    @DisplayName("不应该使用java.util.logging")
    void no_classes_should_use_java_util_logging() {
        NO_CLASSES_SHOULD_USE_JAVA_UTIL_LOGGING.check(importedClasses);
    }

    @Test
    @DisplayName("不应该抛出通用异常")
    void no_classes_should_throw_generic_exceptions() {
        NO_CLASSES_SHOULD_THROW_GENERIC_EXCEPTIONS.check(importedClasses);
    }

    @Test
    @DisplayName("不应该使用Joda Time（使用Java 8+ Time API）")
    void no_classes_should_use_jodatime() {
        NO_CLASSES_SHOULD_USE_JODATIME.check(importedClasses);
    }

    @Test
    @DisplayName("领域实体不应该使用Lombok的@Data注解（避免破坏封装性）")
    void domain_entities_should_not_use_lombok_data() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..domain..")
                .and().haveSimpleNameNotEndingWith("Repository")
                .should().beAnnotatedWith("lombok.Data")
                .because("领域实体应该保持封装性，不应该暴露所有setter");

        rule.check(importedClasses);
    }

    @Test
    @DisplayName("Controller不应该直接依赖Repository")
    void controllers_should_not_depend_on_repositories() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..interfaces..")
                .should().dependOnClassesThat()
                .resideInAPackage("..infrastructure.persistence..");

        rule.check(importedClasses);
    }
}
