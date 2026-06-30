# Build stage
FROM maven:3.9.6-eclipse-temurin-17-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Run stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# SECURITY: Create a non-root group and user to run the application
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

COPY --from=build /app/target/Project-1.0-SNAPSHOT.jar app.jar
RUN mkdir -p uploads db && chown -R appuser:appgroup /app

# SECURITY: Switch to the non-root user
USER appuser

EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
