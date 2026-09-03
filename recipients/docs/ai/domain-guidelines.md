# Domain Guidelines

Compact reference for domain-modeling conventions in the `recipients` bounded
context.

These rules describe conventions already established by this repository. Do not
introduce a new domain concept only because it exists in generic DDD literature
or in another bounded context. First-use decisions that affect behavior,
responsibility, public contracts, architecture, persistence, observability, or
security must be resolved in Design.

## 1. Domain Boundary

- Domain code is framework-free.
- Domain objects own invariants and business behavior.
- Application code orchestrates use cases and owns ID generation when the use
  case creates the identity, time sourcing, repository coordination, logging,
  observations, concurrency limits, operational failure classification, and
  transaction coordination.
- Infrastructure owns Spring, HTTP, persistence, database, messaging,
  serialization, and runtime configuration.
- Once input crosses into a use case, prefer domain IDs and value objects over
  raw primitives for values with domain meaning.
- Create value objects for validation, canonicalization, masking/formatting
  behavior, or meaningful type safety. Do not wrap every primitive by default.

Allowed dependencies:

```text
domain -> Java standard library
domain -> shared domain-core
domain -> same bounded-context domain types
```

Forbidden dependencies:

```text
domain -> application
domain -> infrastructure
domain -> bootstrap/runtime
domain -> Spring
domain -> JPA/Jakarta persistence
domain -> REST DTOs/controllers/ProblemDetail
domain -> Spring Data/Pageable/Page
domain -> logging frameworks
domain -> messaging/serialization infrastructure
```

Represent external aggregate or bounded-context relationships by a local domain
ID value object unless Design establishes a different rule.

## 2. Shared Domain Core

The shared `domain-core` module provides these contracts:

- `Entity<ID extends EntityId<?>>`: stores a required ID and implements equality
  and hash code by concrete class plus ID.
- `AggregateRoot<ID extends AggregateId<?>>`: extends `Entity`, supports
  protected `registerEvent(...)`, `hasEvents()`, and `pullEvents()`.
- `DomainCollection<T>`: keeps a defensive mutable internal list, returns
  immutable defensive copies through `values()`, and exposes `stream()` and
  iteration.
- `AggregateRepository<A, ID>`: command-side base port with `findById(ID)` and
  `save(A)`.
- `EntityId<T>` and `AggregateId<T>`: typed ID contracts; `EntityId.asString()`
  delegates to `value().toString()`.
- `EventId`: UUID-backed domain event ID with `of(UUID)` and `newId()`.
- `DomainEvent<A extends AggregateId<?>, D>`: provides metadata and data,
  exposes `eventId()`, `aggregateId()`, and `occurredAt()`, and defaults
  `eventVersion()` to `1`.
- `Preconditions`: `required`, `requiredNotBlank`, and `checkArgument`, all
  raising `DomainValidationException` on failure.
- Domain exception hierarchy: `DomainException`, `DomainValidationException`,
  `DomainRuleViolationException`, `DomainConflictException`, and
  `DomainNotFoundException`.

Use these contracts directly instead of reimplementing identity equality, event
buffering, required-value checks, or command aggregate repository shape.

## 3. Package Shape

Use concept-focused packages under `domain/<concept>/`:

- `aggregate`: aggregate roots and internal aggregate entities/collections when
  established.
- `exceptions`: concept-specific domain exceptions.
- `identity`: domain ID records.
- `repository`: command-side aggregate repository ports.
- `validation`: aggregate-level validation constants.
- `value_objects`: value objects and nested value-object groupings.
- `events`: domain events, once established for this bounded context.
- `policies`: domain policies, only after Design confirms the rule does not
  belong naturally to an aggregate or value object.

Current `recipients` domain elements are aggregate root, identifiers, value
objects, domain exceptions, domain events, validation constants, and aggregate
repository ports.

Child entities, domain collections, domain policies, and domain services are
supported patterns in the wider repository, but their first use in `recipients`
needs Design confirmation.

## 4. Aggregate Roots

Rules:

- Place aggregate roots under `domain/<concept>/aggregate`.
- Make aggregate classes `final` and extend `AggregateRoot<AggregateIdType>`.
- Use a private constructor.
- Use `create(...)` for new instances.
- Use `restore(...)` for persistence reconstitution.
- The application supplies aggregate IDs and timestamps unless Design
  explicitly assigns that responsibility to the aggregate.
