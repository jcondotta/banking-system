# Repository Guidelines

## Project Structure & Module Organization

This is the `ledger` bounded context: a Java 25, Spring Boot 4, single-module Maven service in the root reactor. It uses DDD, CQRS, and package-level architectural boundaries.

- `src/main/java/com/jcondotta/banking/ledger/domain/`: framework-free ledger account aggregate, entries, identities, value objects, enums, exceptions, and repository port.
- `src/main/java/com/jcondotta/banking/ledger/application/`: account provisioning and internal-transfer application commands, handlers, and structured logging definitions.
- `src/main/java/com/jcondotta/banking/ledger/infrastructure/`: Kafka event consumers, PostgreSQL persistence adapters, and runtime configuration.
- `src/main/resources/`: Spring configuration, Liquibase changelogs, SQL migrations, and logging configuration.
- `src/test/java/`: unit tests and architecture tests. Keep tests under packages matching production code and name them `*Test`.
- `docker/`: local PostgreSQL Compose configuration is in the repository-level `docker/docker-compose.yml`.

The service provisions ledger accounts from bank-account activation events and applies internal-transfer events to sender and recipient balances. Keep domain logic framework-free and preserve the existing architecture rules in `LedgerArchitectureTest`.

## Build, Test, and Development Commands

Run Maven commands from the repository root.

- `./mvnw -pl services/ledger -am test`: run ledger unit and architecture tests.
- `./mvnw -pl services/ledger -am clean verify`: build ledger and required modules, run available tests, and generate JaCoCo output.
- `./mvnw -pl services/ledger -am spring-boot:run -Dspring-boot.run.profiles=local`: run the service locally on port `8083`.
- `docker compose -f docker/docker-compose.yml up -d postgres-ledger`: start PostgreSQL `ledger_db` on the port configured in Compose.
- `./mvnw -pl services/ledger -am pitest:mutationCoverage`: run PIT mutation testing.

## Coding Style & Naming Conventions

Use Java conventions and packages under `com.jcondotta.banking.ledger`. Keep dependencies pointing inward: infrastructure may depend on application and domain, application may depend on domain, and domain must not depend on Spring, JPA, Kafka, or infrastructure code.

Use the existing command, handler, repository, adapter, entity, mapper, and structured logging patterns. Keep account and transfer identifiers out of metric tags.

## Testing Guidelines

Use JUnit 5, AssertJ, Mockito, Instancio, ArchUnit, JaCoCo, and PIT as configured. Keep unit tests isolated from Spring and external infrastructure when possible. Use integration coverage when a change requires validating Kafka, PostgreSQL, Liquibase, or application wiring; follow existing module conventions rather than introducing parallel container setup.

## Change Guidance

Changes to event consumption should preserve idempotency and transactional balance updates. When changing ledger entries, balances, or transfer application semantics, inspect the domain aggregate and handler tests, persistence adapters and mappings, Liquibase changelogs, and relevant event contracts before editing. Resolve meaningful behavioral or consistency decisions during Design.

Do not commit secrets. Keep environment-specific configuration in Spring profiles, Compose, or deployment configuration. Include focused test evidence and API, event, persistence, or observability notes in pull requests when relevant.
