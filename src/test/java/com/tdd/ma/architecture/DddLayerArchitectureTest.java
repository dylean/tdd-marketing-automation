package com.tdd.ma.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.library.Architectures;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

/**
 * DDD 分层架构守护测试
 * 
 * 确保代码遵循 DDD 分层架构的依赖规则：
 * - Domain 层不依赖任何其他层
 * - Application 层只能依赖 Domain 层
 * - Interfaces 层只能依赖 Application 层
 * - Infrastructure 层可以依赖 Domain 层和 Application 层（实现接口）
 */
@DisplayName("DDD 分层架构守护测试")
class DddLayerArchitectureTest {

    private static final String BASE_PACKAGE = "com.tdd.ma";
    private static JavaClasses importedClasses;

    @BeforeAll
    static void setup() {
        importedClasses = new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages(BASE_PACKAGE);
    }

    @Test
    @DisplayName("DDD分层架构：各层之间的依赖关系必须符合规范")
    void ddd_layer_dependencies_should_be_respected() {
        Architectures.LayeredArchitecture architecture = layeredArchitecture()
                .consideringOnlyDependenciesInLayers()
                // 定义各层
                .layer("Interfaces").definedBy(BASE_PACKAGE + ".interfaces..")
                .layer("Application").definedBy(BASE_PACKAGE + ".application..")
                .layer("Domain").definedBy(BASE_PACKAGE + ".domain..")
                .layer("Infrastructure").definedBy(BASE_PACKAGE + ".infrastructure..")
                
                // 定义依赖规则
                // Domain 层是核心，不依赖任何其他层
                .whereLayer("Domain").mayNotAccessAnyLayer()
                
                // Application 层只能访问 Domain 层
                .whereLayer("Application").mayOnlyAccessLayers("Domain")
                
                // Interfaces 层只能访问 Application 层和 Domain 层（DTO转换可能需要）
                .whereLayer("Interfaces").mayOnlyAccessLayers("Application", "Domain")
                
                // Infrastructure 层可以访问 Domain 层和 Application 层（实现接口）
                .whereLayer("Infrastructure").mayOnlyAccessLayers("Domain", "Application");

        architecture.check(importedClasses);
    }

    @Test
    @DisplayName("Domain层不应该依赖Spring框架")
    void domain_should_not_depend_on_spring() {
        com.tngtech.archunit.lang.ArchRule rule = com.tngtech.archunit.lang.syntax.ArchRuleDefinition
                .noClasses()
                .that().resideInAPackage(BASE_PACKAGE + ".domain..")
                .should().dependOnClassesThat()
                .resideInAnyPackage("org.springframework..");

        rule.check(importedClasses);
    }

    @Test
    @DisplayName("Domain层不应该依赖MyBatis")
    void domain_should_not_depend_on_mybatis() {
        com.tngtech.archunit.lang.ArchRule rule = com.tngtech.archunit.lang.syntax.ArchRuleDefinition
                .noClasses()
                .that().resideInAPackage(BASE_PACKAGE + ".domain..")
                .should().dependOnClassesThat()
                .resideInAnyPackage("com.baomidou.mybatisplus..", "org.apache.ibatis..");

        rule.check(importedClasses);
    }
}
