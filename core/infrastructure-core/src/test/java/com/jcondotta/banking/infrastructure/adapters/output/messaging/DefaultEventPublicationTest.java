package com.jcondotta.banking.infrastructure.adapters.output.messaging;

import org.junit.jupiter.api.Test;

import static com.jcondotta.banking.infrastructure.adapters.output.messaging.MessagingTestFixtures.domainEvent;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DefaultEventPublicationTest {

  private static final String DESTINATION = "test-events";
  private static final String MESSAGE_KEY = "208ff308-a695-48e5-87d8-99f5da6b57ac";

  @Test
  void shouldCreatePublication() {
    var event = domainEvent();

    var publication = new DefaultEventPublication<>(event, DESTINATION, MESSAGE_KEY);

    assertThat(publication.event()).isSameAs(event);
    assertThat(publication.destination()).isEqualTo(DESTINATION);
    assertThat(publication.key()).isEqualTo(MESSAGE_KEY);
  }

  @Test
  void shouldRejectNullEvent() {
    assertThatThrownBy(() -> new DefaultEventPublication<>(null, DESTINATION, MESSAGE_KEY))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("event must not be null");
  }

  @Test
  void shouldRejectBlankDestination() {
    assertThatThrownBy(() -> new DefaultEventPublication<>(domainEvent(), " ", MESSAGE_KEY))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("destination must not be blank");
  }

  @Test
  void shouldRejectBlankKey() {
    assertThatThrownBy(() -> new DefaultEventPublication<>(domainEvent(), DESTINATION, " "))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("key must not be blank");
  }
}
