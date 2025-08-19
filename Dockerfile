# ===== build stage (JDK 17) =====
FROM gradle:8.7.0-jdk17-alpine AS build
WORKDIR /workspace

# Gradle 설정 및 gradlew 복사
COPY build.gradle settings.gradle gradlew ./
COPY gradle ./gradle

# gradlew 실행권한 부여
RUN chmod +x gradlew

# 소스 코드 복사
COPY src ./src

# JAR 빌드 (테스트 생략 시 -x test)
RUN ./gradlew clean bootJar -x test --no-daemon

# ===== runtime stage (JRE 17) =====
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# 빌드 단계에서 만든 JAR만 복사
COPY --from=build /workspace/build/libs/*SNAPSHOT*.jar /app/app.jar

EXPOSE 8080

# 메모리 옵션과 함께 실행
ENTRYPOINT ["java", "-Xmx256m", "-Xms128m", "-XX:+UseSerialGC", "-jar", "/app/app.jar"]
