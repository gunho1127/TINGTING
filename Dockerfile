# (1) Base image 선택
FROM openjdk:17-jdk-slim

# (2) JAR 파일을 컨테이너에 복사
COPY build/libs/TingTing-0.0.1-SNAPSHOT.jar app.jar

LABEL authors="h2019"

# (3) 앱 실행
ENTRYPOINT ["java", "-jar", "/app.jar"]