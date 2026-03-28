package com.jcondotta.banking.accounts.infrastructure.adapters.output.messaging.outbox;

import com.jcondotta.application.core.events.IntegrationEventCollector;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.entity.OutboxEntity;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.mapper.OutboxEntityMapper;
import com.jcondotta.domain.core.AggregateRoot;
import com.jcondotta.domain.identity.AggregateId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class OutboxEventCollector {

  private final IntegrationEventCollector integrationEventCollector;
  private final OutboxEntityMapper outboxMapper;

  public List<OutboxEntity> collect(AggregateRoot<?> aggregate) {
    AggregateId<?> aggregateId = aggregate.getId();

    return integrationEventCollector.collect(aggregate.pullEvents())
      .stream()
      .map(integrationEvent -> outboxMapper.toOutboxEntity(aggregateId, integrationEvent))
      .toList();
  }
}