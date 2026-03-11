package com.jcondotta.banking.accounts.infrastructure.adapters.output.messaging.outbox;

import com.jcondotta.banking.accounts.infrastructure.adapters.CorrelationIdProvider;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.messaging.mapper.DefaultEventMetadataContext;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.messaging.mapper.DomainEventToIntegrationEventResolver;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.entity.OutboxEntity;
import com.jcondotta.domain.core.AggregateRoot;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class OutboxEventCollector {

  private final DomainEventToIntegrationEventResolver domainEventToIntegrationEventMapper;
  private final OutboxEntityMapper outboxMapper;
  private final CorrelationIdProvider correlationIdProvider;

  public List<OutboxEntity> collect(AggregateRoot<?> aggregate) {
    Objects.requireNonNull(aggregate, "aggregate must not be null");

    var aggregateId = aggregate.getId();
    var eventMetadataContext = DefaultEventMetadataContext.of(
      correlationIdProvider.get()
    );

    return aggregate.pullEvents()
      .stream()
      .map(event -> {
        var integrationEvent = domainEventToIntegrationEventMapper.toIntegrationEvent(event, eventMetadataContext);

        return outboxMapper.toOutboxEntity(aggregateId, integrationEvent);
      })
      .toList();
  }
}