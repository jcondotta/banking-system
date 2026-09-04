# Repository Guidelines

## Project Structure & Module Organization

This is the `transfers` bounded context: a Java 25, Spring Boot 4, single-module Maven service using DDD, CQRS, and hexagonal architecture by package rather than by Maven submodule. It has its own Maven tree outside the root reactor.

- `src/main/java/com/jcondotta/banking/transfers/`: application entrypoint and all production code.
- `domain/`: framework-free transfer model, including bank accounts, transfer aggregates, parties, monetary movements, domain events, exceptions, identities, value objects, and repository ports.
- `application/`: use-case orchestration, commands, handlers, output ports, observability, and structured logging definitions.
- `infrastructure/`: Spring and adapter code for REST delivery, accounts-service lookup, PostgreSQL persistence, and runtime configuration.
- `src/main/resources/`: Spring `application*.yml`, Liquibase changelogs, SQL migrations, and `logback-spring.xml`.
- `src/test/java/`: unit and integration tests organized under packages matching production code, plus shared test support.
- `docker/`: local PostgreSQL Compose configuration.

Keep dependencies pointing inward by package: infrastructure may depend on application and domain, application may depend on domain, and domain must not depend on Spring, JPA, HTTP, logging frameworks, or infrastructure code.

## Development Workflow

Shared development workflow guidance lives under `../docs/ai/`. Use it for requirements, design, implementation, testing, and validation; when referenced from this bounded context, keep paths relative to this directory.

## Build, Test, and Development Commands

Run commands from this directory. The Maven wrapper lives one level up.

- `../mvnw clean verify`: build the service, run Surefire/Failsafe, and generate JaCoCo reports.
- `../mvnw test`: run unit tests.
- `../mvnw spring-boot:run -Dspring-boot.run.profiles=local`: run the service locally on port `8082`.
- `docker compose -f docker/docker-compose.yml up -d`: start PostgreSQL `transfers_db` on `127.0.0.1:5433`.
- `../mvnw pitest:mutationCoverage`: run PIT mutation testing.

## Coding Style & Naming Conventions

Use Java conventions with 4-space indentation and packages under `com.jcondotta.banking.transfers`. Prefer records for immutable commands, IDs, event data, and DTOs.

Follow local naming patterns such as `*Command`, `*CommandHandler`, `*Repository`, `*Adapter`, `*Client`, `*Controller`, `*Request`, `*Response`, `*Entity`, `*Mapper`, `*Event`, `*Test`, and `*IT`.

Keep REST paths under `app.api.transfers.*`, API versioning on `X-API-Version`, use `BankTransferEventType` and `BankTransferLogKey` for structured logs, and keep identifiers out of metric tags. Preserve framework-free domain code even though all layers compile in one Maven module.

## Testing Guidelines

Tests use JUnit 5, AssertJ, Mockito, Rest Assured, Spring Boot Test, JaCoCo, and PIT. Keep tests under matching packages in `src/test/java`; name unit tests `*Test` and integration tests `*IT` so Surefire and Failsafe select them correctly. Add focused domain tests for business rules and integration tests for REST, persistence, upstream HTTP clients, and containerized dependencies.

## Commit & Pull Request Guidelines

Use imperative, sentence-style commit messages such as `Add internal transfer request endpoint`. Keep commits focused and describe the behavioral change.

Pull requests should include a short summary, affected areas, test evidence (`../mvnw clean verify`, targeted tests, or PIT where relevant), linked issues, and API/configuration notes when endpoints, events, profiles, Docker, or observability behavior change.

## Security & Configuration Tips

Do not commit secrets or local credentials. Compose credentials are development-only. Keep environment-specific values in Spring profiles, Docker Compose overrides, or deployment configuration. Liquibase changelogs live under `src/main/resources/db/changelog`; include SQL rollbacks. Prefer low-cardinality metrics labels; place account IDs, transfer IDs, and correlation IDs in logs or traces.

## Agent-Specific Instructions

When answering questions about existing behavior or updating architectural guidance, verify the relevant production code, tests, configuration, and resources rather than inferring behavior from package names alone.
