package com.jcondotta.domain.core;

import com.jcondotta.domain.testsupport.FakeAggregate;
import com.jcondotta.domain.testsupport.FakeAggregateId;
import com.jcondotta.domain.testsupport.FakeDomainEvent;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AggregateRootTest {

  @Test
  void shouldRegisterDomainEvent_whenEventIsRaised() {
    FakeAggregateId id = FakeAggregateId.newId();
    FakeAggregate aggregate = new FakeAggregate(id);

    FakeDomainEvent event = FakeDomainEvent.create(id);

    aggregate.raiseEvent(event);

    assertThat(aggregate.hasEvents()).isTrue();
  }

  @Test
  void shouldReturnRegisteredEvents_whenPullEventsIsCalled() {
    FakeAggregateId id = FakeAggregateId.newId();
    FakeAggregate aggregate = new FakeAggregate(FakeAggregateId.newId());

    FakeDomainEvent event = FakeDomainEvent.create(id);

    aggregate.raiseEvent(event);

    assertThat(aggregate.pullEvents())
      .containsExactly(event);
  }

  @Test
  void shouldClearEvents_whenPullEventsIsCalled() {

    FakeAggregateId id = FakeAggregateId.newId();
    FakeAggregate aggregate = new FakeAggregate(id);

    FakeDomainEvent event = FakeDomainEvent.create(id);

    aggregate.raiseEvent(event);

    aggregate.pullEvents();

    assertThat(aggregate.hasEvents()).isFalse();
  }

  @Test
  void shouldReturnEventsInRegistrationOrder_whenMultipleEventsAreRaised() {

    FakeAggregateId id = FakeAggregateId.newId();
    FakeAggregate aggregate = new FakeAggregate(id);

    FakeDomainEvent event1 = FakeDomainEvent.create(id);
    FakeDomainEvent event2 = FakeDomainEvent.create(id);

    aggregate.raiseEvent(event1);
    aggregate.raiseEvent(event2);

    assertThat(aggregate.pullEvents())
      .containsExactly(event1, event2);
  }

  @Test
  void shouldReturnEmpty_whenNoEventsWereRegistered() {

    FakeAggregateId id = FakeAggregateId.newId();
    FakeAggregate aggregate = new FakeAggregate(id);

    assertThat(aggregate.hasEvents()).isFalse();
    assertThat(aggregate.pullEvents()).isEmpty();
  }
}