package com.jcondotta.banking.accounts.infrastructure.adapters.output.outbox.entity;

import static com.jcondotta.banking.accounts.infrastructure.Preconditions.required;
import static com.jcondotta.banking.accounts.infrastructure.Preconditions.requiredNotBlank;

public record OutboxStatusQueryKey(String partitionKey, String sortKey) {

  static final String PARTITION_KEY_TEMPLATE = "OUTBOX#%d";

  public static final String SORT_KEY_PENDING_PREFIX = "PENDING#";
  public static final String SORT_KEY_PROCESSING_PREFIX = "PROCESSING#";
  public static final String SORT_KEY_PUBLISHED_PREFIX = "PUBLISHED#";
  public static final String SORT_KEY_FAILED_PREFIX = "FAILED#";

  static final String STATUS_REQUIRED = "status must be provided";
  static final String PARTITION_KEY_REQUIRED = "partitionKey must be provided";
  static final String SORT_KEY_REQUIRED = "sortKey must be provided";

  public OutboxStatusQueryKey {
    requiredNotBlank(partitionKey, PARTITION_KEY_REQUIRED);
    requiredNotBlank(sortKey, SORT_KEY_REQUIRED);
  }

  public static OutboxStatusQueryKey of(int shard, OutboxStatus status) {
    required(status, STATUS_REQUIRED);

    return new OutboxStatusQueryKey(
      buildPartitionKey(shard),
      buildSortKey(status)
    );
  }

  public static String buildPartitionKey(int shard) {
    return PARTITION_KEY_TEMPLATE.formatted(shard);
  }

  public static String buildSortKey(OutboxStatus status) {
    required(status, STATUS_REQUIRED);

    return switch (status) {
      case PENDING -> SORT_KEY_PENDING_PREFIX;
      case PROCESSING -> SORT_KEY_PROCESSING_PREFIX;
      case PUBLISHED -> SORT_KEY_PUBLISHED_PREFIX;
      case FAILED -> SORT_KEY_FAILED_PREFIX;
    };
  }
}