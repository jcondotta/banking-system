package com.jcondotta.banking.accounts.infrastructure.adapters.output.outbox.entity;

import static com.jcondotta.banking.accounts.infrastructure.Preconditions.requiredNotBlank;

public record OutboxKey(String partitionKey, String sortKey) {

  public static final String PARTITION_KEY_TEMPLATE = "BANK_ACCOUNT#%s";
  public static final String SORT_KEY_TEMPLATE = "OUTBOX#%s";

  static final String AGGREGATE_ID_REQUIRED = "aggregateId must be provided";
  static final String EVENT_ID_REQUIRED = "eventId must be provided";

  static final String PARTITION_KEY_REQUIRED = "partitionKey must be provided";
  static final String SORT_KEY_REQUIRED = "sortKey must be provided";

  public OutboxKey {
    requiredNotBlank(partitionKey, PARTITION_KEY_REQUIRED);
    requiredNotBlank(sortKey, SORT_KEY_REQUIRED);
  }

  public static OutboxKey of(String aggregateId, String eventId) {
    requiredNotBlank(aggregateId, AGGREGATE_ID_REQUIRED);
    requiredNotBlank(eventId, EVENT_ID_REQUIRED);

    return new OutboxKey(
      PARTITION_KEY_TEMPLATE.formatted(aggregateId),
      SORT_KEY_TEMPLATE.formatted(eventId)
    );
  }
}