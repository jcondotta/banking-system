package com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.dynamodb.outbox.write.collector;

import com.jcondotta.application.events.CorrelationIdProvider;
import com.jcondotta.application.events.EventSourceProvider;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.dynamodb.outbox.entity.OutboxEntity;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.dynamodb.outbox.write.mapper.OutboxEntityMapper;
import com.jcondotta.banking.infrastructure.adapters.output.messaging.EventEnvelope;
import com.jcondotta.banking.infrastructure.adapters.output.messaging.EventPublicationContext;
import com.jcondotta.banking.infrastructure.adapters.output.messaging.EventPublicationRegistry;
import com.jcondotta.domain.core.AggregateRoot;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OutboxEventCollector {

  private final EventPublicationRegistry publicationRegistry;
  private final OutboxEntityMapper outboxMapper;
  private final CorrelationIdProvider correlationIdProvider;
  private final EventSourceProvider eventSourceProvider;

  public List<OutboxEntity> collect(AggregateRoot<?> aggregate) {
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
