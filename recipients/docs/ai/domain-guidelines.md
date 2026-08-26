# Domain Guidelines

Compact reference for domain-modeling conventions in this bounded context.
Rules here are established by the repository. Do not invent conventions from
generic DDD practice; resolve first-use decisions in Design.

## 1. Core Rules

- Domain code is framework-free and follows the dependency rules in section 9.
- Domain objects own invariants and business behavior.
- Application orchestrates domain behavior and owns use-case concerns such as
  ID generation, time, logging, observations, and transaction coordination.
- Use domain IDs and value objects once input has crossed into the use case.
- Create value objects for values with domain validation, normalization,
  masking/formatting behavior, or meaningful type safety. Do not wrap every
  primitive by default.

## 2. Package Shape

Use concept-focused domain packages: `aggregate`, `exceptions`, `identity`,
`repository`, `validation`, and `value_objects` under `domain/<concept>/`.

Established elements: aggregate roots, identifiers, value objects, domain
exceptions, validation constants, failure reasons, and aggregate repository
ports. Child entities and domain services require a Design decision before
first use.

## 3. Aggregates

Rules:

- Place aggregate roots under `domain/<concept>/aggregate`.
- Make aggregate classes `final` and extend `AggregateRoot<AggregateIdType>`.
- Use a private constructor.
- Use `create(...)` for new instances.
- Use `restore(...)` for persistence reconstitution.
- The application supplies aggregate IDs and timestamps; the aggregate receives
  them through `create(...)`.
- Validate required state in the constructor and behavior methods.
- Accept already-validated value objects and identifiers.
- Keep fields private; use `final` for identity/ownership/creation data where
  possible.
- Mutate state only through behavior methods.
- Expose getters as needed; do not expose setters.
- Equality is identity-based through `Entity<ID>`.
- Reference other aggregates or bounded-context concepts by ID value object.
- `restore(...)` may accept existing version state.
- Restoring an aggregate must not register domain events.
- Keep JPA/Spring/persistence details out of aggregates.

Minimal shape:

```java
public final class Order extends AggregateRoot<OrderId> {
  private final OwnerId ownerId;
  private OrderName name;
  private final Instant createdAt;
  private Long version;
  private Order(OrderId id, OwnerId ownerId, OrderName name, Instant createdAt, Long version) {
    super(required(id, OrderError.ORDER_ID_NOT_PROVIDED));
    this.ownerId = required(ownerId, OrderError.OWNER_ID_NOT_PROVIDED);
    this.name = required(name, OrderError.ORDER_NAME_NOT_PROVIDED);
    this.createdAt = required(createdAt, OrderError.CREATED_AT_NOT_PROVIDED);
    this.version = version;
  }

  public static Order create(OrderId id, OwnerId ownerId, OrderName name, Instant createdAt) {
    return new Order(id, ownerId, name, createdAt, null);
  }
  public static Order restore(OrderId id, OwnerId ownerId, OrderName name, Instant createdAt, Long version) {
    return new Order(id, ownerId, name, createdAt, version);
  }
  public void assertOwnedBy(OwnerId ownerId) {
    required(ownerId, OrderError.OWNER_ID_NOT_PROVIDED);
    if (!this.ownerId.equals(ownerId)) {
      throw new OrderOwnershipMismatchException(getId(), ownerId);
    }
  }
  public void rename(OrderName name) {
    this.name = required(name, OrderError.ORDER_NAME_NOT_PROVIDED);
  }
}
```

Do not make aggregate state public, expose setters, or annotate aggregates with
`@Entity`.

## 4. Value Objects

Rules:

- Use Java records for simple value objects.
- Place them under `domain/<concept>/value_objects`.
- Use record component `value` for single-value wrappers.
- Validate in the compact constructor and provide `of(...)`.
- Keep value-specific validation messages and domain limits on the value object.
- Throw `DomainValidationException` directly or through `Preconditions`.
- Normalize before final validation when canonical form affects validity.
- Rely on record structural equality.
- Put value-specific domain behavior on the value object, such as masking.

Observed normalization: name-like values strip surrounding whitespace and
collapse repeated whitespace; code-like values remove irrelevant whitespace and
uppercase when uppercase is the canonical domain format.

Minimal shape:

```java
public record DisplayName(String value) {
  public static final int MAX_LENGTH = 50;
  public static final String NAME_NOT_PROVIDED = "display name must be provided";
  public static final String NAME_NOT_BLANK = "display name must not be blank";
  
  public DisplayName {
    required(value, NAME_NOT_PROVIDED);
    value = value.strip().replaceAll("\\s+", " ");
    requiredNotBlank(value, NAME_NOT_BLANK);
    checkArgument(value.length() <= MAX_LENGTH, "display name is too long");
  }

  public static DisplayName of(String value) {
    return new DisplayName(value);
  }
}
```

Do not normalize the same value in adapters before constructing the value object.

## 5. Identifiers

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
public record OrderId(UUID value) implements AggregateId<UUID> {
  
