# Repository Guidelines

## Project Structure & Module Organization

This is a Java 25, Spring Boot 4, Maven banking system. The root `pom.xml` builds `core`, `accounts`, and `recipients`; `transfers` has its own Maven tree but is not listed in the root modules.

- `core/`: shared `domain-core`, `application-core`, and `infrastructure-core` modules.
- `accounts/`: bounded context implemented as a flat single Maven module. Its API and transactional-outbox worker run in the same Spring Boot application.
- `recipients/`: bounded context implemented as a flat single Maven module, organized by package into domain, application, infrastructure, and bootstrap/runtime concerns.
- `transfers/`: bounded context with its own Maven tree outside the root reactor.
- `src/main/java` and `src/test/java`: production and test code inside each module or bounded context.
- `src/main/resources`: Spring configuration, Liquibase changelogs, logging config, and other runtime resources.
- `docker/`, `k8s/`, `terraform/`: local infrastructure, Kubernetes manifests, and cloud infrastructure where present.

## Development Workflow

Shared development workflow guidance lives under `docs/ai/`.

The numbered documents represent stages of the development process. They define how to reason about, design, implement, test, and validate changes.

| Stage | Purpose | Guidance |
| --- | --- | --- |
| 1. Requirements | Understand the requested behavior, scope, constraints, and open questions | `docs/ai/01-requirements.md` |
| 2. Design | Resolve behavioral, contract, and architectural decisions before implementation | `docs/ai/02-design.md` |
| 3. Implementation | Implement autonomously after relevant requirements and design decisions are resolved | `docs/ai/03-implementation.md` |
| 4. Testing | Add or update tests according to repository testing conventions | `docs/ai/04-testing.md` |
| 5. Validation | Independently review the completed change before considering it finished | `docs/ai/05-validation.md` |

Bounded contexts may provide local guidance under their own `docs/ai/` directory. When shared workflow guidance and local bounded-context guidance both apply, prefer the more specific local guidance for paths, commands, fixtures, contracts, and bounded-context behavior.

The stages describe responsibilities, not mandatory ceremony for every task.

Start from the earliest unresolved stage relevant to the task.

A request to implement, build, add, create, change, or fix something does not by itself mean that requirements or design have already been resolved.

Before entering implementation for a new feature or non-trivial behavioral change, determine whether the relevant requirements and design are sufficiently defined.

If meaningful behavioral, contract, or architectural decisions remain unresolved, perform the relevant requirements and design stages before implementation.

Skip an earlier stage only when its concerns are already resolved by the user's request, prior explicit decisions in the current task, or a hard technical/contractual constraint that leaves no viable alternative.

Repository guidance, established local patterns, similar existing endpoints, technical preference, conservatism, simplicity, safety, or idiomatic style do not resolve feature-level decisions by themselves. Use that evidence to recommend an option during Design, not to replace the user's decision.

Do not repeat analysis or decisions that are already established.

A small or mechanical change may require little or no explicit requirements or design work. A change is not "clearly established" merely because a nearby feature does something similar when the new change exposes behavior, public contract, responsibility, abstraction, architecture, security/data exposure, or observability choices.

When a later stage exposes a concern that belongs to an earlier stage, handle it according to the rules of the current stage rather than silently treating the earlier decision as user-approved.

Load only the guidance relevant to the current task.

## Interaction And Autonomy

Keep user interaction concentrated in the design stage.

Requirements analysis may identify open questions, ambiguities, edge cases, and decisions that need to be resolved. Continue investigating while useful analysis can still be performed, and carry meaningful unresolved questions into design.

Design is the interactive stage. When multiple meaningful behavioral, contract, or architectural alternatives are viable, present them to the user, explain the relevant trade-offs, recommend an option, and obtain a decision before considering the design resolved.

Do not begin implementation merely because the user explicitly asked to implement something. For a new feature or non-trivial behavioral change, unresolved design decisions must first pass through the design stage.

Before editing code for a new feature or non-trivial behavioral change, the Design gate in `docs/ai/02-design.md` must have no OPEN decisions. If working from a bounded context, use the relative path from that context to the shared design document.

Once implementation begins, implementation, testing, and validation are autonomous stages for the resolved design. Do not stop those stages merely to ask about already-resolved decisions or purely mechanical details.

If implementation discovers a meaningful behavioral, public-contract, responsibility, abstraction, architecture, security/data-exposure, or observability decision that was not identified or resolved during Design, stop implementation and return to Design before making further code changes for that decision. Do not use implementation-time discovery as a way to bypass the Design gate.

Only purely mechanical implementation details remain autonomous after Design, such as local variable names, import ordering, helper method extraction with no architectural effect, and formatting.

Do not treat a decision made autonomously during implementation as though it had been agreed with the user.

Validation must complete its review before reporting questions, findings, suggestions, or decisions that may require user attention.

## Build, Test, and Development Commands

- `./mvnw clean verify`: build root modules, run unit and integration tests, and generate JaCoCo reports.
- `./mvnw test`: run unit tests through Surefire.
- `./mvnw -pl recipients -am spring-boot:run -Dspring-boot.run.profiles=local`: run the recipients service locally with required upstream modules.
- `cd recipients && docker compose -f docker/docker-compose.yml up -d`: start recipients local dependencies.
- `./mvnw -pl accounts -am spring-boot:run -Dspring-boot.run.profiles=local`: run the accounts API and outbox worker locally.
- `./mvnw -pl accounts -am pitest:mutationCoverage`: run PIT mutation testing for accounts.

## Coding Style & Naming Conventions

Use Java conventions with 4-space indentation, descriptive class names, and packages under `com.jcondotta.banking`. Preserve architecture boundaries: domain code must stay framework-free; Spring, persistence, messaging, and HTTP adapters belong in infrastructure/bootstrap concerns. Follow existing naming patterns such as `*CommandHandler`, `*Repository`, `*Config`, `*Test`, and `*IT`.

## Testing Guidelines

Tests use JUnit 5, Mockito, Rest Assured, ArchUnit, Instancio, Testcontainers, JaCoCo, and PIT. Keep unit tests beside the module or package they verify under `src/test/java`. Name unit tests `*Test` and integration tests `*IT` so Surefire and Failsafe pick them up correctly. Add focused domain tests for business rules and integration tests for REST, persistence, messaging, and containerized dependencies.

## Commit & Pull Request Guidelines

Recent commits use imperative, sentence-style messages such as `Add name filtering to list recipients API`. Keep commits focused and describe the behavioral change. Pull requests should include a short summary, affected modules or areas, test evidence (`./mvnw clean verify`, targeted tests, or PIT where relevant), linked issues, and API/configuration notes when endpoints, events, profiles, or infrastructure files change.

## Security & Configuration Tips

Do not commit secrets or local credentials. Keep environment-specific values in Spring profiles, Docker Compose overrides, Kubernetes manifests, or Terraform variables. Prefer low-cardinality metrics labels; put identifiers such as account IDs, recipient IDs, and correlation IDs in logs or traces.

## Agent-Specific Instructions

Use `AGENTS.md` files as the initial repository map. Do not rediscover repository structure, conventions, or architecture that are already documented unless this guidance appears incomplete, stale, or inconsistent with the task.

For questions about existing behavior, use the documented structure to identify the narrowest relevant entry points. Inspect only the production code, tests, configuration, or resources needed to verify the answer, and expand the investigation only when necessary.

Treat production code and tests as the source of truth for current behavior. These files provide orientation and guidance; they do not replace verification when the task depends on implementation details.

Avoid broad repository scans when the guidance already provides enough information to locate the relevant code.

Preserve unrelated user changes.
