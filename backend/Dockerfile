# =========================
# Build stage
# =========================
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /workspace

# 先複製 Maven 設定與 pom 以利用快取預取相依
COPY pom.xml .
COPY .mvn/ .mvn/
COPY mvnw .
RUN chmod +x mvnw || true

# 預先下載相依（無 src，提高快取命中）
RUN mvn -B -q -DskipTests dependency:go-offline

# 複製原始碼並打包
COPY src ./src
RUN mvn -B -q -DskipTests package

# =========================
# Runtime stage
# =========================
FROM eclipse-temurin:21-jre-ubi9-minimal AS runtime
ENV LANG=C.UTF-8 TZ=Asia/Taipei
WORKDIR /app

# 安全性：建立非 root 使用者
RUN useradd -r -u 10001 appuser

# 複製 fat jar（若使用 spring-boot repackage，target 下通常只有一個可執行 jar）
COPY --from=build /workspace/target/*.jar /app/app.jar

# 生產建議 JVM 參數（可視資源調整）
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75 \
 -Dfile.encoding=UTF-8 -Dsun.jnu.encoding=UTF-8"

# Spring 以環境變數驅動；K8s 會注入 DB_*、APP_JWT_* 等
ENV SERVER_PORT=8080 \
    SERVER_SERVLET_CONTEXT_PATH=/api

EXPOSE 8080
USER appuser

# Docker 層級健康檢查（K8s 仍以 probe 為準，可保留）
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
 CMD sh -c 'wget -qO- "http://127.0.0.1:8080${SERVER_SERVLET_CONTEXT_PATH:-/api}/actuator/health" | grep -q UP || exit 1'

ENTRYPOINT ["sh","-c","exec java $JAVA_OPTS -jar /app/app.jar"]
