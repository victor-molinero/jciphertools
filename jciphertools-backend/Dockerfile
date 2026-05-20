# Build stage
FROM maven:3.9.9-eclipse-temurin-21 AS builder
WORKDIR /app

COPY pom.xml .
COPY src ./src
# Include keys if your app expects local key files
COPY keys ./keys

RUN mvn -q -DskipTests package

# Runtime stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

COPY --from=builder /app/target/*SNAPSHOT.jar app.jar
COPY --from=builder /app/keys ./keys

EXPOSE 8081 8082
ENTRYPOINT ["java","-jar","/app/app.jar"]