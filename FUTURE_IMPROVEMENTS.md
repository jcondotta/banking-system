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

