package com.jcondotta.banking.infrastructure.adapters.output.messaging;

import java.util.UUID;

public record EventPublicationContext(UUID correlationId, String eventSource) {

  public EventPublicationContext {
    if (correlationId == null) {
      throw new IllegalArgumentException("correlation id must not be null");
    }
    if (eventSource == null || eventSource.isBlank()) {
      throw new IllegalArgumentException("event source must not be blank");
    }
  }
}
