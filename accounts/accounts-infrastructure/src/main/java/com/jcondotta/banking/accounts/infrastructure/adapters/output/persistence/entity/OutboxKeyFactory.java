package com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.entity;

import java.time.Instant;
import java.util.UUID;

public final class OutboxKeyFactory {

  private static final String AGGREGATE_PK = "BANK_ACCOUNT#%s";
  private static final String OUTBOX_SK = "OUTBOX#%s";

  private static final String GSI_PK = "OUTBOX";

  private static final String PENDING = "PENDING#%s";
  private static final String PROCESSING = "PROCESSING#%s";
  private static final String PROCESSED = "PROCESSED#%s";

  private OutboxKeyFactory() {
  }

  public static OutboxKey pending(UUID bankAccountId, UUID eventId, Instant occurredAt) {
    return new OutboxKey(
      AGGREGATE_PK.formatted(bankAccountId),
      OUTBOX_SK.formatted(eventId),
      GSI_PK,
      PENDING.formatted(occurredAt)
    );
  }

  public static String processingSortKey(Instant processingAt) {
    return PROCESSING.formatted(processingAt);
  }

  public static String processedSortKey(Instant processedAt) {
    return PROCESSED.formatted(processedAt);
  }

  public static String gsiPartitionKey() {
    return GSI_PK;
  }
}