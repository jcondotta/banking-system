# Future Improvements

## accounts — Outbox dead-letter handling

**Context:** `OutboxEventStoreImpl.deadLetterEvent()` is called when an event exceeds `processing.max-retries`. Currently it silently deletes the item from DynamoDB.

**Problem:** Failed events are permanently lost with no audit trail, no alerting, and no way to inspect or replay them. In production this means data loss without any visibility.

**Solution:** Instead of deleting, move the event to a dead-letter store before discarding it. Options:
- Write to a separate DynamoDB table (e.g. `outbox-dead-letter`) for inspection and manual replay.
- Publish to a dead-letter Kafka topic so downstream teams can react.
- At minimum, emit a structured `ERROR` log with the full event payload so it can be recovered from log aggregation.

**Steps:**
1. Decide on the dead-letter destination (DynamoDB table vs Kafka topic vs both).
2. Add a `deadLetterEvent` write before the delete in `OutboxEventStoreImpl`.
3. Add a metric/alert on dead-letter events so on-call is notified when events start failing.
4. Consider a replay mechanism (manual or automated) to reprocess dead-lettered events after the root cause is fixed.

## Test suite — Spring context reuse and outbox isolation

**Context:** Accounts integration tests normally disable the outbox worker. `BankAccountActivatedOutboxIT` enables it, creating a different Spring context against the same DynamoDB table. Keeping that context cached left its worker running and deleting events expected by subsequent tests. `@DirtiesContext(classMode = AFTER_CLASS)` now closes that context and shuts down its worker after the class finishes. The other integration tests still reuse their compatible context; contexts are not recreated for every class.

**Problem:** Separate context configurations and context rebuilding add startup cost to the test suite. Improve context reuse within each bounded context to make the full suite faster while preserving deterministic assertions and real persistence and messaging coverage.

**Proposed approach:** Evaluate a common integration-test configuration with explicit control over background processing. Most tests could share a context with automatic polling stopped and invoke the real outbox dispatcher when publication is required. Keep coverage of automatic polling and worker startup/shutdown, either through a controlled lifecycle in the shared context or a small dedicated test group. The lifecycle mechanism and resulting context strategy still need design and measurement.

**Steps:**
1. Measure suite duration, context creation counts, context startup time, and container startup time to establish a baseline.
2. Review profiles, `@TestPropertySource`, and bean overrides that create distinct context configurations; consolidate compatible configurations within each module.
3. Evaluate explicit dispatch versus controlled worker start/stop for outbox tests. Ensure processing is stopped and in-flight tasks have completed before another test inspects the shared table.
4. Preserve database cleanup and event isolation. Context reuse alone does not isolate shared data, and parallel execution requires a separate isolation strategy.
5. Retain the current `@DirtiesContext` fix until its replacement passes repeated runs with varied test order, including the worker test before the HTTP tests.
6. Compare before/after timings under equivalent conditions and apply the proven approach to other modules where it reduces startup cost.

**Acceptance:** All accounts unit and integration tests pass together, including real Kafka publication and outbox cleanup. Tests remain independent of execution order, no worker leaks into later tests, and measurements demonstrate fewer context creations and a shorter suite runtime without reducing coverage.

