FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

COPY gradlew .
COPY gradle ./gradle
COPY build.gradle settings.gradle ./

COPY src ./src

RUN ./gradlew bootJar -x test --no-daemon

EXPOSE 8080

ENTRYPOINT ["java","-jar","build/libs/roost-0.0.1-SNAPSHOT.jar"]