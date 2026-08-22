# Recipients Implementation Guidelines

Use this stage to implement behavior and design that are already sufficiently defined.

## Goal

Implement the requested change autonomously while preserving the agreed behavior, repository architecture, and established local conventions.

## Before Editing

Review the relevant requirements and design decisions when they are available.

Inspect only the production code, tests, configuration, and resources necessary to understand the affected implementation.

Use `docs/ai/change-playbook.md` when the change affects multiple architectural areas.

Do not reopen decisions that were already explicitly resolved by the user unless the implementation reveals a concrete contradiction or impossibility.

## Implementation

Follow the closest established local pattern when it is compatible with the agreed design.

Keep changes focused on the requested behavior.

Preserve:
- dependency direction
- framework-free domain code
- existing contracts unless intentionally changed
- established package responsibilities
- unrelated user changes

Prefer the smallest coherent change that fully implements the agreed behavior.

Do not introduce new abstractions, generalized infrastructure, or architectural movement without a concrete need from the requested change.

## Decisions Discovered During Implementation

Implementation is autonomous. Do not stop implementation merely to ask a design question.

If implementation exposes a meaningful behavioral, contract, or architectural decision that was not resolved during requirements or design:
- choose the option that best preserves existing behavior, contracts, architectural boundaries, and established repository patterns
- prefer the least disruptive and least expansive viable option when evidence does not clearly favor another choice
- avoid unnecessary architectural broadening or scope expansion
- continue the implementation
- record the decision, relevant alternatives, and rationale for validation

Do not silently treat a decision made autonomously during implementation as having been agreed with the user.

If an unresolved issue makes correct implementation impossible rather than merely requiring a choice between viable alternatives, stop and explain the blocker.

## Scope Discipline

Do not perform unrelated refactors merely because nearby code could be improved.

Do not move or generalize existing abstractions unless required by the implemented behavior or agreed design.

Do not change public behavior, persistence semantics, or architectural ownership as incidental cleanup.

Preserve unrelated user changes.

## Completion

After implementation:
- update or add the necessary tests
- run the focused verification appropriate to the affected areas
- proceed to validation for non-trivial changes

Do not consider passing tests alone sufficient evidence that the implementation is ready.