# Design Guidelines

Use this interactive stage to agree on behavior and architecture before implementing a new feature or non-trivial behavioral change. Do not edit code during Design or while waiting for an answer.

## 1. Inspect And Identify Decisions

Read the relevant implementation, tests, contracts, and repository guidance. Identify what the user's request already fixes and what remains open.

Consider these areas when applicable:

- behavior, error semantics, and edge cases
- API paths, methods, headers, and request/response models
- domain behavior, invariants, and application/use-case responsibilities
- repository strategy and persistence semantics
- abstraction reuse or creation, packages, and architectural boundaries
- security, ownership, and data exposure
- concurrency and consistency
- observability
- testing implications and scope

Create an explicit decision list before proposing an implementation. For each item, record:

- the concrete question and status: OPEN or RESOLVED, or not applicable with a reason
- materially different alternatives
- evidence, constraints, risks, and trade-offs
- the recommendation and its rationale
- the resolution and its source, when resolved

Do not choose an alternative while building the list.

## 2. Decision Resolution Criteria

Record a decision as OPEN when two or more viable alternatives affect behavior, public contract, responsibilities, abstractions, architecture, security/data exposure, persistence semantics, consistency, observability, or test scope.

An existing pattern, similar endpoint, convention, or preference for safety, conservatism, simplicity, idiomatic style, testability, or smaller changes is evidence for a recommendation. None substitutes for the user's decision.

A decision may be RESOLVED without asking only when:

- the user's request explicitly selects the behavior or design;
- an earlier decision in the same task logically determines it;
- a documented public contract or hard technical constraint leaves no viable alternative; or
- the item is purely mechanical and affects none of the areas above, such as formatting, import ordering, or local variable names.

Do not ask about purely mechanical details. For meaningful decisions, prefer asking a question that later proves unnecessary over silently choosing an alternative.

## 3. Resolve Decisions With The User

For every OPEN decision, explain the question, alternatives, and important trade-offs; recommend an option with reasons; and ask the user to choose or confirm. Do not resolve it autonomously.

Resolve prerequisites first. After each answer, update the list and re-evaluate dependent decisions. An answer resolves another item only when it logically determines that choice. Add newly discovered decisions and resolve them before implementation.

If the user's choice conflicts with repository architecture, established behavior, or another resolved decision, explain the conflict and let the user decide how to proceed.

## 4. Design Gate

Implementation MUST NOT begin while any identified Design decision is OPEN.

Before leaving Design, explicitly verify the decision list against this checklist. Each area must either be RESOLVED using the criteria above or be shown not applicable with a reason. An unresolved applicable area remains OPEN:

- [ ] behavior, error semantics, and important edge cases
- [ ] API and request/response contracts
- [ ] domain/application responsibilities
- [ ] repository strategy and persistence semantics
- [ ] abstractions and architectural boundaries
- [ ] security, ownership, and data exposure
- [ ] concurrency and consistency
- [ ] observability
- [ ] testing implications and expected scope

The list records decisions and their sources; this gate checks that no applicable area or OPEN decision was missed.

## 5. Design Outcome

Summarize the agreed design using the resolved decision list or a concise account of each resolution and its source. Cover the applicable checklist areas, including persistence implications and expected testing scope.

Do not imply that a recommendation was user-approved unless the user explicitly accepted it. Only after the gate passes and the summary is established may [Implementation](03-implementation.md) proceed autonomously for the agreed design.
