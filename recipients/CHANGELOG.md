# Changelog

All notable changes to the recipients service are documented in this file.

## 1.0.1 - 2026-05-10

### Added

- Added optional `name` filter to the list recipients API.
- Added partial, case-insensitive recipient name search.
- Added unit coverage for list-recipient filter normalization and query flow.
- Added integration coverage for filtered list results, blank filters, case-insensitive matching, and pagination.

### Changed

- Updated recipients module version from `1.0.0` to `1.0.1`.
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
