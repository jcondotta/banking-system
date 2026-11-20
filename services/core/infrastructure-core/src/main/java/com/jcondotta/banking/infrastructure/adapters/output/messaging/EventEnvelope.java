package com.jcondotta.banking.infrastructure.adapters.output.messaging;

import com.jcondotta.domain.events.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record EventEnvelope(
  String eventId,
  UUID correlationId,
  String aggregateId,
  String eventType,
  String eventSource,
  Instant occurredAt,
  int eventVersion,
  Object data
) {

  public static EventEnvelope from(DomainEvent<?, ?> event, EventPublicationContext context) {
    return new EventEnvelope(
      event.eventId().value().toString(),
      context.correlationId(),
      event.aggregateId().asString(),
      event.eventType(),
      context.eventSource(),
      event.occurredAt(),
      event.eventVersion(),
      event.data()
    );
  }

}
