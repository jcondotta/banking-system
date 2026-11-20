package com.jcondotta.banking.infrastructure.adapters.output.messaging;

import com.jcondotta.domain.events.DomainEvent;

public interface EventRoutingResolver<E extends DomainEvent<?, ?>> {

  Class<E> domainEventType();

  EventRouting resolve(E event);
}
