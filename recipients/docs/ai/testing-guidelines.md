# Recipients Testing Guidelines

Use these guidelines when adding, modifying, reviewing, or refactoring tests in the `recipients` bounded context.

Before introducing a new test pattern, inspect the nearest existing test for the same layer or component type. Prefer established local patterns over generic testing rules.

## Test Types

Use `*Test` for unit tests and `*IT` for integration tests.

Unit tests should cover:
- domain value objects, identities, aggregate behavior, ownership checks, exceptions, and failure reasons
- application command/query handlers, repository interactions, structured logging, concurrency-limit behavior, and failure propagation
- infrastructure mappers, DTO validation, controllers, exception handlers, properties, persistence mappers, and repository adapters

Integration tests should cover:
- HTTP flows with Rest Assured
- PostgreSQL persistence through Testcontainers
- Liquibase schema behavior
- concurrency behavior
- correlation IDs, API version headers, status codes, and `ProblemDetail` payloads when changed

Do not add browser E2E tests for this service unless a real browser-facing workflow is introduced.

## Naming And Structure

Follow the existing naming style:
- class names: `*Test` and `*IT`
- method names: `shouldExpectedBehavior_whenScenario`

Use Arrange-Act-Assert as the default structure, but keep the layout natural when a test needs setup helpers or multiple related observations.

Use `@ParameterizedTest` for input families such as blank values, invalid identifiers, invalid IBANs, pagination bounds, and validation limits.

Do not add `@DisplayName` unless the surrounding test class already uses it.

## Assertions

Use AssertJ as the default assertion library.

Prefer AssertJ `SoftAssertions` for new or modified tests when a single behavior has multiple related outputs that should be evaluated together, such as:
- persisted aggregate state
- response status, headers, and body
- `ProblemDetail` fields
- structured log level, event type, and outcome

Existing tests may use JUnit `assertAll` with AssertJ assertions. Preserve that style when working in tests that already use it unless the test is being meaningfully reworked; do not refactor tests solely to replace `assertAll` with `SoftAssertions`.

Use direct assertions when there is only one relevant outcome or when grouping assertions would reduce readability.

Do not split one coherent behavior into many tests only to satisfy an arbitrary assertion count.

Prefer assertions that verify observable behavior. Avoid tests that only mirror implementation details or framework wiring.

## Mocks And Test Data

Use Mockito for application and controller unit tests when collaborators are ports, handlers, mappers, or servlet requests.

Prefer existing fixtures and test support:
- `RecipientFixtures`
- `RecipientTestData`
- `TimeFactory`
- `BlankValuesSource`
- `ValidatorTestFactory`

Keep tests deterministic. Use fixed clocks and explicit fixtures instead of depending on wall-clock time or random data when the exact value matters.

## Integration Tests

Use the existing `@IntegrationTest` annotation for Spring Boot integration tests. It already configures:
- random web port
- `test` profile
- Testcontainers initialization

Use Rest Assured for HTTP requests and keep request setup close to the endpoint under test.

For HTTP integration tests, follow the existing request-specification pattern, including API versioning and request/response logging on validation failures.

Verify both HTTP behavior and the important persistence side effect when the endpoint changes state.

Clean or isolate state through existing repository/test support patterns. Do not introduce shared mutable state between tests.

## Structured Logging Tests

For application handlers that emit structured logs, follow the existing handler-test pattern.

- Capture log events with `ListAppender` through `StructuredLogEventSupport`.
- Attach the appender during setup and detach it during teardown.
- Verify structured log semantics rather than formatted log messages.
- Verify the expected log level, event type, outcome, and relevant structured keys.
- Cover success, expected/domain failure, and unexpected failure logging when those paths exist.
- Follow the nearest handler test for the exact setup and assertions.

## Coverage And Test Selection

Keep meaningful coverage for changed behavior. Do not chase coverage by testing trivial constructors, generated framework behavior, or configuration binding that is already covered elsewhere.

For core business rules, prefer focused domain tests with mutation-resistant assertions.

For API, schema, persistence, logging, observability, or concurrency changes, update the matching integration coverage.

Use `docs/ai/change-playbook.md` to choose the focused Maven command for the change type.

## Test-First Guidance

Prefer test-first for new behavior and bug fixes when the expected behavior is clear.

Do not block small refactors or mechanical renames on writing a failing test first when existing focused tests already cover the behavior.