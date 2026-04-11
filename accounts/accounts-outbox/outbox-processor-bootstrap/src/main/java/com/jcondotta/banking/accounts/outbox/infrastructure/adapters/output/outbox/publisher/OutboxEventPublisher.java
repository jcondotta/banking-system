package com.jcondotta.banking.accounts.outbox.infrastructure.adapters.output.outbox.publisher;

import com.jcondotta.banking.accounts.infrastructure.adapters.output.outbox.entity.OutboxEntity;

public interface OutboxEventPublisher {

  void send(OutboxEntity outboxEntity);
}