- Validate required state in the constructor and behavior methods.
- Accept already-validated value objects and identifiers.
- Keep fields private; use `final` for identity, ownership, and creation data
  where possible.
- Mutate state only through behavior methods.
- Expose getters as needed; do not expose setters.
- Equality is identity-based through `Entity<ID>`.
- Reference other aggregates or bounded-context concepts by ID value object.
- `restore(...)` may accept existing version state.
- Restoring an aggregate must not register domain events.
- Keep JPA, Spring, and persistence details out of aggregates.

Minimal shape:

```java
public final class Recipient extends AggregateRoot<RecipientId> {
  private final BankAccountId bankAccountId;
  private RecipientName recipientName;
  private Iban iban;
  private final Instant createdAt;
  private Long version;

  private Recipient(
    RecipientId recipientId,
    BankAccountId bankAccountId,
    RecipientName recipientName,
    Iban iban,
    Instant createdAt,
    Long version
  ) {
    super(required(recipientId, RecipientError.RECIPIENT_ID_NOT_PROVIDED));
    this.bankAccountId = required(bankAccountId, RecipientError.BANK_ACCOUNT_ID_NOT_PROVIDED);
    this.recipientName = required(recipientName, RecipientError.RECIPIENT_NAME_NOT_PROVIDED);
    this.iban = required(iban, RecipientError.IBAN_NOT_PROVIDED);
    this.createdAt = required(createdAt, RecipientError.CREATED_AT_NOT_PROVIDED);
    this.version = version;
  }

  public static Recipient create(
    RecipientId recipientId,
    BankAccountId bankAccountId,
    RecipientName recipientName,
    Iban iban,
    Instant createdAt
  ) {
    return new Recipient(recipientId, bankAccountId, recipientName, iban, createdAt, null);
  }

  public static Recipient restore(
    RecipientId recipientId,
    BankAccountId bankAccountId,
    RecipientName recipientName,
    Iban iban,
    Instant createdAt,
    Long version
  ) {
    return new Recipient(recipientId, bankAccountId, recipientName, iban, createdAt, version);
  }

  public void update(RecipientName recipientName, Iban iban) {
    this.recipientName = required(recipientName, RecipientError.RECIPIENT_NAME_NOT_PROVIDED);
    this.iban = required(iban, RecipientError.IBAN_NOT_PROVIDED);
  }
}
```

Do not make aggregate state public, expose setters, or annotate aggregates with
`@Entity`.

## 5. Child Entities And Collections

`recipients` does not currently have child entities or domain collections.

If introduced:

- Child entities extend `Entity<ChildEntityId>`.
- Child entity identity, lifecycle, and mutation access must be owned by the
  aggregate boundary.
- Use package-private factories or behavior when only the aggregate should
  create or mutate the child.
- Use a domain collection when a collection owns invariants or behavior beyond
  simple storage.
- Prefer extending `DomainCollection<T>` when its defensive-copy and iteration
  behavior fits the model.
- Keep collection mutation methods narrow and intention-revealing.
- Validate collection configuration on construction and mutation.
- Do not expose mutable internal lists.

First use in `recipients` needs Design confirmation for aggregate boundary,
ownership, lifecycle, and invariants.

## 6. Value Objects

Rules:

- Use Java records for simple value objects.
- Place them under `domain/<concept>/value_objects`.
- Use record component `value` for single-value wrappers.
- Validate in the compact constructor and provide `of(...)`.
- Keep value-specific validation messages and domain limits on the value object.
- Throw `DomainValidationException` directly or through `Preconditions`.
- Normalize before final validation when canonical form affects validity.
- Rely on record structural equality.
- Put value-specific domain behavior on the value object, such as masking for
  logs and error details. Authorized recipient read models expose the full IBAN.

Observed normalization:

- Name-like values strip surrounding whitespace and collapse repeated
  whitespace.
- Code-like values remove irrelevant whitespace and uppercase when uppercase is
  the canonical domain format.

Minimal shape:

