package com.jcondotta.banking.infrastructure.adapters.output.messaging;

import com.jcondotta.domain.events.DomainEvent;
import com.jcondotta.domain.events.DomainEventMetadata;
import com.jcondotta.domain.identity.AggregateId;
import com.jcondotta.domain.identity.EventId;

import java.time.Instant;
import java.util.UUID;

final class MessagingTestFixtures {

  private static final EventId EVENT_ID = EventId.of(UUID.fromString("90854175-5da4-4775-82a0-243a602a59df"));
  private static final TestAggregateId AGGREGATE_ID =
    new TestAggregateId(UUID.fromString("10d723ea-fe73-4d58-9ed0-97c248955496"));
  private static final Instant OCCURRED_AT = Instant.parse("2026-01-01T00:00:00Z");

  private MessagingTestFixtures() {}

  static TestDomainEvent domainEvent() {
    return new TestDomainEvent(
      DomainEventMetadata.of(EVENT_ID, AGGREGATE_ID, OCCURRED_AT),
      "test-data"
    );
  }

  record TestAggregateId(UUID value) implements AggregateId<UUID> {}

  record TestDomainEvent(DomainEventMetadata<TestAggregateId> metadata, String data)
    implements DomainEvent<TestAggregateId, String> {

    @Override
    public String eventType() {
      return "test-event";
    }
  }
}
