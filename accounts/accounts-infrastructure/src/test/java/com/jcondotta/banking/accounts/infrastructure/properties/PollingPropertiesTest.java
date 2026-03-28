package com.jcondotta.banking.accounts.infrastructure.properties;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PollingPropertiesTest {

  private static final Duration INTERVAL = Duration.ofSeconds(2);

  @Test
  void shouldCreatePollingProperties_whenIntervalIsPositive() {
    var polling = new PollingProperties(INTERVAL);

    assertThat(polling.interval()).isEqualTo(INTERVAL);
  }

  @Test
  void shouldThrowException_whenIntervalIsNull() {
    assertThatThrownBy(() -> new PollingProperties(null))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage(PollingProperties.INTERVAL_MUST_NOT_BE_NULL);
  }

  @Test
  void shouldThrowException_whenIntervalIsZero() {
    assertThatThrownBy(() -> new PollingProperties(Duration.ZERO))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage(PollingProperties.INTERVAL_MUST_BE_POSITIVE);
  }

  @Test
  void shouldThrowException_whenIntervalIsNegative() {
    assertThatThrownBy(() -> new PollingProperties(Duration.ofSeconds(-1)))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage(PollingProperties.INTERVAL_MUST_BE_POSITIVE);
  }
}
