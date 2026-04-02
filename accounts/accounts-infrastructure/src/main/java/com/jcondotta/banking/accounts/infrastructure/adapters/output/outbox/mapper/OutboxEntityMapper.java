package com.jcondotta.banking.accounts.infrastructure.adapters.output.outbox.mapper;

import com.jcondotta.application.events.IntegrationEvent;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.outbox.entity.OutboxEntity;
import com.jcondotta.domain.identity.AggregateId;

public interface OutboxEntityMapper {

  OutboxEntity toOutboxEntity(AggregateId<?> aggregateId, IntegrationEvent<?> integrationEvent);
}