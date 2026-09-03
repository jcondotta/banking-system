# Repository Guidelines

## Project Structure & Module Organization

This is the `accounts` bounded context: a Java 25, Spring Boot 4, single-module Maven service using DDD, CQRS, hexagonal architecture, DynamoDB, Kafka, and a transactional outbox.

- `src/main/java/com/jcondotta/banking/accounts/domain/`: framework-free bank-account model, invariants, identities, value objects, events, exceptions, and repository ports.
- `src/main/java/com/jcondotta/banking/accounts/application/`: commands, queries, handlers, query models, logging definitions, and output ports.
- `src/main/java/com/jcondotta/banking/accounts/contracts/`: integration-event contracts published by accounts.
- `src/main/java/com/jcondotta/banking/accounts/infrastructure/adapters/input/`: REST endpoints, correlation filtering, and the outbox worker entrypoint.
- `src/main/java/com/jcondotta/banking/accounts/infrastructure/adapters/output/`: DynamoDB persistence/outbox adapters and Kafka publishers.
- `src/main/java/com/jcondotta/banking/accounts/infrastructure/outbox/`: outbox dispatch, processing, concurrency, logging, properties, and internal configuration.
- `src/main/resources/`: the shared Spring and logging configuration for the API and outbox worker.
- `src/test/java/`: unit and integration tests; integration tests live under `.../integration` and use `@IntegrationTest`.

The API and outbox processor share one `AccountsApplication`, Boot JAR, lifecycle, health endpoint, and metrics endpoint. Keep `app.outbox.worker.enabled` as the operational kill switch.

## Build, Test, and Development Commands

Run commands from this directory. The Maven wrapper lives one level up.

- `../mvnw clean verify`: build the service, run Surefire/Failsafe, and generate JaCoCo reports.
- `../mvnw test`: run unit tests.
- `../mvnw spring-boot:run -Dspring-boot.run.profiles=local`: run the API and outbox worker locally on port `8080`.
- `docker compose -f docker/docker-compose.yml up -d`: start the local DynamoDB dependency.
- `../mvnw pitest:mutationCoverage`: run mutation testing for the consolidated service.

## Coding Style & Naming Conventions

Use standard Java conventions with 4-space indentation and packages under `com.jcondotta.banking.accounts`. Preserve inward dependencies: infrastructure may depend on application/domain/contracts, application may depend on domain, and domain must remain framework-free. Keep DynamoDB code under `infrastructure.adapters.output.persistence.dynamodb`, Kafka under `infrastructure.adapters.output.messaging`, and delivery mechanisms under `infrastructure.adapters.input`.

## Testing Guidelines

Tests use JUnit 5, Mockito, Rest Assured, ArchUnit, Testcontainers, JaCoCo, and PIT. Keep unit tests under matching packages in `src/test/java`; name unit tests `*Test` and integration tests `*IT`. The `test` profile disables the outbox worker by default so HTTP tests do not poll concurrently; test worker behavior explicitly when needed.

## Commit & Pull Request Guidelines

Recent commits use imperative, sentence-style messages, for example `Add name filtering to list recipients API`. Keep commits focused and describe the behavioral change. Pull requests should include a short summary, affected modules, test evidence such as `./mvnw clean verify` or targeted module tests, linked issues, and API/configuration notes when endpoints, events, profiles, or infrastructure files change.

## Security & Configuration Tips

Do not commit secrets or local credentials. Keep environment-specific values in Spring profiles, Docker Compose overrides, Kubernetes manifests, or Terraform variables. Prefer low-cardinality metrics labels; place identifiers such as account IDs, recipient IDs, and correlation IDs in logs or traces.
