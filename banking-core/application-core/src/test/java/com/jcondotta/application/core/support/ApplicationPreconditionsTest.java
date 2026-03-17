package com.jcondotta.application.core;

import com.jcondotta.application.core.support.ApplicationPreconditions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ApplicationPreconditionsTest {

  private static final String MESSAGE = "error";

  @Test
  void shouldReturnValue_whenValueIsNotNull() {
    String value = "test";

    String result = ApplicationPreconditions.required(value, MESSAGE);

    assertThat(result).isEqualTo(value);
  }

  @Test
  void shouldThrowException_whenValueIsNull() {
    assertThatThrownBy(() -> ApplicationPreconditions.required(null, MESSAGE))
      .isInstanceOf(NullPointerException.class)
      .hasMessage(MESSAGE);
  }

  @Test
  void shouldReturnValue_whenStringIsNotBlank() {
    String value = "test";

    String result = ApplicationPreconditions.requiredNotBlank(value, MESSAGE);

    assertThat(result).isEqualTo(value);
  }

  @Test
  void shouldThrowException_whenStringIsBlank() {
    assertThatThrownBy(() -> ApplicationPreconditions.requiredNotBlank("   ", MESSAGE))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage(MESSAGE);
  }

  @Test
  void shouldThrowException_whenStringIsNull() {
    assertThatThrownBy(() -> ApplicationPreconditions.requiredNotBlank(null, MESSAGE))
      .isInstanceOf(NullPointerException.class)
      .hasMessage(MESSAGE);
  }
}