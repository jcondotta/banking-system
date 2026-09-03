package com.jcondotta.banking.infrastructure.adapters.output.messaging;

import com.jcondotta.domain.events.DomainEvent;

public interface EventPublicationFactory<E extends DomainEvent<?, ?>> {

  Class<E> domainEventType();

  EventPublication<E> create(E event);
}
