# Multi-stage Dockerfile for a Spring Boot (Maven, Java 17) application.
# Build with Maven (Java 17), run with a slim JRE image.
#
# Notes:
# - The app binds to the port from $PORT (Render provides this). We pass it to Spring with --server.port.
# - Use env vars for DB/Redis (set them in Render); do not hardcode credentials.
# - The wildcard for the jar avoids depending on the exact snapshot name.

################################
# 1) Build stage
################################
FROM maven:3.9.5-eclipse-temurin-17 AS builder
WORKDIR /workspace

# Copy pom first and download dependencies (cached unless pom changes)
COPY pom.xml .
RUN mvn -B -DskipTests dependency:go-offline

# Copy sources and build the jar
COPY src ./src
RUN mvn -B -DskipTests package

################################
# 2) Runtime stage
################################
FROM eclipse-temurin:17-jre
WORKDIR /app

# Copy the fat/uber jar produced by the build stage
COPY --from=builder /workspace/target/*.jar app.jar

# Optional: allow tuning of JVM options via env var
ENV JAVA_OPTS=""

# Expose the usual Spring Boot port (Render provides $PORT at runtime)
EXPOSE 8080

# Start the application, binding to the port provided by $PORT (fallback 8080)
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar --server.port=${PORT:-8080}"]
