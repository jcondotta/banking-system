FROM maven:3.9.9-eclipse-temurin-21-alpine AS builder

WORKDIR /app

COPY ../recipients-infrastructure/pom.xml .
COPY recipients-domain/pom.xml recipients-domain/pom.xml
COPY recipients-application/pom.xml recipients-application/pom.xml
COPY recipients-infrastructure/pom.xml recipients-infrastructure/pom.xml

RUN mvn dependency:go-offline -B

COPY recipients-domain recipients-domain
COPY recipients-application recipients-application
COPY recipients-infrastructure recipients-infrastructure

RUN mvn clean package \
    -pl recipients-infrastructure \
    -am \
    -DskipTests

# ---- Runtime Stage ----
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

RUN apk add --no-cache curl
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

COPY --from=builder \
  /app/recipients-infrastructure/target/recipients-infrastructure-*.jar \
  app.jar

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=5s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java","-XX:+UseG1GC","-XX:+ExitOnOutOfMemoryError","-jar","app.jar"]