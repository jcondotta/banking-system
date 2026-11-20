package com.jcondotta.banking.infrastructure.adapters.output.messaging;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EventRoutingTest {

  private static final String DESTINATION = "test-events";
  private static final String MESSAGE_KEY = "208ff308-a695-48e5-87d8-99f5da6b57ac";

  @Test
  void shouldCreateRouting() {
    var routing = new EventRouting(DESTINATION, MESSAGE_KEY);

    assertThat(routing.destination()).isEqualTo(DESTINATION);
    assertThat(routing.key()).isEqualTo(MESSAGE_KEY);
  }

  @Test
  void shouldRejectBlankDestination() {
    assertThatThrownBy(() -> new EventRouting(" ", MESSAGE_KEY))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("destination must not be blank");
  }

  @Test
  void shouldRejectNullDestination() {
    assertThatThrownBy(() -> new EventRouting(null, MESSAGE_KEY))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("destination must not be blank");
  }

  @Test
  void shouldRejectBlankKey() {
    assertThatThrownBy(() -> new EventRouting(DESTINATION, " "))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("key must not be blank");
  }

  @Test
  void shouldRejectNullKey() {
    assertThatThrownBy(() -> new EventRouting(DESTINATION, null))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("key must not be blank");
  }
}
