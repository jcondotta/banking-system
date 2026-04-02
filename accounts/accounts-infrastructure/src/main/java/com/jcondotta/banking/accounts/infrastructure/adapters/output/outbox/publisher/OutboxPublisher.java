package com.jcondotta.banking.accounts.infrastructure.adapters.output.outbox.publisher;

import com.jcondotta.banking.accounts.infrastructure.adapters.output.outbox.entity.OutboxEntity;

public interface OutboxPublisher {

  void publish(OutboxEntity outboxEntity);
}
