# ── Stage 1: Build the JAR ──────────────────────────────────────────
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app

# Copy pom first so dependency layer is cached
COPY pom.xml .
RUN mvn dependency:go-offline -q

# Copy source and build (skip tests for speed)
COPY src ./src
RUN mvn clean package -DskipTests -q

# ── Stage 2: Run the JAR ────────────────────────────────────────────
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
