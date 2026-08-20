# Repository Guidelines

## Project Structure & Module Organization

This is the `transfers` bounded context: a Java 25, Spring Boot 4, single-module Maven service using DDD, CQRS, and hexagonal architecture by package rather than by Maven submodule.

- `src/main/java/...`: application entrypoint and all production code.
- `domain/`: framework-free model.
- `application/`: use-case orchestration.
- `infrastructure/`: Spring and adapter code.
- `src/main/resources/`: Spring config, Liquibase, SQL, logging.
- `src/test/java/`: unit and integration tests.
- `docker/`: local runtime dependencies.
- `k8s/`: Kubernetes manifests, when present.

## Build, Test, and Development Commands

- `../mvnw clean verify`: build the transfers service, run tests, and generate reports from this directory.
- `../mvnw test`: run unit tests through Surefire from this directory.
- `../mvnw spring-boot:run -Dspring-boot.run.profiles=local`: run the transfers service locally from this directory.
- `docker compose -f docker/docker-compose.yml up -d`: start transfers local dependencies.
- `../mvnw pitest:mutationCoverage`: run PIT mutation testing for this service.

## Coding Style & Naming Conventions

Use Java conventions with 4-space indentation, descriptive class names, and packages under `com.jcondotta.banking.transfers`. Preserve the architecture boundary: domain code must stay framework-free; Spring, persistence, messaging, and HTTP adapters belong in infrastructure or bootstrap entrypoint code. Follow existing naming patterns such as `*CommandHandler`, `*Repository`, `*Config`, `*Test`, and `*IT`.

## Testing Guidelines

Tests use JUnit 5, Mockito, Rest Assured, Spring Boot Test, JaCoCo, and PIT. Keep unit tests under `src/test/java` beside the package they verify. Name unit tests `*Test` and integration tests `*IT` so Surefire and Failsafe pick them up correctly. Add focused domain tests for business rules and integration tests for REST, persistence, messaging, and containerized dependencies.

## Commit & Pull Request Guidelines

Use imperative, sentence-style commit messages such as `Consolidate transfers Maven module`. Keep commits focused and describe the behavioral change. Pull requests should include a short summary, affected packages, test evidence (`../mvnw clean verify`, targeted tests, or PIT where relevant), linked issues, and API/configuration notes when endpoints, events, profiles, or infrastructure files change.

## Security & Configuration Tips

Do not commit secrets or local credentials. Keep environment-specific values in Spring profiles, Docker Compose overrides, Kubernetes manifests, or Terraform variables. Prefer low-cardinality metrics labels; put identifiers such as account IDs, transfer IDs, and correlation IDs in logs or traces.
