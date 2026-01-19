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

# 安装 curl（用于健康检查）
RUN apk add --no-cache curl

# 添加非 root 用户
RUN addgroup -S spring && adduser -S spring -G spring

# 创建日志目录
RUN mkdir -p /app/logs && chown -R spring:spring /app

# 从构建阶段复制 JAR 包
COPY --from=builder /app/build/libs/*.jar app.jar

# 修改文件权限
RUN chown -R spring:spring /app

# 切换到非 root 用户
USER spring:spring

# 健康检查
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# 暴露端口
EXPOSE 8080

# 环境变量默认值（可通过 docker run -e 覆盖）
ENV SPRING_PROFILES_ACTIVE=prod \
    JAVA_OPTS="-Xmx512m -Xms256m -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -Duser.timezone=Asia/Shanghai"

# 启动应用
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