```java
public record RecipientName(String value) {
  public static final int MAX_LENGTH = 50;
  public static final String NAME_NOT_PROVIDED = "recipient name must be provided";
  public static final String NAME_NOT_BLANK = "recipient name must not be blank";
  public static final String NAME_MUST_NOT_EXCEED_LENGTH = "recipient name must not exceed %d characters";

  public RecipientName {
    required(value, NAME_NOT_PROVIDED);
    value = value.strip().replaceAll("\\s+", " ");
    requiredNotBlank(value, NAME_NOT_BLANK);
    checkArgument(value.length() <= MAX_LENGTH, NAME_MUST_NOT_EXCEED_LENGTH.formatted(MAX_LENGTH));
  }

  public static RecipientName of(String value) {
    return new RecipientName(value);
  }
}
```

Do not normalize the same value in adapters before constructing the value
object.

## 7. Identifiers

Rules:

- Identifiers are records over `UUID`.
- Aggregate root IDs implement `AggregateId<UUID>`.
- Other domain IDs implement `EntityId<UUID>`.
- Validate null UUID values in the compact constructor.
- Provide `of(UUID)` for existing UUIDs.
- Provide `newId()` only when this service creates that identity.
- Use domain ID types in aggregate behavior and repository ports.
- Use `asString()` from `EntityId` when external code needs a string.

```java
public record RecipientId(UUID value) implements AggregateId<UUID> {
  public static final String ID_NOT_PROVIDED = "recipient id value must be provided";

  public RecipientId {
    Preconditions.required(value, ID_NOT_PROVIDED);
  }

  public static RecipientId of(UUID value) {
    return new RecipientId(value);
  }

  public static RecipientId newId() {
    return new RecipientId(UUID.randomUUID());
  }
}

public record BankAccountId(UUID value) implements EntityId<UUID> {
  public static final String ID_NOT_PROVIDED = "bank account id value must be provided";

  public BankAccountId {
    Preconditions.required(value, ID_NOT_PROVIDED);
  }

  public static BankAccountId of(UUID value) {
    return new BankAccountId(value);
  }
}
```

## 8. Exceptions And Operational Failure Classification

Use domain exceptions for invalid domain data, rule violations, conflicts, and
missing domain resources. Do not choose exception types from HTTP status codes.

Category choices:

- `DomainValidationException`: supplied data cannot represent a valid domain
  value or state. Examples: missing required value, blank value, invalid format,
  invalid length, invalid range, invalid checksum, or invalid construction.
- `DomainRuleViolationException`: individual values are valid, but the operation
  violates a business rule involving current state, ownership, or lifecycle.
- `DomainConflictException`: the operation conflicts with already-existing
  domain state or resources.
- `DomainNotFoundException`: a required domain resource does not exist.

Rules:

- Put concept-specific exceptions under `domain/<concept>/exceptions`.
- Make concrete domain exceptions `final`.
- Use human-readable message constants on exception classes.
- Expose contextual values through getters when useful.
- Mask sensitive values before storing them on exceptions.
- Keep HTTP status, ProblemDetail, SQL constraint names, database exception
  types, and transport details outside domain exceptions.
- Do not parse exception messages to determine failure categories.

Operational failure classification belongs in application:

- Use `RecipientFailureReason` for stable log reasons and normalization.
- Map concrete domain/application exception types explicitly in application.
- Keep `INTERNAL_ERROR`, log normalization, and other operational categories out
  of domain types.
- Do not make domain exceptions implement application-facing reason providers.

## 9. Repository Ports And CQRS

Command/write aggregate persistence ports belong in domain.

Rules:

- Place write ports under `domain/<concept>/repository`.
- Extend `AggregateRepository<Aggregate, AggregateId>`.
- Return aggregates, not read models.
- Accept domain IDs and aggregates, not raw UUIDs or persistence entities.
- Return `Optional<Aggregate>` for lookups that may miss.
- `save(Aggregate)` returns `void`.
- Add only command-side operations required by use cases.
- Scope ownership-sensitive lookups by `BankAccountId` and `RecipientId`; both a
  missing recipient and one owned by another account use not-found semantics.
- Keep Spring Data, JPA, SQL, pagination, filters, and projections out of
  domain ports.

```java
public interface RecipientRepository extends AggregateRepository<Recipient, RecipientId> {
  Optional<Recipient> findByBankAccountIdAndId(BankAccountId bankAccountId, RecipientId recipientId);

  void delete(Recipient recipient);
}
```

Read/query repository ports belong in application.

Rules:

- Place read ports under `application/<concept>/query`.
- Return application query models/projections, not aggregates.
- Keep paging/filter types outside domain ports.
- Domain IDs may be query parameters.

Do not extend `JpaRepository` from a domain port. Do not put projection queries
on the domain aggregate repository.

