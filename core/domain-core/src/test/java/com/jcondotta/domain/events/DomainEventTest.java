package com.jcondotta.domain.events;

import com.jcondotta.domain.identity.EventId;
import com.jcondotta.domain.testsupport.FakeAggregateId;
import com.jcondotta.domain.testsupport.FakeDomainEvent;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class DomainEventTest {

  private static final EventId EVENT_ID = EventId.newId();
  private static final FakeAggregateId AGGREGATE_ID = FakeAggregateId.newId();
  private static final Instant OCCURRED_AT = Instant.now();

  private static final DomainEventMetadata<FakeAggregateId> METADATA = DomainEventMetadata.of(
    EVENT_ID,
    AGGREGATE_ID,
    OCCURRED_AT
  );

  @Test
  void shouldReturnDefaultEventVersion_whenNotOverridden() {
    DomainEvent<FakeAggregateId, FakeAggregateId> event = new FakeDomainEvent(METADATA, AGGREGATE_ID);

    assertThat(event.eventVersion()).isEqualTo(1);
  }

  @Test
  void shouldReturnEventVersion_whenVersionIsCustomized() {
    DomainEvent<FakeAggregateId, FakeAggregateId> event = new TestDomainEventWithCustomVersion(
      METADATA,
      AGGREGATE_ID
    );

    assertThat(event.eventVersion()).isEqualTo(2);
  }

  @Test
  void shouldReturnEventDataCorrectly() {
    DomainEvent<FakeAggregateId, FakeAggregateId> event = new FakeDomainEvent(METADATA, AGGREGATE_ID);

    assertThat(event.eventId()).isEqualTo(EVENT_ID);
    assertThat(event.aggregateId()).isEqualTo(AGGREGATE_ID);
    assertThat(event.data()).isEqualTo(AGGREGATE_ID);
    assertThat(event.eventType()).isEqualTo(FakeDomainEvent.EVENT_TYPE);
    assertThat(event.occurredAt()).isEqualTo(OCCURRED_AT);
  }

  private record TestDomainEventWithCustomVersion(DomainEventMetadata<FakeAggregateId> metadata, FakeAggregateId data)
    implements DomainEvent<FakeAggregateId, FakeAggregateId> {

    @Override
    public String eventType() {
      return FakeDomainEvent.EVENT_TYPE;
    }

    @Override
    public int eventVersion() {
      return 2;
    }
  }
}
