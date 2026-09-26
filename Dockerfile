# ==========================================
# Stage 1: Build the React Frontend
# ==========================================
FROM node:20-alpine AS frontend-builder
WORKDIR /app/frontend

COPY frontend/package*.json ./
RUN npm ci

COPY frontend/ ./
RUN npm run build:spring

# ==========================================
# Stage 2: Build the Spring Boot Backend
# ==========================================
FROM maven:3.9.6-eclipse-temurin-21-alpine AS backend-builder
WORKDIR /app

COPY pom.xml ./
# Cache maven dependencies
RUN mvn dependency:go-offline -B || true

COPY src ./src
# Copy the built React static assets from Stage 1 directly into Spring Boot static resources
COPY --from=frontend-builder /app/src/main/resources/static ./src/main/resources/static

RUN mvn clean package -DskipTests

# ==========================================
# Stage 3: Minimal Production Runtime
# ==========================================
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Non-root user for security
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

COPY --from=backend-builder /app/target/momentum-0.0.1-SNAPSHOT.jar app.jar

ENV PORT=8080
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
