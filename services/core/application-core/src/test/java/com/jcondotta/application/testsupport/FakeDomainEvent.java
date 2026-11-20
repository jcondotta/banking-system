package com.jcondotta.application.testsupport;

import com.jcondotta.domain.events.DomainEvent;
import com.jcondotta.domain.events.DomainEventMetadata;

import java.time.Instant;

public record FakeDomainEvent(DomainEventMetadata<FakeAggregateId> metadata, FakeAggregateId data)
  implements DomainEvent<FakeAggregateId, FakeAggregateId> {

  public static final String EVENT_TYPE = "fake-domain-event";

  public static FakeDomainEvent newEvent(FakeAggregateId aggregateId) {
    return new FakeDomainEvent(DomainEventMetadata.of(aggregateId, Instant.now()), aggregateId);
  }

  @Override
  public String eventType() {
    return EVENT_TYPE;
  }
}
