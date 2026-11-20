# Changelog

All notable changes to the recipients service are documented in this file.

## 2.0.0 - 2026-09-03

### Added

- Added GetRecipient use case with query, handler, and REST mapping.
- Added UpdateRecipient use case with command, handler, and REST mapping.

### Changed

- Refactored DomainEvent structure to include metadata and typed event data.
- Standardized exception handling using ApiProblem constants across handlers and controllers.
- Replaced InvalidDomainDataException with DomainValidationException for domain validation failures.
- Updated exception handler HTTP status codes and titles for consistency.
- Normalized recipient name handling and enforced maximum length constraints.
- Updated service version from 1.1.0 to 2.0.0.

### Verified

- `../mvnw test` passes in the consolidated module.
- `../mvnw verify` passes, including integration tests.
- `../mvnw spring-boot:run -Dspring-boot.run.profiles=local` starts successfully and `/actuator/health` returns `UP`.

## 1.1.0 - 2026-08-20

### Changed

- Consolidated the recipients bounded context from four Maven submodules into one standard Maven module.
- Moved production code, tests, resources, Liquibase changelogs, logging config, and Dockerfile under the flat `src` project layout.
- Updated the service version from `1.0.1` to `1.1.0`.

### Verified

- `../mvnw test` passes in the consolidated module.
- `../mvnw verify` passes, including integration tests.
- `../mvnw spring-boot:run -Dspring-boot.run.profiles=local` starts successfully and `/actuator/health` returns `UP`.

## 1.0.1 - 2026-05-10

### Added

- Added optional `name` filter to the list recipients API.
- Added partial, case-insensitive recipient name search.
- Added unit coverage for list-recipient filter normalization and query flow.
- Added integration coverage for filtered list results, blank filters, case-insensitive matching, and pagination.

### Changed

- Updated project/module versions from `1.0.0` to `1.0.1`.
- Updated Docker and Kubernetes documentation examples to use image tag `1.0.1`.

### Verified

- `recipients-domain` unit tests pass with 100% PIT mutation score.
- `recipients-application` unit tests pass with 100% PIT mutation score.
- Infrastructure and bootstrap list-recipient tests pass for the new filter behavior.

## 1.0.0 - 2026-05-06

### Added

- Initial production-oriented recipients service baseline.
- Recipient create, list, and remove use cases.
- PostgreSQL persistence with Liquibase migrations.
- Structured logging, Micrometer metrics, OpenTelemetry support, and Prometheus endpoint.
- Docker image and Kubernetes application manifests.
- Testcontainers-backed integration tests and PIT mutation testing setup.
