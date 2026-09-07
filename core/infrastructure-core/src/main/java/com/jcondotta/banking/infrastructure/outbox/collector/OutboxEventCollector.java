package com.jcondotta.banking.infrastructure.outbox.collector;

import com.jcondotta.application.events.CorrelationIdProvider;
import com.jcondotta.application.events.EventSourceProvider;
import com.jcondotta.banking.infrastructure.adapters.output.messaging.EventEnvelope;
import com.jcondotta.banking.infrastructure.adapters.output.messaging.EventPublicationContext;
import com.jcondotta.banking.infrastructure.adapters.output.messaging.EventPublicationRegistry;
import com.jcondotta.banking.infrastructure.outbox.mapper.OutboxEntityMapper;
import com.jcondotta.banking.infrastructure.outbox.record.OutboxRecord;
import com.jcondotta.domain.core.AggregateRoot;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class OutboxEventCollector<T extends OutboxRecord> {

  private final EventPublicationRegistry publicationRegistry;
  private final OutboxEntityMapper<T> outboxMapper;
  private final CorrelationIdProvider correlationIdProvider;
  private final EventSourceProvider eventSourceProvider;

  public List<T> collect(AggregateRoot<?> aggregate) {
    var domainEvents = aggregate.pullEvents();
    if (domainEvents.isEmpty()) return List.of();

    var context = new EventPublicationContext(correlationIdProvider.get(), eventSourceProvider.get());

    return domainEvents.stream()
      .map(event -> {
        var publication = publicationRegistry.publicationFor(event);
        var envelope = EventEnvelope.from(publication, context);
        return outboxMapper.toOutboxEntity(event, envelope);
      })
      .toList();
  }
}
