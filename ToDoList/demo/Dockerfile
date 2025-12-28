# syntax=docker/dockerfile:1

FROM eclipse-temurin:17-jdk AS build
WORKDIR /app

COPY gradlew build.gradle settings.gradle gradle.properties micronaut-cli.yml /app/
COPY gradle /app/gradle
COPY src /app/src

# Build
RUN sed -i 's/\r$//' /app/gradlew \
    && chmod +x /app/gradlew \
    && /app/gradlew clean build -x test

FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=build /app/build/libs/*-all-optimized.jar /app/app.jar

EXPOSE 8080
ENV MICRONAUT_ENVIRONMENTS=docker

ENTRYPOINT ["java", "-jar", "/app/app.jar"]