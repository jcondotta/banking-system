package com.jcondotta.application.events.mapper;

import com.jcondotta.application.events.IntegrationEvent;
import com.jcondotta.domain.events.DomainEvent;

import java.lang.reflect.ParameterizedType;

public abstract class AbstractDomainEventMapper<E extends DomainEvent<?>, I extends IntegrationEvent<?>>
  implements DomainEventIntegrationEventMapper<E, I> {

  protected final Class<E> eventType;

  @SuppressWarnings("unchecked")
  protected AbstractDomainEventMapper() {
    ParameterizedType parameterizedType = (ParameterizedType) getClass().getGenericSuperclass();
    this.eventType = (Class<E>) parameterizedType.getActualTypeArguments()[0];
  }

  @Override
  public final Class<E> domainEventType() {
    return eventType;
  }
}