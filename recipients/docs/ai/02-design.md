# Recipients Design Guidelines

Use this stage to decide how the requested behavior should fit into the existing system before implementation begins.

## Goal

Turn the understood requirements into an agreed solution.

Design is the primary interactive stage of the development workflow.

The goal is not to minimize questions. The goal is to expose meaningful choices before implementation so that the user can participate in the design instead of discovering those choices after the code has been written.

Inspect the relevant implementation and established repository patterns before proposing architectural or behavioral choices.

## Decision Discovery

Before proposing a solution, inspect enough of the relevant code to identify the meaningful decisions exposed by the requested change.

Do not stop at the first ambiguity when additional investigation can reasonably reveal related decisions or dependencies.

Consider decisions involving:
- externally observable behavior or error semantics
- endpoint paths and API shape
- request and response contracts or public models
- domain responsibilities, invariants, and business rules
- ownership semantics
- application use-case boundaries
- persistence and data-access strategy
- command-side versus query-side access
- architectural ownership or dependency direction
- package or layer boundaries
- the responsibility or scope of existing abstractions
- whether abstractions should be reused, shared, broadened, moved, created, or remain use-case-specific
- persistence contracts or schema behavior
- security or data-exposure behavior
- concurrency or consistency guarantees
- observability behavior
- integration with existing infrastructure

Do not limit Design questions only to architectural decisions.

## Design Decisions

A choice belongs to Design when multiple reasonable alternatives are viable and choosing between them could affect behavior, contracts, responsibilities, architecture, data access, observability, or future implementation direction.

Do not silently choose between such alternatives.

Existing repository patterns should inform the alternatives and recommendation, but the existence of a technically compatible or preferred local pattern does not by itself resolve a Design decision when another reasonable alternative remains viable.

Do not treat the most conservative, conventional, secure, simple, or locally consistent option as automatically resolved when meaningful alternatives exist.

When uncertain whether a non-mechanical choice belongs to Design or Implementation, prefer surfacing it during Design.

## Interaction

When a Design decision exists:
- explain what needs to be decided
- present the viable alternatives
- explain the relevant trade-offs
- recommend an option based on the existing code, requirements, and repository conventions
- ask the user to decide or confirm the recommendation

Do not resolve an identified Design decision autonomously.

Identifying a decision and then selecting the recommended option yourself does not count as resolving the decision.

Group related decisions when that makes them easier to evaluate, but keep each decision independently understandable.

When several meaningful decisions are discovered, present all decisions that can reasonably be identified before implementation rather than asking only about the first one.

Do not enter Implementation while identified Design decisions remain unresolved.

## What Not To Ask

Do not ask about purely mechanical implementation details whose alternatives do not meaningfully affect behavior, contracts, responsibilities, architecture, data access, observability, or maintainability.

Examples include:
- local variable names
- import ordering
- formatting
- obvious framework annotations required by an already agreed design
- exact placement of trivial helper methods
- mechanical extensions of a pattern when no reasonable design alternative exists

Do not create questions merely because another implementation is theoretically possible. Alternatives should be reasonable in the context of the requested change and repository.

When uncertain whether a choice is a meaningful Design decision or a mechanical implementation detail, prefer asking during Design.

## Decision Dependencies

Resolve higher-impact decisions before decisions that depend on them.

When one choice constrains another, make that dependency explicit.

If several decisions are independent, they may be presented together so the user can resolve them in one interaction.

Do not finalize a design whose important decisions still depend on unresolved assumptions.

## Design Outcome

Once the relevant decisions are resolved, summarize the agreed solution.

The resulting design should make clear:
- agreed behavior and error semantics
- API and public contract decisions
- affected architectural areas
- ownership and responsibility decisions
- data-access and persistence decisions
- relevant observability or concurrency implications
- important edge cases
- expected testing scope

Implementation should be able to proceed autonomously from the agreed design in normal circumstances.