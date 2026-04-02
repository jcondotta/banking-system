package com.jcondotta.domain.events;


import com.jcondotta.domain.identity.AggregateId;
import com.jcondotta.domain.identity.EventId;

import java.time.Instant;

public interface DomainEvent<A extends AggregateId<?>> {

  EventId eventId();
  A aggregateId();
  Instant occurredAt();

  default int version() {
    return 1;
  }
}