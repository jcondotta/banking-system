package com.jcondotta.domain.events;

import com.jcondotta.domain.identity.AggregateId;
import com.jcondotta.domain.identity.EventId;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class DomainEventTest {

  private record FakeEntityId(UUID value) implements AggregateId<UUID> {}

  private record FakeDomainEvent(
    EventId eventId,
    FakeEntityId aggregateId,
    Instant occurredAt
  ) implements DomainEvent<FakeEntityId> {}

  @Test
  void shouldExposeAllDomainEventProperties() {

    EventId eventId = EventId.newId();
    FakeEntityId aggregateId = new FakeEntityId(UUID.randomUUID());
    Instant occurredAt = Instant.now();

    DomainEvent<FakeEntityId> event = new FakeDomainEvent(eventId, aggregateId, occurredAt);

    assertThat(event.eventId()).isEqualTo(eventId);
    assertThat(event.aggregateId()).isEqualTo(aggregateId);
    assertThat(event.occurredAt()).isEqualTo(occurredAt);
  }
}