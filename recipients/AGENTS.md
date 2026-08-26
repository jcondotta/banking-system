# Repository Guidelines

## Project Structure & Module Organization

This is the `recipients` bounded context: a Java 25, Spring Boot 4, single-module Maven service using DDD, CQRS, and hexagonal architecture by package rather than by Maven submodule.

- `src/main/java/com/jcondotta/banking/recipients/`: application entrypoint and all production code.
- `domain/`: framework-free recipient model. It owns the `Recipient` aggregate, `BankAccountId`, `RecipientId`, `RecipientName`, `Iban`, domain exceptions, failure reasons, validation constants, and repository ports.
- `application/`: use-case orchestration. It owns commands, queries, handlers, query models, logging event names/keys, failure normalization, `@Observed` instrumentation, and concurrency limits.
- `infrastructure/`: Spring and adapter code. It owns REST controllers, request/response DTOs, mappers, `ProblemDetail` exception handlers, correlation filtering, JPA entities/repositories, PostgreSQL adapters, persistence mappers, and runtime configuration.
- `src/main/resources/`: Spring `application*.yml`, Liquibase changelogs, SQL migrations, and `logback-spring.xml`.
- `src/test/java/`: unit and integration tests. Integration tests live under `.../integration` and use `@IntegrationTest`.
- `docker/`: local PostgreSQL Compose.
- `k8s/`: application and PostgreSQL manifests.

Keep dependencies pointing inward by package: infrastructure may depend on application and domain, application may depend on domain, and domain must not depend on Spring, JPA, HTTP, logging frameworks, or infrastructure code.

## Development Workflow

Shared development workflow guidance lives under `../docs/ai/`.

Recipients-specific guidance lives under `docs/ai/`.

`docs/ai/change-playbook.md` provides recipients-specific guidance based on the architectural areas affected by a change.

When shared workflow guidance and local recipients guidance both apply, prefer the more specific local recipients guidance for paths, commands, fixtures, contracts, and bounded-context behavior.

## Build, Test, and Development Commands

Run commands from this directory. The Maven wrapper lives one level up.

- `../mvnw clean verify`: build the service, run Surefire/Failsafe, and produce JaCoCo output.
- `../mvnw test`: run unit tests only.
- `../mvnw spring-boot:run -Dspring-boot.run.profiles=local`: start the service locally on port `8081`.
- `docker compose -f docker/docker-compose.yml up -d`: start PostgreSQL `recipients_db` on `127.0.0.1:5432`.
- `../mvnw pitest:mutationCoverage`: run PIT mutation testing for the consolidated service.

## Coding Style & Naming Conventions

Use Java conventions with 4-space indentation and packages under `com.jcondotta.banking.recipients`. Prefer records for immutable commands, queries, IDs, and DTOs.

Follow local naming patterns: `*Command`, `*CommandHandler`, `*Query`, `*QueryHandler`, `*Repository`, `*Mapper`, `*Controller`, `*ControllerImpl`, `*ExceptionHandler`, `*Properties`, `*Test`, and `*IT`.

REST paths come from `app.api.recipients.*`; API versioning uses `X-API-Version`.

Use `LogContext`, `RecipientEventType`, and `RecipientLogKey`; mask IBANs and keep IDs out of metric tags.

Preserve framework-free domain code even though all layers compile in one Maven module.

## Commit & Pull Request Guidelines

Recent commits use imperative, sentence-style messages such as `Add name filtering to list recipients API`.

PRs should include summary, affected areas, test evidence (`../mvnw verify`, targeted tests, or PIT where relevant), linked issues, and API/config notes when endpoints, profiles, Docker, Kubernetes, or observability behavior change.

## Security & Configuration Tips

Do not commit real secrets.

Compose credentials are development-only (`admin`/`password`). Production config comes from environment variables and Kubernetes manifests.

Liquibase changelogs live in `src/main/resources/db/changelog`; include SQL rollbacks.

## Agent-Specific Instructions

When making architectural changes or updating repository guidance, inspect the affected controllers, handlers, repositories, configuration, resources, and tests rather than inferring architecture from package names alone.
