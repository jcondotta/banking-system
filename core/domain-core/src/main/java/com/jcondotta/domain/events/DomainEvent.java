package com.jcondotta.domain.events;


import com.jcondotta.domain.identity.AggregateId;
import com.jcondotta.domain.identity.EventId;

import java.time.Instant;

public interface DomainEvent<A extends AggregateId<?>, D> {

  DomainEventMetadata<A> metadata();
  D data();
  String eventType();

  default EventId eventId() {
    return metadata().eventId();
  }

  default A aggregateId() {
    return metadata().aggregateId();
  }

  default Instant occurredAt() {
    return metadata().occurredAt();
  }

  default int eventVersion() {
    return 1;
  }
}
