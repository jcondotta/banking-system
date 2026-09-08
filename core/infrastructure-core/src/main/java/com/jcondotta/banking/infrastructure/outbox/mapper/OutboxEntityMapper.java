package com.jcondotta.banking.infrastructure.outbox.mapper;

import com.jcondotta.banking.infrastructure.adapters.output.messaging.EventEnvelope;
import com.jcondotta.banking.infrastructure.adapters.output.messaging.EventPublication;
import com.jcondotta.banking.infrastructure.outbox.record.OutboxRecord;
import com.jcondotta.domain.events.DomainEvent;

public interface OutboxEntityMapper<T extends OutboxRecord> {

  T toOutboxEntity(DomainEvent<?, ?> event, EventPublication<?> publication, EventEnvelope envelope);
}
