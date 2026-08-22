# Recipients Requirements Guidelines

Use this stage to understand what should be built before deciding how it should be implemented.

## Goal

Establish the requested behavior, scope, constraints, and relevant open questions.

Requirements analysis should discover what needs to be decided. It should not prematurely turn implementation choices into requirements.

## Analysis

Inspect the relevant existing behavior before concluding what the requested change requires.

Identify, when relevant:
- expected user-visible behavior
- success and failure scenarios
- scope boundaries
- edge cases
- compatibility with existing behavior
- API contract implications
- persistence implications
- security or data-exposure concerns
- observability expectations
- concurrency implications
- assumptions that are not explicit in the request

Use existing production code and tests to understand current behavior.

Do not infer new requirements solely from what would be convenient to implement.

## Open Questions

Actively identify ambiguities or missing decisions that could materially affect the resulting behavior or design.

Examples include:
- behavior for missing or conflicting resources
- ownership or authorization semantics
- whether existing behavior must remain backward compatible
- whether an operation is scoped by another resource
- whether returned data should match an existing public representation
- edge cases that would materially affect the contract

Do not stop analysis at the first open question if useful investigation can continue.

Collect the relevant open questions and carry them into the design stage.

Do not ask about routine implementation details.

## Output

Requirements analysis should make clear:
- what behavior is already determined
- what constraints already exist
- what remains ambiguous
- which decisions need to be resolved during design

Do not silently resolve meaningful behavioral or product ambiguity merely to produce a complete-looking requirement.