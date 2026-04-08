# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Test Commands

```bash
# Build all modules
mvn clean install

# Run unit tests only (all modules)
mvn test

# Run unit + integration tests (all modules)
mvn verify

# Run tests for a specific module
mvn test -pl accounts/accounts-domain
mvn verify -pl accounts/accounts-infrastructure

# Run a single test class
mvn test -pl accounts/accounts-domain -Dtest=AccountHolderNameTest

# Run a single test method
mvn test -pl accounts/accounts-domain -Dtest=AccountHolderNameTest#shouldFailValidation_whenFirstNameIsBlank

# Mutation testing (80% threshold enforced)
mvn pitest:mutationCoverage -pl accounts/accounts-domain
```

Integration tests are named `*IT.java` and require Docker (TestContainers spins up LocalStack + Kafka automatically).

## Local Development Infrastructure

```bash
# Start DynamoDB (LocalStack) + Kafka + Kafka UI
docker compose -f docker/docker-compose.yml up -d
```

- LocalStack (DynamoDB): `http://127.0.0.1:4566`
- Kafka: `localhost:9092`
- Kafka UI: `http://localhost:8085`

Run the application with `-Dspring.profiles.active=local` to use `application-local.yml` config.

## Module Structure

```
banking-core/
  domain-core/          # AggregateRoot, DomainEvent base types
  application-core/     # Command, CommandHandler, CommandHandlerWithResult,
                        # CorrelationIdProvider, EventSourceProvider,
                        # IntegrationEvent, IntegrationEventMetadata,
                        # AbstractDomainEventMapper, DomainEventMapperRegistry
  infrastructure-core/  # Shared infrastructure utilities

bank-accounts-contracts/ # Integration event DTOs shared between services

accounts/
  accounts-domain/        # BankAccount aggregate, value objects, domain events
  accounts-application/   # Command handlers (open, activate, block, unblock, close, add joint holder)
  accounts-infrastructure/ # REST controllers, DynamoDB repo, Kafka outbox poller, mappers
  accounts-test-support/  # Shared test fixtures

recipients/
  recipients-domain/        # Recipient aggregate
  recipients-application/   # Command handlers
  recipients-infrastructure/ # REST controllers, DynamoDB repo, Kafka consumer
  recipients-test-support/  # Shared test fixtures
```

## Architecture

### Hexagonal / DDD per bounded context

Each bounded context (`accounts`, `recipients`) follows strict layering:
- **domain**: Aggregates, value objects, domain events, repository interfaces — no framework dependencies
- **application**: Command handlers implementing `CommandHandler<C>` or `CommandHandlerWithResult<C, R>` — depends only on domain
- **infrastructure**: Spring Boot wiring, DynamoDB repositories, REST controllers, Kafka producers/consumers

### Outbox Pattern (event publishing reliability)

Domain events are published reliably via a transactional outbox:

1. `BankAccount` raises domain events via `AggregateRoot.registerEvent()`
2. `BankAccountDynamoDbRepository.save()` writes both the aggregate entity and an `OutboxEntity` (status=PENDING) in a single DynamoDB transactional write
3. `OutboxEventCollector` pulls domain events, resolves their `DomainEventMapperRegistry` entry, and produces `IntegrationEvent` objects
4. `OutboxPollerService` (scheduled) queries the `gsi-outbox-status` GSI for PENDING events, publishes to Kafka, then marks them PROCESSED

### Event Mapper Registry

`DomainEventMapperRegistry` maps a domain event class → `AbstractDomainEventMapper<D, I>` (where D=domain event, I=integration event). Mappers are registered in `BankAccountEventMapperConfig`. Adding a new domain event requires:
1. A new mapper class extending `AbstractDomainEventMapper`
2. Registering it in the config bean

### Correlation ID propagation

`CorrelationFilter` extracts (or generates) a UUID from the `X-Correlation-ID` HTTP header, stores it in `ThreadLocalCorrelationIdProvider`, and it flows through to `IntegrationEventMetadata` on every outbox event.

### DynamoDB data model

Single-table design in `bank-accounts` table:
- Partition key: `BANK_ACCOUNT#<uuid>`
- Sort key varies by entity type (account entity vs. account holder entities)
- GSI `gsi-outbox-status` used by the outbox poller to query by status

### Recipients ↔ Accounts integration

The recipients service consumes `BankAccountActivatedIntegrationEvent` from the `bank-account-activated` Kafka topic via `BankAccountActivatedConsumer`, which dispatches a `RegisterBankAccountCommand`.

## Key Technologies

- **Java 21**, **Spring Boot 3.4.3**
- **AWS DynamoDB** (Enhanced Client) via Spring Cloud AWS 3.4.0
- **Apache Kafka** via Spring Kafka
- **MapStruct** for DTO↔domain mapping (annotation processor — run with Lombok)
- **Lombok** (`@Slf4j`, `@RequiredArgsConstructor`, etc.)
- **TestContainers** (LocalStack + Kafka) for integration tests — `@ContextConfiguration(initializers = { LocalStackTestContainer.class, KafkaTestContainer.class })`
- **Rest-Assured** for HTTP integration tests
- **Instancio** for test data generation
- **Micrometer / Actuator** for metrics (`/actuator/health`, `/actuator/prometheus`)
- **SpringDoc OpenAPI** for Swagger docs

## Test Conventions

- Unit tests: `*Test.java` — pure Java, no Spring context, use fixtures from `*-test-support` modules
- Integration tests: `*IT.java` — full Spring context with `@ActiveProfiles("test")`, TestContainers auto-started
- Domain test fixtures live in `*fixtures/` packages (e.g., `BankAccountTestFixture`, `AccountHolderFixtures`)
- Parameterized tests use `ArgumentProvider` classes (e.g., `BlankValuesArgumentProvider`)

## Domain-Driven Design Guidelines

- Aggregates must enforce invariants and encapsulate behavior
- Avoid anemic domain models (no getters/setters only)
- Value Objects must be immutable and self-validating
- Use ubiquitous language consistently across modules
- Domain layer must not depend on infrastructure or frameworks
- Application layer orchestrates use cases, not business rules
- Prefer explicit domain methods over generic setters

## banking-core Rules

- banking-core acts as a shared domain kernel
- It contains only generic abstractions (AggregateRoot, DomainEvent, base classes)
- It must NOT contain business logic specific to accounts or recipients
- It must remain reusable across bounded contexts
- Avoid leaking domain-specific concepts into banking-core

## Event Mapping & Generics

- Each DomainEvent must have a strongly typed mapper
- Avoid raw types and wildcards (<?>)
- Prefer type-safe generics between DomainEvent and IntegrationEvent
- The mapping should guarantee compile-time safety
- DomainEventMapperRegistry should resolve mappers by event type
- Design must remain extensible when adding new events
- Avoid casting whenever possible

## Testing Philosophy

- Follow naming convention: should..._when...
- Cover edge cases and domain invariants
- Prefer explicit test setup over magic
- Use fixtures to simplify test creation
- Mutation testing threshold must be respected