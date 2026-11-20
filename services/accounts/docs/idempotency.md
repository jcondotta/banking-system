# Account command idempotency

Account opening and joint-holder creation require a client-supplied UUID header:

```http
X-Idempotency-Key: 550e8400-e29b-41d4-a716-446655440000
```

Reuse the key for retries of the same intention. Different intentions need new keys.
The server never generates a replacement key. Missing or malformed keys return 400.

## Key and replay

The identity is the idempotency key alone. Keys must be unique across both
account opening and joint-holder creation; reusing a key for another intention
returns 409 when its request fingerprint differs.

- Opening replays 201 with the original account Location and no body.
- Adding a holder replays 204, including when the account status has subsequently changed.
- Reusing a key with different normalized command data returns 409 ProblemDetail.
- The joint-holder fingerprint includes the target account ID, not just the body.
- A new key still goes through the domain's existing status and holder-limit rules.

This mechanism is not authentication or authorization. Any access checks must run
before returning a replay; possession of a key must never grant account access.
Keys are not scoped by tenant or operation. Introducing tenant-specific key
spaces would require revisiting this contract.

## Storage and architecture

`IdempotencyEntity` has its own enhanced-client schema pointing to the same table as
accounts and outbox. No new physical table, index, Redis deployment or TTL is required.

```text
partitionKey = IDEMPOTENCY#<UUID>
sortKey      = IDEMPOTENCY
requestHash, hashVersion, bankAccountId, createdAt
```

IdempotencyRepository.find(key) is an application port with a dedicated DynamoDB
adapter using strongly consistent GetItem. Idempotent application commands carry
the key; aggregates and the domain repository remain unaware of it.
BankAccountTransactionalRepository is the application write port. Its DynamoDB
implementation adds the receipt conditionally to the same transaction as the
version-protected account, holders and outbox appenders.

Only successful commits create receipts. There are no processing markers, cached
errors or expiration. Retention is indefinite: deleting a receipt permits a future
retry to execute again. Outbox cleanup must not delete receipts.

## Fingerprint V2

SHA-256 is calculated over the UTF-8 JSON serialization of the complete normalized
command, including its idempotency key and nested value objects. The joint-holder
command also includes the target account ID. New serializable record components
are included automatically; there is no manually maintained field list.

The dedicated `idempotencyJsonMapper` bean, defined in `IdempotencyJacksonConfig`,
is injected into `AccountCommandFingerprint` by qualifier. It is not a default
autowire candidate, so the HTTP mapper remains independently auto-configured.
The idempotency mapper sorts properties alphabetically and map entries by key,
includes null properties, writes dates as ISO strings, and fails on empty beans.
It does not share the HTTP mapper's customizations. Collection order remains
significant. Serialization failures abort the request before business execution.
This format is deterministic for the supported command types, not RFC 8785 JCS.

Keep the encoding and normalization stable while V2 receipts exist. Adding fields
or changing serialization can change fingerprints, even for otherwise equivalent
retries, and requires considering stored receipts. Explicit serialization ignores
or custom serializers can exclude fields and must be reviewed accordingly.

V2 replaces the experimental manual V1 encoding without backward comparison
support. Existing V1 receipts are rejected as unsupported. This rollout assumes
an environment whose previous data can be reset; no automatic reset or migration
is performed. Unknown stored versions continue to fail closed.

## Failures and concurrency

The conditional receipt insertion, not the initial lookup, prevents duplicate
commits. After an execution/write failure, the application rechecks the receipt:
a matching committed receipt is success; a different fingerprint is conflict.
This also covers a lost commit response and a concurrent holder-limit rejection.

Only DynamoDB transaction contention is automatically retried: at most three
attempts, waiting 25ms then 50ms, rebuilding/reloading the aggregate each time.
Unrelated optimistic-lock failures retain the existing conflict behavior.
Exhausted contention returns 503 with Retry-After: 1. Retry with the same key.
An absent receipt after an ambiguous failure is not treated as success.

The guarantee is one committed business change per retained key. Outbox delivery
remains at-least-once; consumers still need deduplication.

## Validation

Unit tests cover fingerprints, replay, mismatches, error reconciliation and retry
bounds. Focused integration tests cover HTTP replay, key conflicts across
intentions, target conflicts, conditional and optimistic-lock rollback, and
concurrent requests for both operations. Existing API fixtures now supply fresh
keys for new intentions.
