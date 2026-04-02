package com.jcondotta.application.events;

import java.time.Instant;
import java.util.UUID;

public record IntegrationEventMetadata(
  UUID eventId,
  UUID correlationId,
  String eventSource,
  int version,
  Instant occurredAt
) {

  public static final String EVENT_ID_REQUIRED = "eventId must be provided";
  public static final String CORRELATION_ID_REQUIRED = "correlationId must be provided";
  public static final String EVENT_SOURCE_REQUIRED = "eventSource must be provided";
  public static final String OCCURRED_AT_REQUIRED = "occurredAt must be provided";
  public static final String VERSION_MUST_BE_GREATER_THAN_ZERO = "version must be greater than zero";

  public static final int DEFAULT_EVENT_VERSION = 1;

  public IntegrationEventMetadata {
    if (eventId == null) {
      throw new NullPointerException(EVENT_ID_REQUIRED);
    }
    if (correlationId == null) {
      throw new NullPointerException(CORRELATION_ID_REQUIRED);
    }
    if (eventSource == null || eventSource.isBlank()) {
      throw new IllegalArgumentException(EVENT_SOURCE_REQUIRED);
    }
    if (occurredAt == null) {
      throw new NullPointerException(OCCURRED_AT_REQUIRED);
    }
    if (version <= 0) {
      throw new IllegalArgumentException(VERSION_MUST_BE_GREATER_THAN_ZERO);
    }
  }

  public static IntegrationEventMetadata of(UUID eventId, UUID correlationId, String source, int version, Instant occurredAt) {
    return new IntegrationEventMetadata(eventId, correlationId, source, version, occurredAt);
  }

  public static IntegrationEventMetadata of(UUID eventId, UUID correlationId, String source, Instant occurredAt) {
    return IntegrationEventMetadata.of(eventId, correlationId, source, DEFAULT_EVENT_VERSION, occurredAt);
  }
}