# Repository Guidelines

## Project Map

This is a Java 25, Spring Boot 4, Maven banking system. The root `pom.xml` reactor contains all code modules under `services/`:

- `services/core/`: shared `money-core`, `movement-core`, `application-core`, `infrastructure-core`, and opt-in `outbox-infrastructure` modules. Domain contracts come from the published `io.github.jcondotta:domain-core` library.
- `services/accounts/`: single-module bounded context; its API and transactional-outbox worker run in the same Spring Boot application.
- `services/recipients/`, `services/transfers/`, `services/ledger/`: single-module bounded contexts with architectural boundaries organized by package.
- `docker/`, `k8s/`, `terraform/`: local, Kubernetes, and cloud infrastructure where present.

Read the affected service's `AGENTS.md` for its package map, contracts, commands, and local guidance. Prefer local guidance for paths, commands, fixtures, contracts, and bounded-context behavior when it is more specific than shared guidance.

## Select Relevant Guidance

Load only the documents relevant to the task and current stage. The conditions below are cumulative: a domain implementation may also require implementation and testing guidance.

| Task or stage | Read |
| --- | --- |
| Explain existing behavior | Local `AGENTS.md` and relevant production code, tests, or configuration |
| Clarify requirements for a change | [Requirements](docs/ai/01-requirements.md) |
| Resolve behavioral, contract, or architectural alternatives | [Design](docs/ai/02-design.md) |
| Implement a sufficiently defined change | [Implementation](docs/ai/03-implementation.md) |
| Add, change, or review tests | [Testing](docs/ai/04-testing.md) |
| Validate a completed change | [Validation](docs/ai/05-validation.md) |
| Change domain models or shared domain contracts | [Domain guidelines](docs/ai/06-domain-guidelines.md) |
| Change application responsibilities or shared application contracts | [Application guidelines](docs/ai/07-application-guidelines.md) |
| Change adapters, transport/storage mapping, or runtime wiring | [Infrastructure guidelines](docs/ai/08-infrastructure-guidelines.md) |
| Change HTTP endpoints or their clients | [REST API guidelines](docs/ai/09-rest-api-guidelines.md) |
| Change protobuf definitions, gRPC services, or their clients | [gRPC guidelines](docs/ai/10-grpc-guidelines.md) |
| Change published messages or their consumers | [Event contract guidelines](docs/ai/11-event-contracts-guidelines.md) |
| Change logs, metrics, tracing, correlation, or telemetry configuration | [Observability guidelines](docs/ai/12-observability-guidelines.md) |

Read only the protocol guides affected by the change. A shared model change may affect more than one boundary; inspect its uses to determine which guides apply.

Use the affected context's change playbook when applicable. Resolve shared-document paths from the repository root or relative to the context you are working in.

## Decisions And Autonomy

Start at the earliest unresolved stage. Stages describe responsibilities, not mandatory ceremony for every task; small mechanical changes may need little or no requirements or design work. Do not repeat established analysis or decisions.

A request to implement does not itself resolve requirements or design. Before implementing a new feature or non-trivial behavioral change, read Design and pass its gate with no OPEN decisions. Existing patterns support recommendations; they do not replace the user's decision on meaningful alternatives.

Concentrate user interaction in Design. Continue useful requirements investigation and carry open questions into Design. Implementation, testing, and validation are autonomous for the resolved design; do not ask about already-resolved decisions or purely mechanical details.

If a later stage reveals an unresolved meaningful decision, follow that stage's procedure. In implementation or testing, return to Design before encoding the choice. In validation, complete the review before reporting findings. Never present an autonomous choice as user-approved.

## Build And Test

Run commands from the repository root:

- `./mvnw clean test`: run unit tests across the reactor.
- `./mvnw clean verify`: build all modules, run unit/integration tests, and generate JaCoCo reports.
- `docker compose -f docker/docker-compose.yml up -d`: start local dependencies.

For service startup, focused tests, and mutation testing, use the service's `AGENTS.md` and playbook. Run verification appropriate to the change and review the completed result.

## Shared Conventions

Use Java conventions and 4-space indentation. Service packages live under `com.jcondotta.banking`; preserve existing namespaces in shared modules, including `com.jcondotta.domain` and `com.jcondotta.application`. Preserve architectural boundaries: domain code stays framework-free; Spring, persistence, messaging, and transport adapters belong in infrastructure/bootstrap concerns. Follow established local naming patterns. Keep tests in the matching module/package under `src/test/java`, using `*Test` for unit tests and `*IT` for integration tests.

Do not commit secrets or local credentials. Keep environment-specific values in Spring profiles, Compose overrides, Kubernetes manifests, or Terraform variables. Use low-cardinality metric labels; put identifiers in logs or traces.

Use focused, imperative commit messages describing the change. PRs should state the behavior changed, affected areas, test evidence, linked issues, and relevant API, event, configuration, or infrastructure changes.

## Investigation And Scope

Use `AGENTS.md` files as the initial map. Do not rediscover documented structure or conventions unless guidance appears incomplete, stale, or inconsistent with the task.

For existing behavior, start at the narrowest relevant entry points and expand only as needed. Production code and tests establish current behavior; documentation does not replace verification of implementation details. Avoid broad repository scans when the map is sufficient.

Preserve unrelated user changes.
