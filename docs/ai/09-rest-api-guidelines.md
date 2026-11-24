# REST API Guidelines

Use this guide for HTTP endpoints and their clients. Adapter responsibilities belong in [Infrastructure](08-infrastructure-guidelines.md); use [Design](02-design.md) to resolve behavior, compatibility, versioning, and rollout choices.

## Locate The HTTP Boundary

Inspect the controller interface/implementation, request and response DTOs, mappers, exception handlers, route/version configuration, and known HTTP clients. Include external clients in the compatibility assessment; report any that cannot be inspected.

An existing service-to-service example is accounts' REST lookup controller paired with transfers' [BankAccountLookupClient](../../services/transfers/src/main/java/com/jcondotta/banking/transfers/infrastructure/adapters/output/bank_account/BankAccountLookupClient.java) and adapter. Follow the actual HTTP request and response rather than assuming client and server Java types are shared.

## Review The HTTP Contract

For the affected endpoint, check:

- path, method, query parameters, headers, and API-version selection
- request/response fields, validation, missing versus null values, enums, and number/date encoding
- status codes, error bodies, and not-found/conflict semantics
- pagination, filtering, and ordering when applicable
- ownership, authorization, and sensitive-data exposure

Check configuration and filters as well as controller annotations. A response field or status-code change can affect clients without changing the route.

## Compatibility And Request Replay

Compare previous and proposed requests, responses, and behavior against the affected clients. Check mixed client/server versions and deployment order when they may coexist; an additive field is not automatically accepted by every reader. A version header alone does not establish a migration strategy.

For intentional incompatibilities, resolve client updates, coexistence, and rollout dependencies in Design before changing the contract.

For retryable writes, distinguish an HTTP response or timeout from whether the business change committed. Check required idempotency headers and replay/conflict behavior. Accounts' [idempotency contract](../../services/accounts/docs/idempotency.md) owns its key, fingerprint, and retention rules.

## Verification

Use [Testing](04-testing.md) for HTTP test conventions and integration scope. Verify the serialized response, status, headers, errors, and affected client interpretation; compiling DTOs alone does not establish compatibility.

Keep endpoint documentation and tests aligned with the agreed contract. Report unverified client assumptions during [Validation](05-validation.md).
