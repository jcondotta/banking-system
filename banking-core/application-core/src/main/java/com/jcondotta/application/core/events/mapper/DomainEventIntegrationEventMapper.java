package com.jcondotta.application.core.events.mapper;

import com.jcondotta.application.core.events.IntegrationEvent;
import com.jcondotta.application.core.events.IntegrationEventMetadata;
import com.jcondotta.domain.events.DomainEvent;

public interface DomainEventIntegrationEventMapper<E extends DomainEvent<?>, I extends IntegrationEvent<?>> {

  Class<E> domainEventType();

  I map(IntegrationEventMetadata metadata, E event);
}