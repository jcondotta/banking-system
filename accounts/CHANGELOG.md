# Changelog

All notable changes to the accounts service are documented in this file.

## Future Improvements

- Add end-to-end gRPC integration tests covering the running server, protobuf serialization, and gRPC status mapping.
- Add an end-to-end transactional-outbox integration test covering event persistence, worker processing, Kafka publication, and outbox completion.
- Add an integration test exercising optimistic locking through concurrent updates against DynamoDB.
- Guarantee IBAN uniqueness during account activation and handle generated IBAN collisions.
- Align the configurable IBAN GSI name with the fixed `gsi-iban` DynamoDB schema annotation.
- Clean up the protobuf Maven configuration to remove the unsupported-parameter warning and duplicate `generate` execution.

## 2.1.0 - 2026-10-02

### Added

- gRPC `BankAccountLookupService` v1 with bank account lookup by ID and IBAN, reusing the existing query handlers and mapping failures to `INVALID_ARGUMENT`, `NOT_FOUND`, and `INTERNAL` statuses.
- `BankAccountActivated` integration event with dedicated Kafka routing, carrying the assigned IBAN and account currency.
- Optimistic concurrency control for DynamoDB aggregate writes using persisted versions, conditional transactional writes, and conflict reporting through `BankAccountOptimisticLockException`.
- ISO 3166-1 alpha-2 country support for account-holder addresses across domain, REST, gRPC, query models, and DynamoDB persistence.

### Changed

- Bank accounts are now opened in `PENDING` status without an IBAN; the IBAN is generated and assigned during activation.
- Enforced consistency between account status and IBAN: pending accounts must not have an IBAN, while activated and subsequent states must have one.
- Activation now emits `BankAccountActivated` instead of the generic `BankAccountStatusChanged` event.
- Bank account lookup by IBAN now uses the DynamoDB `gsi-iban` global secondary index.
- Refactored the accounts outbox implementation to use the shared `outbox-infrastructure` module while retaining accounts-specific DynamoDB storage, event routing, worker wiring, and the operational kill switch.
- Address country is now mandatory in account-opening and joint-holder REST requests and is returned by REST and gRPC lookup responses.

### Breaking Changes

- Existing REST clients must provide `address.country` as a two-letter uppercase country code.
- Consumers that previously interpreted activation through `BankAccountStatusChanged` must consume the new `BankAccountActivated` event.
- Newly opened pending accounts no longer expose an IBAN until activation.

### Verified

- `../mvnw test` passes with 635 unit tests.
- `../mvnw verify` passes, including 62 integration tests.

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
