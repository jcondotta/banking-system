package com.jcondotta.banking.accounts.infrastructure.adapters.output.outbox.entity;

import java.time.Instant;

import static com.jcondotta.banking.accounts.infrastructure.Preconditions.required;
import static com.jcondotta.banking.accounts.infrastructure.Preconditions.requiredNotBlank;

public record OutboxStatusKey(String partitionKey, String sortKey) {

  static final String PARTITION_KEY_TEMPLATE = "OUTBOX#%d";
  static final String SORT_KEY_PENDING_TEMPLATE = "PENDING#%s";
  static final String SORT_KEY_PROCESSING_TEMPLATE = "PROCESSING#%s";
  static final String SORT_KEY_PUBLISHED_TEMPLATE = "PUBLISHED#%s";
  static final String SORT_KEY_FAILED_TEMPLATE = "FAILED#%s";

  static final String PARTITION_KEY_REQUIRED = "partitionKey must be provided";
  static final String SORT_KEY_REQUIRED = "sortKey must be provided";

  static final String AGGREGATE_ID_REQUIRED = "aggregateId must be provided";
  static final String CREATED_AT_REQUIRED = "createdAt must be provided";

  public OutboxStatusKey {
    requiredNotBlank(partitionKey, PARTITION_KEY_REQUIRED);
    requiredNotBlank(sortKey, SORT_KEY_REQUIRED);
  }

  public static OutboxStatusKey pending(String aggregateId, Instant createdAt) {
    requiredNotBlank(aggregateId, AGGREGATE_ID_REQUIRED);
    required(createdAt, CREATED_AT_REQUIRED);

    return new OutboxStatusKey(
      buildPartitionKey(aggregateId),
      buildSortKey(OutboxStatus.PENDING, createdAt)
    );
  }

  public static String buildPartitionKey(String aggregateId) {
    requiredNotBlank(aggregateId, AGGREGATE_ID_REQUIRED);
    return PARTITION_KEY_TEMPLATE.formatted(shardOf(aggregateId));
  }

  public static String buildSortKey(OutboxStatus status, Instant now) {
    return switch (status) {
      case PENDING -> SORT_KEY_PENDING_TEMPLATE.formatted(now);
      case PROCESSING -> SORT_KEY_PROCESSING_TEMPLATE.formatted(now);
      case PUBLISHED -> SORT_KEY_PUBLISHED_TEMPLATE.formatted(now);
      case FAILED -> SORT_KEY_FAILED_TEMPLATE.formatted(now);
    };
  }

  private static int shardOf(String aggregateId) {
    return Math.abs(aggregateId.hashCode()) % OutboxConstants.OUTBOX_TOTAL_SHARDS;
  }
}