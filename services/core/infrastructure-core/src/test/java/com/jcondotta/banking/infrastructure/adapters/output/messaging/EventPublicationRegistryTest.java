package com.jcondotta.banking.infrastructure.adapters.output.messaging;

import com.jcondotta.banking.infrastructure.adapters.output.messaging.exceptions.DuplicateEventPublicationFactoryException;
import org.junit.jupiter.api.Test;

import static com.jcondotta.banking.infrastructure.adapters.output.messaging.MessagingTestFixtures.TestDomainEvent;
import static com.jcondotta.banking.infrastructure.adapters.output.messaging.MessagingTestFixtures.domainEvent;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EventPublicationRegistryTest {

  private static final String DESTINATION = "test-events";
  private static final String MESSAGE_KEY = "test-key";

  @Test
  void shouldReturnRouting_whenFactoryIsRegistered() {
    var registry = EventPublicationRegistryFactory.of(domainEventFactory());
    var event = domainEvent();

    var routing = registry.routingFor(event);

    assertThat(routing).isInstanceOf(EventRouting.class);
    assertThat(routing.destination()).isEqualTo(DESTINATION);
    assertThat(routing.key()).isEqualTo(MESSAGE_KEY);
  }

  @Test
  void shouldThrowException_whenFactoryIsMissing() {
    var registry = EventPublicationRegistryFactory.of();

    assertThatThrownBy(() -> registry.routingFor(domainEvent()))
      .isInstanceOf(IllegalStateException.class)
      .hasMessageContaining("No EventPublication factory registered");
  }

  @Test
  void shouldRejectNullEvent() {
    var registry = EventPublicationRegistryFactory.of();

    assertThatThrownBy(() -> registry.routingFor(null))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("event must not be null");
  }

  @Test
  void shouldThrowException_whenFactoryIsDuplicated() {
    var factory = domainEventFactory();

    assertThatThrownBy(() -> EventPublicationRegistryFactory.of(factory, factory))
      .isInstanceOf(DuplicateEventPublicationFactoryException.class)
      .hasMessageContaining("Multiple EventPublication factories registered");
  }

  private static EventRoutingResolver<TestDomainEvent> domainEventFactory() {
    return new EventRoutingResolver<>() {
      @Override
      public Class<TestDomainEvent> domainEventType() {
        return TestDomainEvent.class;
      }

      @Override
      public EventRouting resolve(TestDomainEvent event) {
        return new EventRouting(DESTINATION, MESSAGE_KEY);
      }
    };
  }
}
