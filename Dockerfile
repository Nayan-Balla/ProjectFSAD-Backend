# Stage 1: Build
FROM maven:3.9.11-eclipse-temurin-21 AS build
WORKDIR /app

# Copy pom and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build JAR
RUN mvn clean package -DskipTests


# Stage 2: Run
FROM eclipse-temurin:21-jdk-jammy
WORKDIR /app

# Copy JAR from build stage
COPY --from=build /app/target/*.jar app.jar

# Render requires 8080 exposure (important)
EXPOSE 8080

# Run app
ENTRYPOINT ["java", "-jar", "app.jar"]