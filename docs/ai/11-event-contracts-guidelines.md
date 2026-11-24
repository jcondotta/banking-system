# Event Contract Guidelines

Use this guide for published messages and their consumers. Domain event creation belongs in [Domain](06-domain-guidelines.md); broker/outbox mechanics belong in [Infrastructure](08-infrastructure-guidelines.md#messaging-and-shared-infrastructure). Use [Design](02-design.md) to resolve compatibility and delivery changes.

## Identify Publisher And Consumers

Locate the publisher, routing/configuration, serialized representation, known consumers, payload types, and handlers. Include external consumers and report any that cannot be inspected.

The shared [EventEnvelope](../../services/core/infrastructure-core/src/main/java/com/jcondotta/banking/infrastructure/adapters/output/messaging/EventEnvelope.java) is used for publication, while [EventMessage](../../services/core/infrastructure-core/src/main/java/com/jcondotta/banking/infrastructure/adapters/input/messaging/EventMessage.java) models consumption with context-specific payloads. Producer and consumer Java classes may differ; follow serialized fields and their interpretation.

`EventEnvelope.from` currently places domain event data directly in the envelope. Changing domain event data can therefore change published JSON without editing the publisher. Inspect consuming payloads and handlers before treating that change as internal.

## Review The Message Contract

For the affected event, check:

- destination and routing key
- envelope metadata, event type/version, identity, and timestamp meaning
- payload fields/types, missing versus null values, enums, and number/date encoding
- validation, ownership, and sensitive-data exposure to consumers
- the business meaning and timing of publication

Compare old and new messages against affected readers, including deployment order and replay of stored messages. Do not assume an additive field is accepted or that consumers validate `eventVersion`; inspect their actual behavior.

For intentional incompatibilities, resolve consumer updates, coexistence, rollout dependencies, and stored-message migration/replay implications in Design.

## Delivery And Replay

Distinguish a committed business change, outbox persistence, broker acceptance, and consumer completion. Establish the relevant guarantees from implementation and configuration rather than treating those outcomes as equivalent.

When affected, inspect retries, timeout outcomes, acknowledgement/error handling, duplicate detection, and ordering. Identify the deduplication value: event ID, aggregate ID, and correlation ID have different roles, and command idempotency does not itself establish consumer deduplication.

An outbox does not by itself establish exactly-once business processing. Verify consumer transaction and deduplication behavior separately, including replay of previously stored messages.

## Verification

Use [Testing](04-testing.md) for conventions and integration scope. Compatibility evidence should exercise serialized messages and consumer interpretation; a mocked deserializer or publisher-only test cannot establish both sides.

Existing entry points include recipients' `KafkaBrokerPublisherSerializationTest` and accounts' `BankAccountActivatedOutboxIT`. Each verifies a particular boundary; inspect assertions before claiming broader delivery guarantees.

Keep event documentation and tests aligned with the agreed contract. Report unverified consumer assumptions during [Validation](05-validation.md).
