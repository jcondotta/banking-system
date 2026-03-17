package com.jcondotta.domain.events;

import com.jcondotta.domain.identity.AggregateId;
import com.jcondotta.domain.identity.EventId;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class DomainEventTest {

  private static final EventId EVENT_ID = EventId.newId();
  private static final TestAggregateId AGGREGATE_ID = new TestAggregateId(UUID.randomUUID());
  private static final Instant OCCURRED_AT = Instant.now();

  @Test
  void shouldReturnDefaultVersion_whenNotOverridden() {
    DomainEvent<TestAggregateId> event = new TestDomainEvent(EVENT_ID, AGGREGATE_ID, OCCURRED_AT);

    assertThat(event.version()).isEqualTo(1);
  }

  @Test
  void shouldAllowOverridingVersion_whenImplemented() {
    DomainEvent<TestAggregateId> event = new TestDomainEventWithCustomVersion(EVENT_ID, AGGREGATE_ID, OCCURRED_AT);

    assertThat(event.version()).isEqualTo(2);
  }

  @Test
  void shouldReturnEventDataCorrectly() {
    DomainEvent<TestAggregateId> event = new TestDomainEvent(EVENT_ID, AGGREGATE_ID, OCCURRED_AT);

    assertThat(event.eventId()).isEqualTo(EVENT_ID);
    assertThat(event.aggregateId()).isEqualTo(AGGREGATE_ID);
    assertThat(event.occurredAt()).isEqualTo(OCCURRED_AT);
  }

  private record TestAggregateId(UUID value) implements AggregateId<UUID> {}

  private record TestDomainEvent(EventId eventId, TestAggregateId aggregateId, Instant occurredAt)
    implements DomainEvent<TestAggregateId> {}

  private record TestDomainEventWithCustomVersion(EventId eventId, TestAggregateId aggregateId, Instant occurredAt)
    implements DomainEvent<TestAggregateId> {

    @Override
    public int version() {
      return 2;
    }
  }
}