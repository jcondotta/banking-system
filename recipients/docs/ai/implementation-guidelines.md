# Recipients Implementation Guidelines

Use these guidelines when implementing changes in the `recipients` bounded context.

Prefer the smallest change that satisfies the requested behavior while preserving established architectural boundaries and local patterns.

## Before Editing

Before the first edit, inspect the relevant implementation and determine whether the requested change exposes any meaningful architectural or behavioral decisions.

A decision is considered resolved when it is clearly determined by:
- the user's request;
- repository guidance;
- an established local pattern;
- or existing behavior that the change is expected to preserve.

Do not rediscover unrelated parts of the repository. Inspect only what is necessary to understand the requested change and its immediate architectural boundaries.

## Decision Checkpoints

Do not silently choose between materially different alternatives when a meaningful architectural or behavioral decision remains unresolved.

When such a decision exists:
- explain what needs to be decided;
- present the viable alternatives;
- recommend an option and explain why;
- wait for the user's decision before implementing that part.

Treat changes to existing architectural boundaries as meaningful decisions. In particular, stop and ask before:
- moving an existing abstraction to a different package or layer;
- broadening an abstraction that currently belongs to one use case so it can be shared by another;
- introducing a new shared abstraction instead of keeping use-case-specific boundaries;
- changing ownership or dependency direction between packages or layers.

Do not ask for confirmation about routine, mechanical, low-impact, or clearly established implementation choices.

## During Implementation

After all known decision points are resolved, continue implementation autonomously.

If a new meaningful unresolved decision emerges while implementing, stop before making that decision and consult the user.

Do not reinterpret previously resolved decisions unless new evidence from the code makes the chosen approach inconsistent or unsafe.

Preserve unrelated user changes.