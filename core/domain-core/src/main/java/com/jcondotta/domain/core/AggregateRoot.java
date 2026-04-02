package com.jcondotta.domain.core;

import com.jcondotta.domain.events.DomainEvent;
import com.jcondotta.domain.identity.AggregateId;

import java.util.ArrayList;
import java.util.List;

public abstract class AggregateRoot<ID extends AggregateId<?>> extends Entity<ID> {

  private final List<DomainEvent<ID>> events = new ArrayList<>();

  protected AggregateRoot(ID id) {
    super(id);
  }

  protected void registerEvent(DomainEvent<ID> event) {
    events.add(event);
  }

  public List<DomainEvent<?>> pullEvents() {
    List<DomainEvent<?>> pulledEvents = List.copyOf(events);
    events.clear();
    return pulledEvents;
  }

  public boolean hasEvents() {
    return !events.isEmpty();
  }
}