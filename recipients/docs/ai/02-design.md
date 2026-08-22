# Recipients Design Guidelines

Use this stage to decide how the requested behavior should fit into the existing system before implementation begins.

## Goal

Turn the understood requirements into an agreed solution.

Design is the primary interactive stage of the development workflow.

Inspect the relevant implementation and established repository patterns before proposing architectural or behavioral choices.

## Design Decisions

Identify meaningful alternatives that affect:
- externally observable behavior or error semantics
- API contracts or public models
- domain responsibilities or invariants
- application use-case boundaries
- architectural ownership or dependency direction
- package or layer boundaries
- the responsibility or scope of existing abstractions
- whether abstractions should be reused, shared, moved, or remain use-case-specific
- persistence contracts or schema behavior
- security or data-exposure behavior
- concurrency or consistency guarantees
- observability behavior when it represents a meaningful design choice

Do not silently choose between materially different viable alternatives.

Existing patterns should inform the recommendation, but the existence of a technically compatible pattern does not by itself eliminate a meaningful design decision.

## Interaction

When a meaningful design decision exists:
- explain what needs to be decided
- present the viable alternatives
- explain the relevant trade-offs
- recommend an option based on the existing code and repository conventions
- ask the user to decide or confirm the recommendation

Group related decisions when that makes them easier to evaluate.

Do not ask about routine, mechanical, low-impact, or easily reversible implementation details.

Prefer asking when a choice establishes behavior, contracts, responsibilities, or architectural direction that may otherwise require meaningful rework later.

## Decision Dependencies

Resolve higher-impact decisions before decisions that depend on them.

When one choice constrains another, make that dependency explicit.

Do not finalize a design whose important decisions still depend on unresolved assumptions.

## Design Outcome

Once the relevant decisions are resolved, summarize the agreed solution.

The resulting design should make clear:
- agreed behavior and error semantics
- affected architectural areas
- important contracts
- ownership and responsibility decisions
- relevant persistence or observability implications
- important edge cases
- expected testing scope

Implementation should be able to proceed autonomously from the agreed design in normal circumstances.