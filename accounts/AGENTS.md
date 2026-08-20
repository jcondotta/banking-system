# Repository Guidelines

## Project Structure & Module Organization

This is a Java 25, Spring Boot 4, Maven multi-module banking system. The root `pom.xml` builds `core`, `accounts`, and `recipients`; `transfers` has a separate Maven tree and is not included in the root modules.

- `core/`: shared `domain-core`, `application-core`, and `infrastructure-core` modules.
- `accounts/`, `recipients/`, `transfers/`: bounded contexts split into `*-domain`, `*-application`, `*-infrastructure`, and `*-bootstrap` modules.
- `accounts/accounts-contracts` and `accounts/accounts-outbox`: accounts-specific contracts and outbox support.
- `src/main/java` and `src/test/java`: production and test code within each module.
- `src/main/resources`: Spring config, Liquibase changelogs, and logging configuration.
- `docker/`, `k8s/`, `terraform/`: local dependencies, Kubernetes manifests, and cloud infrastructure.

## Build, Test, and Development Commands

- `./mvnw clean verify`: builds root modules, runs unit and integration tests, and generates JaCoCo reports.
- `./mvnw test`: runs unit tests through Surefire.
- `./mvnw -pl accounts/accounts-bootstrap -am spring-boot:run -Dspring-boot.run.profiles=local`: runs the accounts service locally with required upstream modules.
- `cd accounts && docker compose -f docker/docker-compose.yml up -d`: starts local accounts dependencies.
- `./mvnw -pl accounts/accounts-domain pitest:mutationCoverage`: runs PIT mutation testing for the accounts domain module.

## Coding Style & Naming Conventions

Use standard Java conventions with 4-space indentation, descriptive class names, and packages under `com.jcondotta.banking`. Preserve architectural boundaries: domain code must remain framework-free, while Spring, HTTP, messaging, and persistence adapters belong in infrastructure or bootstrap modules. Follow existing naming patterns such as `*CommandHandler`, `*Repository`, `*Config`, `*Test`, and `*IT`.

## Testing Guidelines

Tests use JUnit 5, Mockito, Rest Assured, ArchUnit, Instancio, Testcontainers, JaCoCo, and PIT. Keep tests beside the module they verify under `src/test/java`. Name unit tests `*Test` and integration tests `*IT` so Surefire and Failsafe pick them up correctly. Add focused domain tests for business rules and integration tests for REST, persistence, messaging, and containerized dependencies.

## Commit & Pull Request Guidelines

Recent commits use imperative, sentence-style messages, for example `Add name filtering to list recipients API`. Keep commits focused and describe the behavioral change. Pull requests should include a short summary, affected modules, test evidence such as `./mvnw clean verify` or targeted module tests, linked issues, and API/configuration notes when endpoints, events, profiles, or infrastructure files change.

## Security & Configuration Tips

Do not commit secrets or local credentials. Keep environment-specific values in Spring profiles, Docker Compose overrides, Kubernetes manifests, or Terraform variables. Prefer low-cardinality metrics labels; place identifiers such as account IDs, recipient IDs, and correlation IDs in logs or traces.
