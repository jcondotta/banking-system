# Repository Guidelines

## Project Structure & Module Organization

This is the `recipients` bounded context: a Java 25, Spring Boot 4, single-module Maven service using DDD, CQRS, and hexagonal architecture by package rather than by Maven submodule.

- `src/main/java/com/jcondotta/banking/recipients/`: application entrypoint and all production code.
- `domain/`: framework-free recipient model. It owns the `Recipient` aggregate, `BankAccountId`, `RecipientId`, `RecipientName`, `Iban`, domain exceptions, failure reasons, validation constants, and repository ports.
- `application/`: use-case orchestration. It owns commands, queries, handlers, query models, logging event names/keys, failure normalization, `@Observed` instrumentation, and concurrency limits.
- `infrastructure/`: Spring and adapter code. It owns REST controllers, request/response DTOs, mappers, `ProblemDetail` exception handlers, correlation filtering, JPA entities/repositories, PostgreSQL adapters, persistence mappers, and runtime configuration.
- `src/main/resources/`: Spring `application*.yml`, Liquibase changelogs, SQL migrations, and `logback-spring.xml`.
- `src/test/java/`: unit and integration tests. Integration tests live under `.../integration` and use `@IntegrationTest`.
- `docker/`: local PostgreSQL Compose.
- `k8s/`: application and PostgreSQL manifests.

Keep dependencies pointing inward by package: infrastructure may depend on application and domain, application may depend on domain, and domain must not depend on Spring, JPA, HTTP, logging frameworks, or infrastructure code.

## Development Workflow

Development guidance lives under `docs/ai/`.

The numbered documents represent stages of the development process. They define how to reason about, design, implement, test, and validate changes.

| Stage | Purpose | Guidance |
| --- | --- | --- |
| 1. Requirements | Understand the requested behavior, scope, constraints, and open questions | `docs/ai/01-requirements.md` |
| 2. Design | Resolve behavioral, contract, and architectural decisions before implementation | `docs/ai/02-design.md` |
| 3. Implementation | Implement the agreed behavior and design autonomously | `docs/ai/03-implementation.md` |
| 4. Testing | Add or update tests according to repository testing conventions | `docs/ai/04-testing.md` |
| 5. Validation | Independently review the completed change before considering it finished | `docs/ai/05-validation.md` |

`docs/ai/change-playbook.md` provides cross-cutting guidance based on the architectural areas affected by a change.

The stages describe responsibilities, not mandatory ceremony for every task.

Start from the earliest stage that is relevant and not already resolved by the user's request or prior decisions. Do not repeat analysis or decisions that are already established.

A small or mechanical change may require little or no explicit requirements or design work. A feature or non-trivial behavioral change may require the full workflow.

When a later stage exposes a concern that belongs to an earlier stage, handle it according to the rules of the current stage rather than silently treating the earlier decision as user-approved.

Load only the guidance relevant to the current task.

## Interaction And Autonomy

Keep user interaction concentrated in the design stage.

Requirements analysis may identify open questions, ambiguities, edge cases, and decisions that need to be resolved, but should continue investigating when useful work can still be done.

Design is the interactive stage. When multiple meaningful behavioral, contract, or architectural alternatives are viable, present them to the user, recommend an option, and obtain a decision before considering the design resolved.

Implementation, testing, and validation are autonomous stages. Do not stop those stages merely to ask a question.

If implementation requires a meaningful decision that was not resolved during design, make the most conservative choice consistent with existing behavior, contracts, architecture, and repository patterns. Record the decision so it can be surfaced during validation.

Validation must complete its review before reporting questions, findings, suggestions, or decisions that may require user attention.

## Build, Test, and Development Commands

Run commands from this directory. The Maven wrapper lives one level up.

- `../mvnw clean verify`: build the service, run Surefire/Failsafe, and produce JaCoCo output.
- `../mvnw test`: run unit tests only.
- `../mvnw spring-boot:run -Dspring-boot.run.profiles=local`: start the service locally on port `8081`.
- `docker compose -f docker/docker-compose.yml up -d`: start PostgreSQL `recipients_db` on `127.0.0.1:5432`.
- `../mvnw pitest:mutationCoverage`: run PIT mutation testing for the consolidated service.

## Coding Style & Naming Conventions

Use Java conventions with 4-space indentation and packages under `com.jcondotta.banking.recipients`. Prefer records for immutable commands, queries, IDs, and DTOs.

Follow local naming patterns: `*Command`, `*CommandHandler`, `*Query`, `*QueryHandler`, `*Repository`, `*Mapper`, `*Controller`, `*ControllerImpl`, `*ExceptionHandler`, `*Properties`, `*Test`, and `*IT`.

REST paths come from `app.api.recipients.*`; API versioning uses `X-API-Version`.

Use `LogContext`, `RecipientEventType`, and `RecipientLogKey`; mask IBANs and keep IDs out of metric tags.

Preserve framework-free domain code even though all layers compile in one Maven module.

## Commit & Pull Request Guidelines

Recent commits use imperative, sentence-style messages such as `Add name filtering to list recipients API`.

PRs should include summary, affected areas, test evidence (`../mvnw verify`, targeted tests, or PIT where relevant), linked issues, and API/config notes when endpoints, profiles, Docker, Kubernetes, or observability behavior change.

## Security & Configuration Tips

Do not commit real secrets.

Compose credentials are development-only (`admin`/`password`). Production config comes from environment variables and Kubernetes manifests.

Liquibase changelogs live in `src/main/resources/db/changelog`; include SQL rollbacks.

## Agent-Specific Instructions

Use this file as the initial repository map. Do not rediscover repository structure, conventions, or architecture that are already documented here unless this guidance appears incomplete, stale, or inconsistent with the task.

For questions about existing behavior, use the documented structure to identify the narrowest relevant entry points. Inspect only the production code, tests, configuration, or resources needed to verify the answer, and expand the investigation only when necessary.

Treat production code and tests as the source of truth for current behavior. This file provides orientation and guidance; it does not replace verification when the task depends on implementation details.

Avoid broad repository scans when this guide already provides enough information to locate the relevant code.

When making architectural changes or updating repository guidance, inspect the affected controllers, handlers, repositories, configuration, resources, and tests rather than inferring architecture from package names alone.

Preserve unrelated user changes.