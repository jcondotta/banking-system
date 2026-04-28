package com.jcondotta.banking.accounts.infrastructure.adapters.output.outbox.entity;

import com.jcondotta.domain.identity.AggregateId;

import java.util.Objects;
import java.util.UUID;

import static com.jcondotta.banking.accounts.infrastructure.support.Preconditions.required;
import static com.jcondotta.banking.accounts.infrastructure.support.Preconditions.requiredNotBlank;

public final class OutboxKey {

  public static final String PARTITION_KEY_TEMPLATE = "BANK_ACCOUNT#%s";
  public static final String SORT_KEY_TEMPLATE = "OUTBOX#%s";

  static final String PARTITION_KEY_REQUIRED = "partitionKey must be provided";
  static final String SORT_KEY_REQUIRED = "sortKey must be provided";

  static final String AGGREGATE_ID_REQUIRED = "aggregateId must be provided";
  static final String EVENT_ID_REQUIRED = "eventId must be provided";

  private final String partitionKey;
  private final String sortKey;

  private OutboxKey(String partitionKey, String sortKey) {
    this.partitionKey = requiredNotBlank(partitionKey, PARTITION_KEY_REQUIRED);
    this.sortKey = requiredNotBlank(sortKey, SORT_KEY_REQUIRED);
  }

  public static OutboxKey of(AggregateId<?> aggregateId, UUID eventId) {
    required(aggregateId, AGGREGATE_ID_REQUIRED);
    required(eventId, EVENT_ID_REQUIRED);

    return new OutboxKey(
      PARTITION_KEY_TEMPLATE.formatted(aggregateId.asString()),
      SORT_KEY_TEMPLATE.formatted(eventId.toString())
    );
  }

  public String partitionKey() {
    return partitionKey;
  }

  public String sortKey() {
    return sortKey;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof OutboxKey other)) return false;
    return Objects.equals(partitionKey, other.partitionKey)
      && Objects.equals(sortKey, other.sortKey);
  }

  @Override
  public int hashCode() {
    return Objects.hash(partitionKey, sortKey);
  }

  @Override
  public String toString() {
    return "OutboxKey[partitionKey=" + partitionKey + ", sortKey=" + sortKey + "]";
  }
}