## 10. Domain Events

Shared `domain-core` supports domain events. `RecipientCreatedEvent` and
`RecipientDeletedEvent` are established production events in `recipients`.

Rules:

- Place events under `domain/<concept>/events`.
- Implement `DomainEvent<A extends AggregateId<?>, D>`.
- Use records unless a concrete need requires a class.
- Validate `eventId`, `aggregateId`, and `occurredAt` with
  `DomainEventErrors`.
- Include business payload needed by downstream domain/application behavior.
- Use `eventVersion()` only for event schema versioning.
- Aggregate roots register events internally with `registerEvent(...)`.
- `pullEvents()` returns registered events in registration order and clears the
  aggregate event buffer.
- Restored aggregates must not register events.
- Domain events must not depend on Spring, messaging, serialization, outbox, or
  integration-event metadata.
- Application or infrastructure maps domain events to technical/integration
  events.

New event types or changes to existing event payloads need Design confirmation
for payload shape, event ID/time sourcing, registration trigger, publication
responsibility, compatibility, and testing scope.

## 11. Domain Policies And Services

Domain policies are established in `accounts`, not in `recipients`. Domain
services are not established in this repository.

Use a policy only when:

- the rule is domain behavior;
- the rule does not fit naturally inside one aggregate or value object;
- the rule needs a replaceable strategy or registry; and
- the dependency remains domain-owned and framework-free.

Use a domain service only after Design confirms that the behavior belongs in the
domain and cannot be expressed clearly by an aggregate, value object,
collection, or policy.

Do not use policies or services for application orchestration, persistence,
logging, observations, transaction coordination, or integration calls.

## 12. Constants And Creation

Constants:

- Keep value-object validation messages and limits on the value object.
- Use concept-specific validation constants for aggregate-required messages.
- Use domain enums for stable domain failure categories.
- Keep API paths, headers, request validation annotations, table names, column
  names, SQL constraint names, JPA lengths, logging names, and metric keys
  outside domain.
- Share a constant only when it is truly a domain invariant.

Creation:

- Value objects: record constructor plus `of(...)`.
- Existing IDs: `of(UUID)`.
- Generated IDs: `newId()` only for identities created by this service.
- Aggregates: private constructor plus `create(...)` and `restore(...)`.
- Dedicated domain factory classes and builders are not established. Add one
  only after Design confirms named static factories are insufficient.

## 13. Testing Domain Changes

Add or update focused domain tests when changing:

- aggregate construction, restore behavior, lifecycle, or mutation;
- value-object validation, normalization, formatting, or masking;
- identifier creation and null validation;
- exception message or contextual values;
- repository port shape;
- domain event payload, validation, registration, or pull semantics;
- child entity, domain collection, policy, or service behavior.

Focused commands:

```text
../mvnw test -Dtest=*Recipient*Test
../mvnw test -Dtest=*Iban*Test,*RecipientName*Test,*BankAccountId*Test,*RecipientId*Test
```

For shared `domain-core` changes, run the relevant core module tests from the
repository root.

## 14. Decision Guide

- Value object: use for domain validation, canonicalization, format behavior,
  masking/display behavior, or type safety. Keep primitive for transport/query
  criteria with no domain invariant.
- Aggregate rule: use when it protects valid domain state or expresses business
  behavior.
- Application rule: use for orchestration concerns such as repository calls, ID
  generation, time, logging, operational failure classification, observations,
  and transaction coordination.
- Domain repository: command side, aggregate persistence, aggregate return
  types.
- Application query repository: read side, projections, pagination, filters, and
  query models.
- Exception category: use `DomainValidationException` for invalid value/state
  construction, `DomainRuleViolationException` for valid data that violates a
  stateful business rule, `DomainConflictException` for already-existing
  conflicting state, and `DomainNotFoundException` for missing required domain
  resources.
- Domain event: use for a meaningful business fact raised by aggregate behavior
  when downstream behavior should react; map technical events outside domain.
- Child entity: use only inside an aggregate boundary when identity and
  lifecycle are subordinate to the aggregate.
- Domain collection: use when collection-level invariants or behavior matter.
- Domain policy: use for domain rules needing a replaceable strategy or
  registry.
- Domain service: no convention established. Consider only for a domain rule
  that does not belong naturally to one aggregate, value object, collection, or
  policy, and resolve ownership in Design first.
