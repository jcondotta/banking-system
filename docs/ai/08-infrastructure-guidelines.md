# Infrastructure Guidelines

Use this guide for adapters, transport/storage mapping, and runtime wiring. It complements [Application](07-application-guidelines.md), which owns use-case and port responsibilities. For protocol-specific contracts, read only the affected guide: [REST](09-rest-api-guidelines.md), [gRPC](10-grpc-guidelines.md), or [Events](11-event-contracts-guidelines.md). Use [Design](02-design.md) for unresolved choices and local guidance for service-specific paths and commands.

## Locate The Boundary

Start with the adapter being changed, the port or handler it connects to, its mapper, and the relevant configuration. Expand into other components only when that dependency affects the change.

| Change | Inspect together |
| --- | --- |
| REST or gRPC input | Controller/service, request/response mapping, handler, exception translation, protocol configuration |
| Message consumption | Consumer, envelope/payload types, command mapping, listener configuration |
| Persistence | Port, adapter, entity/query mapping, schema, transaction caller |
| Message publication | Publisher or outbox collector, routing, serializer, sender, worker/store configuration |
| Upstream call | Application port, client adapter, remote DTO, client configuration |

## Input And Output Adapters

Input adapters translate protocol input into application commands or queries and translate the result back to the protocol. Keep parsing and transport validation here; domain validity remains governed by [Domain](06-domain-guidelines.md), and use-case orchestration by Application.

REST controllers, gRPC services, and Kafka consumers are different delivery mechanisms. Inspect their existing error handlers and configuration rather than assuming identical error or retry behavior. Consumers map incoming data to local commands; broker listener details stay at the adapter boundary.

Output adapters implement the relevant port using a database, broker, or remote client. Translate remote DTOs into the port's local representation. For example, transfers' `BankAccountLookupAdapter` implements `BankAccountLookupPort` using an HTTP client; its not-found mapping is specific to that lookup contract.

Map technical failures only when their meaning is established. `RecipientPostgresRepository` recognizes a particular uniqueness constraint and optimistic-lock failure; that does not make every database error a duplicate or every upstream failure a missing resource.

## Mapping And Persistence

Persistence mappers translate stored primitives, IDs, enums, and child records into domain types. Delegate reconstitution to the factories described in [Reconstituting Persisted State](06-domain-guidelines.md#reconstituting-persisted-state). Keep storage annotations and database-specific representations in infrastructure.

Inspect keys, indexes, uniqueness constraints, version fields, query projections, and migrations relevant to a persistence change. Preserve their meaning through both read and write mappings. Follow the context's migration and rollback guidance.

Application defines the intended transaction coordination; inspect the actual adapter and caller to establish which writes are atomic. Existing examples differ:

- Accounts' `BankAccountDynamoDbTransactionalWriter` combines account, outbox, and idempotency appenders in one DynamoDB transaction for its idempotent write path.
- Transfers' `BankTransferPostgresRepository.save` writes the aggregate and collected outbox records within its transactional method.

These are entry points for investigation, not interchangeable persistence templates.

## Messaging And Shared Infrastructure

Consult `services/core/infrastructure-core` for shared protocol helpers, exception handlers, event envelopes, routing, and broker sending before adding an equivalent mechanism.

The optional `services/core/outbox-infrastructure` module supplies collection and worker components. Service-specific stores, mappings, and configuration connect them to persistence. Recipients instead publishes through `RecipientKafkaEventPublisher` and `KafkaBrokerPublisher`, using the shared broker sender directly.

When changing an outbox adapter, inspect collection, storage, claim/publish/completion, and failure handling relevant to the change. When changing direct publication, inspect its call site and propagation of publication failures. Delivery guarantees and compatibility belong in [Event contracts](11-event-contracts-guidelines.md#delivery-and-replay).

## Runtime Wiring

Keep bean composition, client configuration, serialization, and technology properties in infrastructure/bootstrap concerns. Check imports, component scanning, qualifiers, conditional beans, and the affected runtime/test profiles when wiring changes.

For example, accounts' outbox write and worker configurations are separate; worker activation depends on `app.outbox.worker.enabled`. Preserve the intended lifecycle and operational switches when changing configuration. For filters/interceptors, telemetry wiring, and export configuration, consult [Observability](12-observability-guidelines.md).

Use [Testing](04-testing.md) for verification scope and conventions, and the local playbook for fixtures and commands.
