package com.tdd.ma.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

/**
 * 命名规范架构守护测试
 * 
 * 确保代码遵循统一的命名规范
 */
@DisplayName("命名规范架构守护测试")
class NamingConventionTest {

    private static final String BASE_PACKAGE = "com.tdd.ma";
    private static JavaClasses importedClasses;

    @BeforeAll
    static void setup() {
        importedClasses = new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages(BASE_PACKAGE);
    }

    @Test
    @DisplayName("Controller类应该以Controller结尾")
    void controllers_should_be_suffixed() {
        ArchRule rule = classes()
                .that().resideInAPackage("..interfaces.rest..")
                .and().areAnnotatedWith(org.springframework.web.bind.annotation.RestController.class)
                .should().haveSimpleNameEndingWith("Controller");

        rule.check(importedClasses);
    }

    @Test
    @DisplayName("Application Service类应该以ApplicationService或Service结尾")
    void application_services_should_be_suffixed() {
        ArchRule rule = classes()
                .that().resideInAPackage("..application..")
                .and().areAnnotatedWith(org.springframework.stereotype.Service.class)
                .should().haveSimpleNameEndingWith("Service")
                .orShould().haveSimpleNameEndingWith("ApplicationService");

        rule.check(importedClasses);
    }

    @Test
    @DisplayName("Repository接口应该以Repository结尾")
    void repositories_should_be_suffixed() {
        ArchRule rule = classes()
                .that().resideInAPackage("..domain..")
                .and().areInterfaces()
                .and().haveSimpleNameContaining("Repository")
                .should().haveSimpleNameEndingWith("Repository");

        rule.check(importedClasses);
    }

    @Test
    @DisplayName("Mapper接口应该以Mapper结尾")
    void mappers_should_be_suffixed() {
        ArchRule rule = classes()
                .that().resideInAPackage("..infrastructure.persistence..")
                .and().areInterfaces()
                .should().haveSimpleNameEndingWith("Mapper")
                .orShould().haveSimpleNameEndingWith("Repository");

        rule.check(importedClasses);
    }
}
