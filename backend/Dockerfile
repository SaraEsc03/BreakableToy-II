### Multi-stage Dockerfile for Gradle builds
### Stage 1: build with Gradle
FROM gradle:8.6-jdk21 AS build
WORKDIR /app
COPY build.gradle.kts settings.gradle.kts gradle.properties* ./
COPY gradle ./gradle
COPY src ./src
RUN gradle --version
RUN gradle clean build -x test --no-daemon

### Stage 2: run the jar on a lightweight JRE
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
