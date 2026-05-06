# syntax=docker/dockerfile:1.7

FROM maven:3.9.15-eclipse-temurin-25 AS builder

WORKDIR /workspace

COPY pom.xml .
COPY core core
COPY recipients recipients

RUN --mount=type=cache,target=/root/.m2 \
    mvn -B -N -f pom.xml install -DskipTests

RUN --mount=type=cache,target=/root/.m2 \
    mvn -B -f core/pom.xml clean install -DskipTests

RUN --mount=type=cache,target=/root/.m2 \
    mvn -B -f recipients/pom.xml clean package \
      -pl recipients-bootstrap \
      -am \
      -DskipTests

FROM eclipse-temurin:25-jre

WORKDIR /app

RUN apt-get update \
    && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/* \
    && groupadd --system --gid 10001 appgroup \
    && useradd --system --uid 10001 --gid appgroup appuser

COPY --from=builder --chown=10001:10001 \
  /workspace/recipients/recipients-bootstrap/target/recipients-bootstrap-*.jar \
  /app/app.jar

USER 10001:10001

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=5s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["java","-XX:+UseG1GC","-XX:+ExitOnOutOfMemoryError","-XX:MaxRAMPercentage=75","-jar","/app/app.jar"]
