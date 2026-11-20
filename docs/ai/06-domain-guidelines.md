# Domain Model Guidelines

Use this guide for domain construction, persistence reconstitution, and the shared `domain-core` contracts. It records patterns present in `accounts`, `recipients`, `transfers`, and `ledger`; follow bounded-context guidance where a local rule is more specific.

Do not introduce a new domain abstraction just because it appears in DDD literature. For new behavior or a new responsibility, resolve meaningful alternatives during Design before encoding the choice in a model.

## Shared `domain-core` Contracts

The modules under `services/core/domain-core` provide:

- `Entity<ID>` stores a required typed ID and defines equality by concrete class and ID.
- `AggregateRoot<ID>` extends `Entity` and keeps an in-memory list of registered domain events. `pullEvents()` returns a copy in registration order and clears the list; `hasEvents()` reports whether any remain.
- `DomainCollection<T>` copies the input collection, exposes immutable copies through `values()`, and provides iteration and streaming. Subclasses own validation and intention-revealing mutation methods.
- `AggregateRepository<A, ID>` is the base command-side port with `findById(ID)` and `save(A)`.
- `EntityId<T>` and `AggregateId<T>` provide typed identifiers; `asString()` delegates to the underlying value.
- `DomainEvent<A, D>` exposes event metadata and data, including event ID, aggregate ID, occurrence time, and a default event version of `1`.
- `DomainEventMetadata.of(...)` can create metadata with a generated event ID or accept an existing event ID.
- `Preconditions` provides required-value, non-blank, and argument checks using `DomainValidationException`.

Use these shared contracts where they fit. Avoid duplicating identity equality, aggregate event buffering, defensive collection copying, or required-value checks in a bounded context.

## Constructing Models

Concrete entities and aggregate roots in the bounded contexts use private constructors. The constructor establishes valid state by checking required values and any invariants that must hold for every instance, including restored instances.

Expose named static factories that describe the domain action. The names vary by model: `BankAccount.open(...)`, `Recipient.create(...)`, `BankTransfer.requestInternalTransfer(...)`, and `LedgerAccount.provision(...)`. Do not force every aggregate to use a generic `create(...)` name when a more specific action communicates intent.

Factories for new domain actions may establish initial state and register the event that represents that action. Events are registered on the aggregate only after the action's required inputs and state are valid. Use behavior methods for subsequent state changes; keep fields private and expose no public setters.

ID and time generation follow the existing factory and use-case contract. For example, `LedgerAccount.provision(...)` creates its ID, while other factories receive an ID. Some factories receive a timestamp; `BankAccount.open(...)` currently obtains its timestamp internally. Preserve the local contract and resolve changes to ID or time ownership during Design rather than imposing one repository-wide choice.

## Reconstituting Persisted State

Use a dedicated `restore(...)` factory where the aggregate supports persistence reconstitution. It receives the existing identity and persisted state, then delegates to the same private constructor used to enforce required state and invariant checks.

Restoring an entity is different from performing a new domain action:

- Do not generate a replacement identity or new creation time during restoration.
- Do not register creation or other domain events as a side effect of restoration. The event buffer of a restored aggregate starts empty.
- Preserve persisted lifecycle state and version information when the model has them.
- Do not use a creation factory and then mutate the object to imitate persisted state.

The persistence mapper owns translation between storage representation and domain representation. Convert stored primitives and enums into domain IDs, value objects, and collections, then call `restore(...)`. Do not put JPA, DynamoDB, Spring, or serialization concerns in the domain model.

Existing examples include `BankAccountEntityMapperImpl`, `RecipientEntityMapper`, `BankTransferEntityMapper`, and `LedgerAccountEntityMapper`. Their storage shapes differ, but each reconstructs domain types and calls the aggregate's restore factory.

For child entities, use a corresponding `restore(...)` factory when persistence must reconstitute their state. Keep child creation and mutation inside the aggregate boundary where the model requires it; `AccountHolder` is an existing example.

## IDs, Value Objects, And Collections

Use typed IDs for values with domain identity. Existing UUID-backed IDs generally provide `of(UUID)` for a known identity and `newId()` when that context creates one. Aggregate IDs implement `AggregateId<UUID>`; other entity IDs implement `EntityId<UUID>`. Do not generate a new ID while mapping persisted state.

Value objects validate their own domain values and expose named factories such as `of(...)` where established. Java records are common for simple immutable values, but use another form when the value has behavior or structure that calls for it. Prefer passing complete value objects into aggregates instead of passing primitives and rebuilding the objects inside.

Use `DomainCollection<T>` when a collection has domain invariants or behavior beyond storage. Keep its internal state encapsulated and expose immutable views. For simple collections that do not own behavior, follow the bounded context's existing model rather than introducing a collection abstraction automatically.

## Domain Events

Aggregate roots register domain events through `registerEvent(...)` when domain behavior produces them. `pullEvents()` is a one-time drain of the aggregate's current event buffer. A restored aggregate must not replay events from the historical actions that produced its persisted state.

The contexts implement the shared `DomainEvent` contract using metadata and event data records. Keep event payloads domain-owned; map them to transport or integration-event formats outside the domain. Event payload, versioning, timing, and publication changes can affect contracts and must be resolved during Design.

## Testing Construction And Restoration

Keep tests near the domain type under test. Protect observable behavior rather than constructor implementation details.

For construction, test the meaningful initial state, required-input failures, and initial events when the factory promises them. For restoration, test that persisted values and version are retained, invalid restored state is rejected, and no events are registered. For behavior methods, test state transitions, idempotent outcomes, rule violations, and emitted events where applicable.

Prefer focused fixtures and explicit domain values so the scenario is clear. Use fixed timestamps when time is relevant. Follow `docs/ai/04-testing.md` for shared test style and the service's local guidance for test fixtures and focused commands.
