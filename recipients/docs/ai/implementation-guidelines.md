## Decision Checkpoints

Before the first edit, inspect the relevant implementation and identify
whether the task exposes meaningful architectural or behavioral decisions.

A decision requires user confirmation when multiple materially different
implementations are viable and the choice would affect:
- observable API behavior or error semantics;
- ownership or dependency direction;
- the responsibility or scope of an existing abstraction;
- package or layer boundaries;
- reuse of a use-case-specific abstraction by another use case.

Do not treat the existence of a technically compatible local pattern as
sufficient justification to silently resolve one of these decisions.

When such a decision exists:
- explain the decision;
- present the viable alternatives;
- recommend an option;
- wait for the user's decision before editing code affected by that choice.

Do not ask about routine, mechanical, reversible, or clearly established
implementation choices.

After a decision is resolved, continue autonomously unless another
checkpoint is encountered.