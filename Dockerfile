# ===============================
# 1️⃣ BUILD STAGE
# ===============================
FROM maven:3.9.9-eclipse-temurin-17 AS build

WORKDIR /app

# Copy pom.xml first (for dependency caching)
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy source code
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests


# ===============================
# 2️⃣ RUNTIME STAGE
# ===============================
FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# Copy jar from build stage
COPY --from=build /app/target/*jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar"]