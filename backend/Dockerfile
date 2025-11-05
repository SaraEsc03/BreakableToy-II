### Multi-stage Dockerfile for Gradle builds
### Stage 1: build with Gradle
FROM gradle:8.6-jdk21 AS build

WORKDIR /app

# Copy Gradle build files first to leverage layer caching
COPY build.gradle.kts settings.gradle.kts gradle.properties* ./
COPY gradle ./gradle

# Copy source
COPY src ./src

# If a Gradle wrapper exists, use it; otherwise use the image's gradle binary
RUN gradle --version

# Build the project (skip tests for faster image builds; remove -x test to run tests)
RUN gradle clean build -x test --no-daemon

### Stage 2: run the jar on a lightweight JRE
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copy the application jar produced by Gradle
COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
