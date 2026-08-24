# Repository Guidelines

## Project Structure & Module Organization

This is a Java 25, Spring Boot 4, Maven multi-module banking system. The root `pom.xml` builds `core`, `accounts`, and `recipients`; `transfers` has its own Maven tree but is not listed in the root modules.

- `core/`: shared `domain-core`, `application-core`, and `infrastructure-core` modules.
- `accounts/`, `recipients/`, `transfers/`: bounded contexts split into `*-domain`, `*-application`, `*-infrastructure`, and `*-bootstrap` modules. `accounts` also includes `accounts-contracts` and `accounts-outbox`.
- `src/main/java` and `src/test/java`: production and test code inside each module.
- `src/main/resources`: Spring configuration, Liquibase changelogs, and logging config.
- `docker/`, `k8s/`, `terraform/`: local infrastructure, Kubernetes manifests, and cloud infrastructure.

## Build, Test, and Development Commands

- `./mvnw clean verify`: build root modules, run unit and integration tests, and generate JaCoCo reports.
- `./mvnw test`: run unit tests through Surefire.
- `./mvnw -pl recipients/recipients-bootstrap -am spring-boot:run -Dspring-boot.run.profiles=local`: run the recipients service locally with required upstream modules.
- `cd recipients && docker compose -f docker/docker-compose.yml up -d`: start recipients local dependencies.
- `./mvnw -pl accounts/accounts-domain pitest:mutationCoverage`: run PIT mutation testing for a focused module.

## Development Workflow

Shared development workflow guidance lives under `docs/ai/`.

The numbered documents represent stages of the development process. They define how to reason about, design, implement, test, and validate changes.

| Stage | Purpose | Guidance |
| --- | --- | --- |
| 1. Requirements | Understand the requested behavior, scope, constraints, and open questions | `docs/ai/01-requirements.md` |
| 2. Design | Resolve behavioral, contract, and architectural decisions before implementation | `docs/ai/02-design.md` |
| 3. Implementation | Implement autonomously after relevant requirements and design decisions are resolved | `docs/ai/03-implementation.md` |
| 4. Testing | Add or update tests according to repository testing conventions | `docs/ai/04-testing.md` |
| 5. Validation | Independently review the completed change before considering it finished | `docs/ai/05-validation.md` |

Bounded contexts may provide local guidance under their own `docs/ai/` directory. When shared workflow guidance and local bounded-context guidance both apply, prefer the more specific local guidance for paths, commands, fixtures, contracts, and bounded-context behavior.

## Coding Style & Naming Conventions

Use Java conventions with 4-space indentation, descriptive class names, and packages under `com.jcondotta.banking`. Preserve the architecture boundary: domain code must stay framework-free; Spring, persistence, messaging, and HTTP adapters belong in infrastructure or bootstrap modules. Follow existing naming patterns such as `*CommandHandler`, `*Repository`, `*Config`, `*Test`, and `*IT`.

## Testing Guidelines

Tests use JUnit 5, Mockito, Rest Assured, ArchUnit, Instancio, Testcontainers, JaCoCo, and PIT. Keep unit tests beside the module they verify under `src/test/java`. Name unit tests `*Test` and integration tests `*IT` so Surefire and Failsafe pick them up correctly. Add focused domain tests for business rules and integration tests for REST, persistence, messaging, and containerized dependencies.

## Commit & Pull Request Guidelines

Recent commits use imperative, sentence-style messages such as `Add name filtering to list recipients API`. Keep commits focused and describe the behavioral change. Pull requests should include a short summary, affected modules, test evidence (`./mvnw clean verify`, targeted module tests, or PIT where relevant), linked issues, and API/configuration notes when endpoints, events, profiles, or infrastructure files change.

## Security & Configuration Tips

Do not commit secrets or local credentials. Keep environment-specific values in Spring profiles, Docker Compose overrides, Kubernetes manifests, or Terraform variables. Prefer low-cardinality metrics labels; put identifiers such as account IDs, recipient IDs, and correlation IDs in logs or traces.
