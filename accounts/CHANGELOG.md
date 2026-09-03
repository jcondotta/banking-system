# Changelog

All notable changes to the accounts service are documented in this file.

## 2.0.0 - 2026-09-03

### Added

- Initial production-oriented accounts service baseline.
- Bank account lifecycle operations: Open, Activate, Block, Unblock, and Close.
- Joint holder management: AddJointHolder command with primary and joint holder distinction and a configurable maximum holder limit.
- Bank account lookup: GetBankAccountById and GetBankAccountByIban queries.
- DynamoDB persistence with entity and outbox tables, transactional writes via DynamoDB Transactions.
- Transactional outbox pattern for reliable event publishing: OutboxTransactionalAppender, shard-based storage, and virtual-thread outbox worker with a configurable kill switch (`app.outbox.worker.enabled`).
- Kafka event publishing for BankAccountOpened, BankAccountStatusChanged, BankAccountJointHolderAdded, and BankAccountJointHolderDeactivated events.
- Structured logging, Micrometer metrics, OpenTelemetry support, and Prometheus endpoint.
- Docker image and Kubernetes application manifests.
- Testcontainers-backed integration tests using LocalStack for DynamoDB and embedded Kafka.
- ArchUnit architecture enforcement covering domain isolation, application-to-infrastructure boundary, adapter isolation, DynamoDB and REST type confinement, and cycle detection.

### Changed

- Consolidated the accounts bounded context from multiple Maven submodules into one standard Maven module.
- Moved production code, tests, resources, logging config, and Dockerfile under the flat `src` project layout.
- Refactored DomainEvent structure to include metadata and typed event data.
- Standardized exception handling using ApiProblem constants across handlers and controllers.
- Replaced InvalidDomainDataException with DomainValidationException for domain validation failures.
- Updated exception handler HTTP status codes and titles for consistency.
- Updated service version from 1.0.1 to 2.0.0.

### Verified

- `../mvnw test` passes in the consolidated module.
- `../mvnw verify` passes, including integration tests.
- `../mvnw spring-boot:run -Dspring-boot.run.profiles=local` starts successfully and `/actuator/health` returns `UP`.
