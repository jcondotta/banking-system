package com.jcondotta.banking.accounts.contracts.support;

import com.jcondotta.application.events.IntegrationEventMetadata;

import java.time.Instant;
import java.util.UUID;

public final class IntegrationEventMetadataFixtures {

    public static final UUID EVENT_ID =
      UUID.fromString("9f1c2a44-6b7e-4c1a-8d3e-2f7a9b6c5d01");

    public static final UUID CORRELATION_ID =
      UUID.fromString("3a6e1f90-2d4b-4f8a-9c11-7e5d2b8a4c77");

    public static final String EVENT_SOURCE = "accounts-service";

    public static final Instant OCCURRED_AT =
      Instant.parse("2026-04-08T10:00:00Z");

    public static final String OCCURRED_AT_ISO =
      OCCURRED_AT.toString();

    private IntegrationEventMetadataFixtures() {
    }

    public static IntegrationEventMetadata defaultMetadata() {
        return IntegrationEventMetadata.of(
          EVENT_ID,
          CORRELATION_ID,
          EVENT_SOURCE,
          OCCURRED_AT
        );
    }
}