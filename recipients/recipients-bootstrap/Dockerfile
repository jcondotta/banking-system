# syntax=docker/dockerfile:1.7

FROM maven:3.9.9-eclipse-temurin-25 AS builder

WORKDIR /workspace

# This Dockerfile expects the repository root as the build context.
# Example:
# docker build -f recipients/recipients-bootstrap/Dockerfile .

COPY pom.xml .
COPY core core
COPY recipients recipients

RUN --mount=type=cache,target=/root/.m2 \
    mvn -B clean package \
      -pl recipients/recipients-bootstrap \
      -am \
      -DskipTests

FROM eclipse-temurin:25-jre

WORKDIR /app

RUN apt-get update \
    && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/* \
    && groupadd --system appgroup \
    && useradd --system --gid appgroup appuser

COPY --from=builder --chown=appuser:appgroup \
  /workspace/recipients/recipients-bootstrap/target/recipients-bootstrap-*.jar \
  /app/app.jar

USER appuser

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=5s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java","-XX:+UseG1GC","-XX:+ExitOnOutOfMemoryError","-jar","/app/app.jar"]
