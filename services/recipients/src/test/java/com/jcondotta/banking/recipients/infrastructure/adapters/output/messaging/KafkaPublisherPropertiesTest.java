package com.jcondotta.banking.recipients.infrastructure.adapters.output.messaging;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class KafkaPublisherPropertiesTest {

  @Test
  void shouldCreateProperties_whenPublishTimeoutIsPositive() {
    var publishTimeout = Duration.ofSeconds(5);

    var properties = new KafkaPublisherProperties(publishTimeout);

    assertThat(properties.publishTimeout()).isEqualTo(publishTimeout);
  }

  @Test
  void shouldRejectNullPublishTimeout() {
    assertThatThrownBy(() -> new KafkaPublisherProperties(null))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("Kafka publish timeout must be greater than zero");
  }

  @Test
  void shouldRejectZeroPublishTimeout() {
    assertThatThrownBy(() -> new KafkaPublisherProperties(Duration.ZERO))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("Kafka publish timeout must be greater than zero");
  }

  @Test
  void shouldRejectNegativePublishTimeout() {
    assertThatThrownBy(() -> new KafkaPublisherProperties(Duration.ofSeconds(-1)))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("Kafka publish timeout must be greater than zero");
  }
}
