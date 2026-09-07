package com.jcondotta.banking.infrastructure.adapters.output.messaging;

import java.time.Instant;
import java.util.UUID;

public record EventEnvelope(
  String eventId,
  UUID correlationId,
  String aggregateId,
  String messageKey,
  String eventType,
  String destination,
  String eventSource,
  Instant occurredAt,
  int eventVersion,
  Object data
) {

  public static EventEnvelope from(EventPublication<?> publication, EventPublicationContext context) {
    var event = publication.event();

    return new EventEnvelope(
      event.eventId().value().toString(),
      context.correlationId(),
      event.aggregateId().asString(),
      publication.key(),
      event.eventType(),
      publication.destination(),
      context.eventSource(),
      event.occurredAt(),
      event.eventVersion(),
      event.data()
    );
  }
}
