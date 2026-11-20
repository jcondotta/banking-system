package com.jcondotta.banking.infrastructure.adapters.output.messaging;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EventPublicationContextTest {

  private static final UUID CORRELATION_ID = UUID.fromString("ce75acbd-da91-4aca-ad03-e1fbb11429b6");
  private static final String EVENT_SOURCE = "test-service";

  @Test
  void shouldCreatePublicationContext() {
    var context = new EventPublicationContext(CORRELATION_ID, EVENT_SOURCE);

    assertThat(context.correlationId()).isEqualTo(CORRELATION_ID);
    assertThat(context.eventSource()).isEqualTo(EVENT_SOURCE);
  }

  @Test
  void shouldRejectNullCorrelationId() {
    assertThatThrownBy(() -> new EventPublicationContext(null, EVENT_SOURCE))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("correlation id must not be null");
  }

  @Test
  void shouldRejectNullEventSource() {
    assertThatThrownBy(() -> new EventPublicationContext(CORRELATION_ID, null))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("event source must not be blank");
  }

  @Test
  void shouldRejectBlankEventSource() {
    assertThatThrownBy(() -> new EventPublicationContext(CORRELATION_ID, " "))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("event source must not be blank");
  }
}
