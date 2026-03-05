package com.jcondotta.domain.testsupport;

import com.jcondotta.domain.core.AggregateRoot;
import com.jcondotta.domain.events.DomainEvent;

public class FakeAggregate extends AggregateRoot<FakeAggregateId> {

  public FakeAggregate(FakeAggregateId id) {
    super(id);
  }

  public void raiseEvent(DomainEvent<FakeAggregateId> event) {
    registerEvent(event);
  }
}