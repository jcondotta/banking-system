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

---

## accounts — IBAN lookup via DynamoDB GSI

**Context:** The current `BankAccountDynamoDbRepository.findByIban()` implementation performs a full table scan as a provisional measure because no index exists for the `iban` field.

**Problem:** A scan reads every item in the table. As the number of accounts grows, this becomes a latency and cost problem. The implementation also returns the first item found regardless of IBAN match, so it only works correctly when the table has exactly one account or the caller handles mismatches upstream.

**Solution:** Add a Global Secondary Index (GSI) with `iban` as the partition key to the `bank-accounts` DynamoDB table. Update `BankAccountDynamoDbRepository.findByIban()` to query the GSI directly, turning the lookup into an O(1) operation.

**Steps:**
1. Add a GSI to the LocalStack table creation script (`docker/localstack/01-create-bank-accounts-table.sh`) using `--global-secondary-indexes`.
2. Annotate `BankingEntity.getIban()` with `@DynamoDbSecondaryPartitionKey(indexNames = "gsi-iban")` so the Enhanced Client maps it automatically.
3. Inject the GSI table reference in `BankAccountDynamoDbRepository` via `DynamoDbEnhancedClient.table(...).index("gsi-iban")`.
4. Replace the scan in `findByIban()` with a `query()` on the GSI using `QueryConditional.keyEqualTo()`.
5. Add the same GSI to the production Terraform/CloudFormation definition.
