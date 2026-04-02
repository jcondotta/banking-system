package com.jcondotta.application.events.mapper;

import com.jcondotta.application.events.IntegrationEvent;
import com.jcondotta.application.events.IntegrationEventMetadata;
import com.jcondotta.domain.events.DomainEvent;

public interface DomainEventIntegrationEventMapper<E extends DomainEvent<?>, I extends IntegrationEvent<?>> {

  Class<E> domainEventType();

  I map(IntegrationEventMetadata metadata, E event);
}