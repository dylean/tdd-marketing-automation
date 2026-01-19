plugins {
    java
    id("org.springframework.boot") version "3.2.0"
    id("io.spring.dependency-management") version "1.1.4"
    checkstyle
    id("com.github.spotbugs") version "6.0.7"
}

group = "com.tdd"
version = "1.0.0-SNAPSHOT"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

extra["springCloudVersion"] = "2023.0.0"

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
    }
}

val mybatisPlusVersion = "3.5.5"
val archunitVersion = "1.2.1"

dependencies {
    // Spring Boot Starters
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")

    // Spring Cloud OpenFeign
    implementation("org.springframework.cloud:spring-cloud-starter-openfeign")

    // MyBatis Plus
    implementation("com.baomidou:mybatis-plus-spring-boot3-starter:$mybatisPlusVersion")

    // Database
    runtimeOnly("com.mysql:mysql-connector-j")

    // Flyway
    implementation("org.flywaydb:flyway-core")
    implementation("org.flywaydb:flyway-mysql")

    // Lombok
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    testCompileOnly("org.projectlombok:lombok")
    testAnnotationProcessor("org.projectlombok:lombok")

    // Testing
    testImplementation("org.springframework.boot:spring-boot-starter-test")

    // ArchUnit - 架构守护
    testImplementation("com.tngtech.archunit:archunit-junit5:$archunitVersion")
    
    // SpotBugs annotations
    compileOnly("com.github.spotbugs:spotbugs-annotations:4.8.3")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

// ==================== Checkstyle 配置 ====================
checkstyle {
    toolVersion = "10.12.7"
    // 使用 Google Java Style
    config = resources.text.fromUri("https://raw.githubusercontent.com/checkstyle/checkstyle/checkstyle-10.12.7/src/main/resources/google_checks.xml")
    isIgnoreFailures = true  // 暂时不让 checkstyle 失败中断构建
    maxWarnings = 100
}

tasks.withType<Checkstyle> {
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}

// ==================== SpotBugs 配置 ====================
spotbugs {
    toolVersion.set("4.8.3")
    effort.set(com.github.spotbugs.snom.Effort.MAX)
    reportLevel.set(com.github.spotbugs.snom.Confidence.MEDIUM)
    excludeFilter.set(file("${project.rootDir}/config/spotbugs/spotbugs-exclude.xml"))
    ignoreFailures.set(true)  // 暂时不让 spotbugs 失败中断构建
}

tasks.withType<com.github.spotbugs.snom.SpotBugsTask> {
    reports.create("html") {
        required.set(true)
        outputLocation.set(file("$buildDir/reports/spotbugs/${this.name}.html"))
    }
    reports.create("xml") {
        required.set(true)
        outputLocation.set(file("$buildDir/reports/spotbugs/${this.name}.xml"))
    }
}

// ==================== Git Hooks 配置 ====================
// 自定义任务：安装 Git Hooks
tasks.register<Exec>("installGitHooks") {
    group = "git"
    description = "安装 Git Hooks"
    
    commandLine("bash", "${project.rootDir}/scripts/install-git-hooks.sh")
    
    doLast {
        println("✅ Git Hooks 已安装")
    }
}

// 在 build 时自动安装 Git Hooks
tasks.named("build") {
    dependsOn("installGitHooks")
}
