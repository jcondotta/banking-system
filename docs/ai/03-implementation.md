# Implementation Guidelines

Use this stage to implement behavior and design that are already sufficiently defined.

## Goal

Implement the requested change autonomously while preserving the agreed behavior, repository architecture, and established local conventions.

## Entry Condition

For a new feature or non-trivial behavioral change, read [Design](02-design.md) and verify that its gate has passed with no OPEN decisions. An implementation request alone does not satisfy that gate. Return to Requirements or Design if their concerns remain unresolved; otherwise implement autonomously within the agreed design.

## Before Editing

Review the relevant requirements and design decisions when they are available.

Inspect only the production code, tests, configuration, and resources necessary to understand the affected implementation.

Use the local project or bounded-context playbook when the change affects multiple architectural areas.

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

Classify newly discovered decisions using [Design's resolution criteria](02-design.md#2-decision-resolution-criteria). If an item is OPEN:

- stop implementation before making further code changes for that decision;
- leave already-made changes untouched unless they prevent the project from being inspected or the user explicitly asks to revert them;
- document the newly discovered decision, alternatives, trade-offs, and recommendation;
- return to Design and ask the user to resolve it;
- resume implementation only after the Design gate has no OPEN decisions again.

Resolve purely mechanical details autonomously using local conventions.

Do not silently treat a decision made autonomously during implementation as having been agreed with the user.

If an unresolved issue makes correct implementation impossible rather than merely requiring a choice between viable alternatives, stop and explain the blocker.

## Scope Discipline

Do not perform unrelated refactors merely because nearby code could be improved.

Do not move or generalize existing abstractions unless required by the implemented behavior or agreed design.

Do not change public behavior, persistence semantics, or architectural ownership as incidental cleanup.

## Completion

After implementation:
- update or add the necessary tests
- run the focused verification appropriate to the affected areas
- proceed to validation for non-trivial changes

Do not consider passing tests alone sufficient evidence that the implementation is ready.
