# Recipients Testing Guidelines

Use these guidelines when adding or changing tests in the `recipients` bounded context. Prefer the style already used by the nearest test class over generic company-wide testing rules.

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

Use `assertAll` when a single behavior has multiple related outputs, such as:
- persisted aggregate state
- response status, headers, and body
- `ProblemDetail` fields
- structured log level, event type, and outcome

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

Verify both HTTP behavior and the important persistence side effect when the endpoint changes state.

Clean or isolate state through existing repository/test support patterns. Do not introduce shared mutable state between tests.

## Coverage And Test Selection

Keep meaningful coverage for changed behavior. Do not chase coverage by testing trivial constructors, generated framework behavior, or configuration binding that is already covered elsewhere.

For core business rules, prefer focused domain tests with mutation-resistant assertions.

For API, schema, persistence, logging, observability, or concurrency changes, update the matching integration coverage.

Use `docs/ai/change-playbook.md` to choose the focused Maven command for the change type.

## Test-First Guidance

Prefer test-first for new behavior and bug fixes when the expected behavior is clear.

Do not block small refactors or mechanical renames on writing a failing test first when existing focused tests already cover the behavior.
