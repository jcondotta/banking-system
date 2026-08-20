# Recipients Change Playbook

Use this playbook for non-trivial changes in the `recipients` bounded context. Keep changes aligned with the existing package architecture and prefer the closest local pattern before adding new abstractions.

For test style, scope, and integration-test conventions, use `docs/ai/testing.md`.

## Domain Changes

Read:
- `src/main/java/com/jcondotta/banking/recipients/domain/recipient/aggregate`
- related value objects under `domain/recipient/value_objects`
- related identities under `domain/recipient/identity`
- related domain exceptions and failure reasons
- matching tests under `src/test/java/com/jcondotta/banking/recipients/domain`

Rules:
- keep domain code framework-free
- preserve recipient ownership checks
- enforce invariants in value objects or aggregate methods when possible
- keep repository interfaces as ports, not persistence implementations
- update exception messages and failure reasons deliberately

Run:
- `../mvnw test -Dtest=*Recipient*Test`
- `../mvnw test -Dtest=*Iban*Test,*RecipientName*Test,*BankAccountId*Test,*RecipientId*Test` when value objects or identities change

## Application Changes

Read:
- command/query records under `src/main/java/com/jcondotta/banking/recipients/application/recipient`
- matching command or query handler
- repository ports used by the handler
- `RecipientEventType` and `RecipientLogKey` when logging changes
- matching handler tests under `src/test/java/com/jcondotta/banking/recipients/application`

Rules:
- keep orchestration in application and business invariants in domain
- normalize failures consistently
- preserve structured logging event names and keys unless the behavior intentionally changes
- keep identifiers out of metric tags
- do not expose infrastructure exceptions through application APIs

Run:
- `../mvnw test -Dtest=*CommandHandlerTest,*QueryHandlerTest`
- `../mvnw test -Dtest=*CreateRecipient*Test,*RemoveRecipient*Test,*ListRecipients*Test` for use-case changes

## REST/API Changes

Read:
- controller interface and implementation under `infrastructure/adapters/input/rest`
- request and response DTOs
- REST mapper for the endpoint
- related `ProblemDetail` exception handlers
- `RecipientsURIProperties`
- matching controller, mapper, DTO, and integration tests

Rules:
- keep paths sourced from `app.api.recipients.*`
- preserve `X-API-Version` behavior unless the API contract intentionally changes
- map validation, domain, conflict, not-found, database, and rate-limit failures through the existing exception handler style
- do not leak raw IBAN values in responses, logs, or error details
- update integration coverage when endpoint behavior, status codes, headers, or error payloads change

Run:
- `../mvnw test -Dtest=*ControllerImplTest,*RestMapperTest,*RequestTest,*ResponseTest`
- `../mvnw verify -Dit.test=*RecipientIT` for API flow changes

## Persistence Changes

Read:
- JPA entity under `infrastructure/adapters/output/persistence/entity`
- Spring Data repository under `infrastructure/adapters/output/persistence/repository`
- PostgreSQL adapter
- persistence mapper
- Liquibase changelogs under `src/main/resources/db/changelog`
- repository, mapper, and integration tests

Rules:
- keep persistence details in infrastructure
- preserve optimistic-lock and duplicate-IBAN behavior
- include SQL rollbacks for Liquibase changes
- keep query projections aligned with `RecipientSummary`
- mask IBANs before returning read-model data

Run:
- `../mvnw test -Dtest=*PostgresRepositoryTest,*EntityMapperTest,*SummaryMapperTest`
- `../mvnw verify -Dit.test=*RecipientIT` for schema or repository behavior changes

## Configuration, Docker, And Kubernetes Changes

Read:
- `src/main/resources/application*.yml`
- `src/main/resources/logback-spring.xml`
- `docker/docker-compose.yml`
- manifests under `k8s`
- integration test configuration under `src/test/resources`

Rules:
- do not commit real secrets
- keep local credentials development-only
- preserve local service port `8081` unless intentionally changing it
- keep environment-specific values in profiles, Docker Compose, Kubernetes, or Terraform
- update tests or docs when profiles, ports, database names, or observability behavior change

Run:
- `../mvnw test`
- `../mvnw verify` when configuration affects integration tests or application startup

## Before Finishing

Check:
- dependency direction still points inward by package
- domain remains free of Spring, JPA, HTTP, and logging framework dependencies
- tests match the behavior changed
- unrelated user changes are preserved
- API, configuration, observability, or schema changes are mentioned in the final summary
