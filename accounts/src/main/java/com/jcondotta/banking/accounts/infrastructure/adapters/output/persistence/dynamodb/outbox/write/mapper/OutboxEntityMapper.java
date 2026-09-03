package com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.dynamodb.outbox.write.mapper;

import com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.dynamodb.outbox.entity.OutboxEntity;
import com.jcondotta.banking.infrastructure.adapters.output.messaging.EventEnvelope;
import com.jcondotta.domain.events.DomainEvent;

public interface OutboxEntityMapper {

  OutboxEntity toOutboxEntity(DomainEvent<?, ?> event, EventEnvelope envelope);
}
