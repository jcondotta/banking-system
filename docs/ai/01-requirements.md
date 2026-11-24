# Requirements Guidelines

Use this stage to understand what should be built before deciding how it should be implemented.

## Goal

Establish the requested behavior, scope, constraints, and relevant open questions.

Requirements produces evidence and open questions for [Design](02-design.md); it does not approve a design or turn implementation choices into requirements.

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

Record existing patterns as evidence. Use the Decision Resolution Criteria in [Design](02-design.md#2-decision-resolution-criteria) to distinguish determined behavior from choices requiring the user.

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

For each potential decision, note the affected behavioral, contract, responsibility, architectural, security, persistence, consistency, observability, or test-scope concerns. Classify meaningful alternatives using Design's criteria; do not resolve them during Requirements based on a preferred pattern.

Do not ask about routine implementation details.

## Output

Requirements analysis should make clear:
- what behavior is already determined
- what constraints already exist
- what remains ambiguous
- which decisions need to be resolved during design
- which existing patterns are evidence for recommendations rather than decisions

Do not silently resolve meaningful behavioral or product ambiguity merely to produce a complete-looking requirement.
