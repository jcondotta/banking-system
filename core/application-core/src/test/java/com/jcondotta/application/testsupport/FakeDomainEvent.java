package com.jcondotta.application.testsupport;

import com.jcondotta.domain.events.DomainEvent;
import com.jcondotta.domain.identity.EventId;

import java.time.Instant;

public record FakeDomainEvent(EventId eventId, FakeAggregateId aggregateId, Instant occurredAt)
  implements DomainEvent<FakeAggregateId> {

  public static FakeDomainEvent newEvent(FakeAggregateId aggregateId) {
    return new FakeDomainEvent(EventId.newId(), aggregateId, Instant.now());
  }
}