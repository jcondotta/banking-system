# Application Layer Guidelines

Use this guide for application-layer responsibilities and the shared contracts in `services/core/application-core`. It records patterns found across the bounded contexts; use local service guidance where implementation details differ.

Do not introduce new application abstractions or change use-case responsibilities because a pattern appears in another service. If a choice affects behavior, contracts, ownership, consistency, or observability, resolve it during Design.

## Shared Application Contracts

`application-core` currently provides:

- `Command<R>` and `CommandHandler<C>` for commands with no result.
- `CommandHandlerWithResult<C, R>` for commands that return a result.
- `Query<R>` and `QueryHandler<Q, R>` for queries and their result types.
- `TransactionContext` as a marker abstraction for transaction context passed through application-owned ports when a persistence operation needs it.
- `IdempotentCommand<R>`, `IdempotencyKey`, `IdempotencyRecord`, `IdempotentSummary`, and related ports for the idempotency use case.
- `PageRequest` and `PageResult<T>` for shared paging values.

Use these contracts when they match the use case. Do not add framework annotations or infrastructure dependencies to shared application contracts.

## Commands, Queries, And Handlers

Represent a use-case request as a focused command or query, generally an immutable record. Use domain IDs and value objects where the input has domain meaning and that is the established boundary for the service.

Implement one handler per use case. A handler coordinates the application flow: it loads the required state through ports, invokes domain behavior, coordinates persistence or other output ports, and returns the use-case result. Keep business invariants in the domain model and transport concerns in the input adapter.

Use `CommandHandler<C>` when the use case has no result and `CommandHandlerWithResult<C, R>` when it returns a meaningful result, such as an aggregate ID. Use `QueryHandler<Q, R>` for reads. Query handlers may return application read models or paging results; avoid loading and returning aggregates solely to serve a read projection when a query port can provide the required model.

Handlers may depend on domain repository ports and application-owned output ports. Keep dependencies explicit and narrow. Model upstream service access as a port owned by the application boundary; keep HTTP/gRPC clients and serialization in infrastructure.

## Ports, Transactions, And Events

Keep persistence and integration technologies behind ports and adapters. Command-side aggregate repositories commonly live in the domain; read/query ports and projections may live in application. Follow the service's existing split and do not put Spring Data, JPA, DynamoDB, SQL, or transport types in use-case contracts.

Transaction ownership varies by bounded context and operation. Some handlers use a transaction boundary directly; other services coordinate a transactional writer or transaction context through ports, while adapters own persistence transactions. Preserve the established atomicity boundary. Resolve changes to transaction scope, event publication, or consistency during Design.

Event handling also varies: a service may publish pulled domain events through an output port, persist them atomically through an outbox, or process incoming integration events into commands. Keep transport event envelopes and broker details in infrastructure. Do not assume that all services publish domain events the same way.

## Idempotency

Use the shared idempotency contracts only for use cases whose behavior requires replay protection. Commands that participate implement `IdempotentCommand<R>` and expose an idempotency key; application coordination checks whether the request has already succeeded and returns the prior result on a valid replay.

Idempotency is not a default requirement for every command. Key scope, request fingerprinting, replay semantics, persistence atomicity, and conflict behavior are behavior and consistency decisions that must be resolved in Design. Follow the accounts implementation as a service-specific example, not as a universal handler template.

## Logging, Failures, And Observability

Follow the bounded context's established structured logging pattern. Where used, `LogScope` identifies the operation and `LogContext` adds relevant structured fields. Log business outcomes and normalized failure reasons without exposing secrets or sensitive values. Keep identifiers and other high-cardinality values out of metric tags.

Failure classification belongs at the application boundary when the context uses stable operational reasons. Preserve domain exceptions when callers depend on their semantics; map or normalize them according to the local handler and API contract. Do not select domain exception types from HTTP status codes or catch and reclassify every exception automatically.

Use `@Observed` or concurrency controls only where the service has an established requirement and configuration. Keep observation names stable and metric labels low-cardinality. Do not add instrumentation or concurrency limits solely to make handlers look consistent.

## Testing Application Behavior

Test handlers as application units using real commands, queries, and domain objects with mocks or fakes for their external collaborators. Assert the use-case result, domain state or propagated object, and behaviorally important interactions. Include failure and replay cases when they are part of the use case.

Keep tests independent of Spring and infrastructure when a unit test can establish the behavior. Use integration tests for persistence transactions, event delivery, or application wiring that cannot be validated at the unit boundary. Follow `docs/ai/04-testing.md` and the bounded context's local fixtures and focused test commands.
