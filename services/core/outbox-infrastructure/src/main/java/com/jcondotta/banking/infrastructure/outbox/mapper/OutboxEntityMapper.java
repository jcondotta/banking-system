package com.jcondotta.banking.infrastructure.outbox.mapper;

import com.jcondotta.banking.infrastructure.adapters.output.messaging.EventPublication;
import com.jcondotta.banking.infrastructure.outbox.record.OutboxRecord;

public interface OutboxEntityMapper<T extends OutboxRecord> {

  T toOutboxEntity(EventPublication publication);
}
