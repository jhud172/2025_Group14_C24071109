# ---- Build stage ----
FROM gradle:8.7-jdk21 AS build
WORKDIR /app
COPY . .
RUN ./gradlew clean build -x test

# ---- Run stage ----
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar

# Render deployments should use production profile by default.
ENV SPRING_PROFILES_ACTIVE=render

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
