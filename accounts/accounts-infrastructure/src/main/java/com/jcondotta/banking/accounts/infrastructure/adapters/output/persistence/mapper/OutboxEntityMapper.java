package com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.mapper;

import com.jcondotta.application.core.events.IntegrationEvent;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.entity.OutboxEntity;
import com.jcondotta.domain.identity.AggregateId;

public interface OutboxEntityMapper {

  OutboxEntity toOutboxEntity(AggregateId<?> aggregateId, IntegrationEvent<?> integrationEvent);
}