# ===== (추가) React build stage =====
FROM node:18 AS webbuild
WORKDIR /web
# 의존성 설치
COPY frontend/package*.json ./
RUN npm ci
# 소스 복사 & 빌드
COPY frontend/ ./
RUN npm run build    # => /web/build 생성

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

# (추가) React 빌드 산출물 static에 주입 (기존 파일 초기화 후)
RUN mkdir -p src/main/resources/static && rm -rf src/main/resources/static/* || true
COPY --from=webbuild /web/build/ ./src/main/resources/static/

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
