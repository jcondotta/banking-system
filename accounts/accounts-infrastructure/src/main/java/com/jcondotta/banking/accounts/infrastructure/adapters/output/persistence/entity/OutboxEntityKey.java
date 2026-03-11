package com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.entity;

import java.time.Instant;
import java.util.UUID;

public final class OutboxEntityKey {

  private static final String AGGREGATE_PK_TEMPLATE = "BANK_ACCOUNT#%s";
  private static final String OUTBOX_SK_TEMPLATE = "OUTBOX#%s";

  private static final String GSI1_PK = "OUTBOX";
  private static final String GSI1_PENDING_TEMPLATE = "PENDING#%s";
  private static final String GSI1_PROCESSED_TEMPLATE = "PROCESSED#%s";
  private static final String GSI1_PROCESSING_TEMPLATE = "PROCESSING#%s";

  private OutboxEntityKey() {
  }

  public static String partitionKey(UUID bankAccountId) {
    return AGGREGATE_PK_TEMPLATE.formatted(bankAccountId);
  }

  public static String sortKey(UUID eventId) {
    return OUTBOX_SK_TEMPLATE.formatted(eventId);
  }

  public static String gsi1PartitionKey() {
    return GSI1_PK;
  }

  public static String gsi1PendingSortKey(Instant occurredAt) {
    return GSI1_PENDING_TEMPLATE.formatted(occurredAt);
  }

  public static String gsi1ProcessingSortKey(Instant processingAt) {
    return GSI1_PROCESSING_TEMPLATE.formatted(processingAt);
  }

  public static String gsi1ProcessedSortKey(Instant processedAt) {
    return GSI1_PROCESSED_TEMPLATE.formatted(processedAt);
  }
}
