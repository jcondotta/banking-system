package com.jcondotta.banking.infrastructure.adapters.input.messaging;

import java.time.Instant;
import java.util.UUID;

public record EventMessage<D>(
    String eventId,
    UUID correlationId,
    String aggregateId,
    String eventType,
    String eventSource,
    Instant occurredAt,
    int eventVersion,
    D data
) {}
