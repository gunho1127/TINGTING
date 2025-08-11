# ===== build stage (JDK 17) =====
FROM gradle:8.7.0-jdk17-alpine AS build
WORKDIR /workspace

# 캐시 최적화
COPY build.gradle settings.gradle gradlew ./
COPY gradle ./gradle
RUN ./gradlew --version >/dev/null 2>&1 || true

# 소스 복사 후 빌드(테스트 생략 시 -x test)
COPY src ./src
RUN ./gradlew clean bootJar -x test

# ===== runtime stage (JRE 17) =====
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
# 생성된 부트 JAR를 app.jar로 복사
COPY --from=build /workspace/build/libs/*SNAPSHOT*.jar /app/app.jar
# 필요 시 포트 조정
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
