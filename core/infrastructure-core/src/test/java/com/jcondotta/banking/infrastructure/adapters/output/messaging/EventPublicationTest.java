package com.jcondotta.banking.infrastructure.adapters.output.messaging;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

class EventPublicationTest {

  private static final EventRouting ROUTING = new EventRouting("test-events", "test-key");

  @Test
  void shouldCreatePublication_whenEnvelopeAndRoutingAreProvided() {
    var envelope = mock(EventEnvelope.class);

    var publication = new EventPublication(envelope, ROUTING);

    assertThat(publication.envelope()).isSameAs(envelope);
    assertThat(publication.routing()).isSameAs(ROUTING);
  }

  @Test
  void shouldRejectNullEnvelope() {
    assertThatThrownBy(() -> new EventPublication(null, ROUTING))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("envelope must not be null");
  }

  @Test
  void shouldRejectNullRouting() {
    assertThatThrownBy(() -> new EventPublication(mock(EventEnvelope.class), null))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("routing must not be null");
  }
}
