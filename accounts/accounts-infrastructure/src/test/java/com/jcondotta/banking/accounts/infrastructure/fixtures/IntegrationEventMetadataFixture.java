package com.jcondotta.banking.accounts.infrastructure.fixtures;

import com.jcondotta.application.core.events.IntegrationEventMetadata;

import java.time.Instant;
import java.util.UUID;

public class IntegrationEventMetadataFixture {

  public static final UUID DEFAULT_EVENT_ID = UUID.fromString("1091df64-40ea-427b-b570-587372241ce8");
  public static final UUID DEFAULT_CORRELATION_ID = UUID.fromString("f7be51e6-0a9d-4789-91e2-bab5e7800247");
  public static final String DEFAULT_EVENT_SOURCE = "bank-accounts-service";
  public static final int DEFAULT_VERSION = IntegrationEventMetadata.DEFAULT_EVENT_VERSION;
  public static final Instant DEFAULT_OCCURRED_AT = Instant.parse("2026-03-18T19:57:51.533493Z");

  public static IntegrationEventMetadata create() {
    return IntegrationEventMetadata.of(
      DEFAULT_EVENT_ID,
      DEFAULT_CORRELATION_ID,
      DEFAULT_EVENT_SOURCE,
      DEFAULT_VERSION,
      DEFAULT_OCCURRED_AT
    );
  }

  public static IntegrationEventMetadata create(UUID eventId) {
    return IntegrationEventMetadata.of(
      eventId,
      DEFAULT_CORRELATION_ID,
      DEFAULT_EVENT_SOURCE,
      DEFAULT_VERSION,
      DEFAULT_OCCURRED_AT
    );
  }

  public static IntegrationEventMetadata create(UUID eventId, UUID correlationId) {
    return IntegrationEventMetadata.of(
      eventId,
      correlationId,
      DEFAULT_EVENT_SOURCE,
      DEFAULT_VERSION,
      DEFAULT_OCCURRED_AT
    );
  }

  public static IntegrationEventMetadata create(UUID eventId, UUID correlationId, String eventSource) {
    return IntegrationEventMetadata.of(
      eventId,
      correlationId,
      eventSource,
      DEFAULT_VERSION,
      DEFAULT_OCCURRED_AT
    );
  }

  public static IntegrationEventMetadata create(UUID eventId, UUID correlationId, String eventSource, int version, Instant occurredAt) {
    return IntegrationEventMetadata.of(
      eventId,
      correlationId,
      eventSource,
      version,
      occurredAt
    );
  }
}