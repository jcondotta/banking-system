package com.jcondotta.banking.infrastructure.adapters.output.messaging.outbox;

import com.jcondotta.banking.infrastructure.outbox.record.OutboxRecord;

public interface OutboxEventPublisher<T extends OutboxRecord> {

  void send(T outboxRecord);
}