  public OrderId {
    Preconditions.required(value, "order id value must be provided");
  }
  
  public static OrderId of(UUID value) {
    return new OrderId(value);
  }
  
  public static OrderId newId() {
    return new OrderId(UUID.randomUUID());
  }
}

public record OwnerId(UUID value) implements EntityId<UUID> {
  
  public OwnerId {
    Preconditions.required(value, "owner id value must be provided");
  }
  
  public static OwnerId of(UUID value) {
    return new OwnerId(value);
  }
}
```

## 6. Exceptions And Failure Reasons

Use domain exceptions for invalid domain data, rule violations, conflicts, and
missing domain resources. Do not choose exception types from HTTP status codes.

Shared hierarchy:

```text
DomainException
|-- DomainValidationException
|-- DomainRuleViolationException
|-- DomainConflictException
`-- DomainNotFoundException
```

Category choices:

- `DomainValidationException` is used when supplied data cannot represent a
  valid domain value or state. Outer layers may catch it to handle all domain
  validation uniformly.
  Examples: missing required value, blank value, invalid format, invalid length,
  invalid range, invalid checksum, or invalid date/value construction.
- `DomainRuleViolationException` is for operations where individual values are
  valid, but the operation violates a business rule involving current state or
  ownership.
- `DomainConflictException` is for operations that conflict with already-existing
  domain state or resources.
- `DomainNotFoundException` is for required domain resources that do not exist.

Rules:

- Put concept-specific exceptions under `domain/<concept>/exceptions`.
- Make concrete domain exceptions `final`.
- Use human-readable message constants on exception classes.
- Implement `FailureReasonProvider` when callers need a stable
  machine-readable reason.
- Use `FailureReason.normalize()` for lowercase log/error values.
- Expose contextual values through getters when useful.
- Mask sensitive values before storing them on exceptions.
- Keep HTTP status, ProblemDetail, SQL constraint names, database exception
  types, and transport details outside domain exceptions.
- Do not parse exception messages to determine failure categories.

## 7. Repository Ports And CQRS

Command/write aggregate persistence ports belong in domain.

Rules:

- Place write ports under `domain/<concept>/repository`.
- Extend `AggregateRepository<Aggregate, AggregateId>`.
- Return aggregates, not read models.
- Accept domain IDs and aggregates, not raw UUIDs or persistence entities.
- Return `Optional<Aggregate>` for lookups that may miss.
- `save(Aggregate)` returns `void`.
- Add only command-side operations required by use cases.
- Keep Spring Data, JPA, SQL, pagination, and projections out of domain ports.

```java
public interface OrderRepository extends AggregateRepository<Order, OrderId> {
  Optional<Order> findByOwnerIdAndId(OwnerId ownerId, OrderId orderId);
  void delete(Order order);
}
```

Read/query repository ports belong in application.

Rules:

- Place read ports under `application/<concept>/query`.
- Return application query models/projections, not aggregates.
- Keep paging/filter types outside domain ports.
- Domain IDs may be query parameters.

Placement: `domain/<concept>/repository/<Aggregate>Repository` for command side;
`application/<concept>/query/<Aggregate>QueryRepository` for read side.

Do not extend `JpaRepository` from a domain port. Do not put projection queries
on the domain aggregate repository.

## 8. Domain Events

Shared `domain-core` supports domain events; production recipients events are
not yet established.

Established core rules:

- Events implement `DomainEvent<A extends AggregateId<?>>`.
- Events expose `EventId eventId()`, aggregate ID, `Instant occurredAt()`, and
  default `version()` of `1`.
- Aggregate roots register events internally with `registerEvent(...)`.
- `pullEvents()` returns registered events and clears them.
- Restored aggregates must not register events.
- Domain events must not depend on Spring, messaging, serialization, or
  integration-event metadata.
- Application maps domain events to technical/integration events.

First production event in a bounded context needs Design confirmation for
payload shape and registration trigger.

## 9. Constants, Creation, And Dependencies

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
- Generated aggregate IDs: `newId()`.
- Existing IDs: `of(UUID)`.
- Aggregates: private constructor plus `create(...)` and `restore(...)`.
- Dedicated domain factory classes and builders are not established. Add one
  only after Design confirms named static factories are insufficient.

Allowed dependencies: Java standard library, shared `domain-core`, and
same bounded-context domain types.

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

Represent external aggregate relationships by ID value object unless Design
establishes another cross-context rule.

## 10. Decision Guide

- Value object: use for domain validation, canonicalization, format behavior,
  masking/display behavior, or type safety. Keep primitive for transport/query
  criteria with no domain invariant.
- Aggregate rule: use when it protects valid domain state or expresses business
  behavior.
- Application rule: use for orchestration concerns such as repository calls, ID
  generation, time, logging, observations, and transaction coordination.
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
- Domain service: no convention established. Consider only for a business rule
  that does not belong naturally to one aggregate or value object, and resolve
  ownership in Design first.
