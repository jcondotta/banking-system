package com.jcondotta.banking.accounts.infrastructure.properties;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OutboxProcessingPropertiesTest {

  private static final Duration TIMEOUT = Duration.ofSeconds(10);

  @Test
  void shouldCreateProcessingProperties_whenTimeoutIsPositive() {
    var processing = new OutboxProcessingProperties(TIMEOUT);

    assertThat(processing.timeout()).isEqualTo(TIMEOUT);
  }

  @Test
  void shouldThrowException_whenTimeoutIsNull() {
    assertThatThrownBy(() -> new OutboxProcessingProperties(null))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage(OutboxProcessingProperties.TIMEOUT_MUST_NOT_BE_NULL);
  }

  @Test
  void shouldThrowException_whenTimeoutIsZero() {
    assertThatThrownBy(() -> new OutboxProcessingProperties(Duration.ZERO))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage(OutboxProcessingProperties.TIMEOUT_MUST_BE_POSITIVE);
  }

  @Test
  void shouldThrowException_whenTimeoutIsNegative() {
    assertThatThrownBy(() -> new OutboxProcessingProperties(Duration.ofSeconds(-1)))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage(OutboxProcessingProperties.TIMEOUT_MUST_BE_POSITIVE);
  }
}
