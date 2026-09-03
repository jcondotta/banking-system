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
  void shouldCreatePublication_whenFactoryIsRegistered() {
    var registry = EventPublicationRegistryFactory.of(domainEventFactory());
    var event = domainEvent();

    var publication = registry.publicationFor(event);

    assertThat(publication).isInstanceOf(DefaultEventPublication.class);
    assertThat(publication.event()).isSameAs(event);
    assertThat(publication.destination()).isEqualTo(DESTINATION);
    assertThat(publication.key()).isEqualTo(MESSAGE_KEY);
  }

  @Test
  void shouldThrowException_whenFactoryIsMissing() {
    var registry = EventPublicationRegistryFactory.of();

    assertThatThrownBy(() -> registry.publicationFor(domainEvent()))
      .isInstanceOf(IllegalStateException.class)
      .hasMessageContaining("No EventPublication factory registered");
  }

  @Test
  void shouldRejectNullEvent() {
    var registry = EventPublicationRegistryFactory.of();

    assertThatThrownBy(() -> registry.publicationFor(null))
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

  private static EventPublicationFactory<TestDomainEvent> domainEventFactory() {
    return new EventPublicationFactory<>() {
      @Override
      public Class<TestDomainEvent> domainEventType() {
        return TestDomainEvent.class;
      }

      @Override
      public EventPublication<TestDomainEvent> create(TestDomainEvent event) {
        return new DefaultEventPublication<>(event, DESTINATION, MESSAGE_KEY);
      }
    };
  }
}
