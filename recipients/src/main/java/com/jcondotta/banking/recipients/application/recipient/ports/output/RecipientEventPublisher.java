package com.jcondotta.banking.recipients.application.recipient.ports.output;

import com.jcondotta.domain.events.DomainEvent;

import java.util.List;

public interface RecipientEventPublisher {

  void publish(List<DomainEvent<?, ?>> events);
}
