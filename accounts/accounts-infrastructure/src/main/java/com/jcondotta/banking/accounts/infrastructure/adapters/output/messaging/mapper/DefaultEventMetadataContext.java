package com.jcondotta.banking.accounts.infrastructure.adapters.output.messaging.mapper;

import java.util.UUID;

import static java.util.Objects.requireNonNull;

public record DefaultEventMetadataContext(UUID correlationId) implements EventMetadataContext {

  static final String CORRELATION_ID_REQUIRED = "correlationId must be provided";

  public DefaultEventMetadataContext {
    requireNonNull(correlationId, CORRELATION_ID_REQUIRED);
  }

  public static DefaultEventMetadataContext of(UUID correlationId) {
    return new DefaultEventMetadataContext(correlationId);
  }
}