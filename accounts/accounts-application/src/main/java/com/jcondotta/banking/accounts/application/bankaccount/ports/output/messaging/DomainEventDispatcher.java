package com.jcondotta.banking.accounts.application.bankaccount.ports.output.messaging;

import com.jcondotta.domain.events.DomainEvent;

public interface DomainEventDispatcher {

  void publish(DomainEvent event);
}
