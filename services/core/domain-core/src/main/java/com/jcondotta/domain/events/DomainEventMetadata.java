package com.jcondotta.domain.events;

import com.jcondotta.domain.identity.AggregateId;
import com.jcondotta.domain.identity.EventId;
import com.jcondotta.domain.validation.DomainEventErrors;

import java.time.Instant;

import static com.jcondotta.domain.support.Preconditions.required;

public record DomainEventMetadata<A extends AggregateId<?>>(EventId eventId, A aggregateId, Instant occurredAt) {

  public DomainEventMetadata {
    required(eventId, DomainEventErrors.EVENT_ID_MUST_BE_PROVIDED);
    required(aggregateId, DomainEventErrors.AGGREGATE_ID_MUST_BE_PROVIDED);
    required(occurredAt, DomainEventErrors.EVENT_OCCURRED_AT_MUST_BE_PROVIDED);
  }

  public static <A extends AggregateId<?>> DomainEventMetadata<A> of(A aggregateId, Instant occurredAt) {
    return new DomainEventMetadata<>(EventId.newId(), aggregateId, occurredAt);
  }

  public static <A extends AggregateId<?>> DomainEventMetadata<A> of(EventId eventId, A aggregateId, Instant occurredAt) {
    return new DomainEventMetadata<>(eventId, aggregateId, occurredAt);
  }
}
