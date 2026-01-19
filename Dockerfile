# 多阶段构建 Dockerfile for Spring Boot 应用

# 阶段 1: 构建阶段
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

# 复制 Gradle 配置文件
COPY build.gradle.kts settings.gradle.kts gradlew ./
COPY gradle ./gradle

# 下载依赖（利用 Docker 缓存）
RUN ./gradlew dependencies --no-daemon || return 0

# 复制源代码
COPY src ./src

# 构建应用
RUN ./gradlew bootJar --no-daemon

# 阶段 2: 运行阶段
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# 添加非 root 用户
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# 从构建阶段复制 JAR 包
COPY --from=builder /app/build/libs/*.jar app.jar

# 健康检查
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# 暴露端口
EXPOSE 8080

# JVM 参数优化
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:+UseG1GC"

# 启动应用
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
