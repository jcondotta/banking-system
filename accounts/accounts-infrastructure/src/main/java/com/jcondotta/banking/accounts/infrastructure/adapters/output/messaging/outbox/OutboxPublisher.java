package com.jcondotta.banking.accounts.infrastructure.adapters.output.messaging.outbox;

import com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.entity.OutboxEntity;

public interface OutboxPublisher {

  void publish(OutboxEntity outboxEntity);
}
