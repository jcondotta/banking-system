package com.jcondotta.banking.accounts.infrastructure.adapters.output.messaging.mapper;

import com.jcondotta.banking.contracts.IntegrationEvent;
import com.jcondotta.domain.events.DomainEvent;

public interface DomainEventMapper<E extends DomainEvent<?>> {

  Class<E> mappedEventType();

  IntegrationEvent<?> toIntegrationEvent(E event, EventMetadataContext context);
}