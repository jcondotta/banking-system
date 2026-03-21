package com.jcondotta.application.core.support;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ApplicationPreconditionsTest {

  private static final String ERROR_MESSAGE = "error";
  private static final String CURRENT_VALUE = "current value";

  @Test
  void shouldReturnValue_whenRequiredValueIsNotNull() {
    String result = ApplicationPreconditions.required(CURRENT_VALUE, ERROR_MESSAGE);

    assertThat(result).isEqualTo(CURRENT_VALUE);
  }

  @Test
  @SuppressWarnings("ConstantConditions")
  void shouldThrowNullPointerException_whenRequiredValueIsNull() {
    assertThatThrownBy(() -> ApplicationPreconditions.required(null, ERROR_MESSAGE))
      .isInstanceOf(NullPointerException.class)
      .hasMessage(ERROR_MESSAGE);
  }

  @Test
  void shouldReturnValue_whenRequiredNotBlankValueIsNotBlank() {
    String result = ApplicationPreconditions.requiredNotBlank(CURRENT_VALUE, ERROR_MESSAGE);

    assertThat(result).isEqualTo(CURRENT_VALUE);
  }

  @Test
  void shouldThrowIllegalArgumentException_whenRequiredNotBlankValueIsBlank() {
    assertThatThrownBy(() -> ApplicationPreconditions.requiredNotBlank("   ", ERROR_MESSAGE))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage(ERROR_MESSAGE);
  }

  @Test
  void shouldThrowNullPointerException_whenRequiredNotBlankValueIsNull() {
    assertThatThrownBy(() -> ApplicationPreconditions.requiredNotBlank(null, ERROR_MESSAGE))
      .isInstanceOf(NullPointerException.class)
      .hasMessage(ERROR_MESSAGE);
  }

  @Test
  void shouldThrowUnsupportedOperationException_whenInstantiatedViaReflection() throws Exception {
    Constructor<ApplicationPreconditions> constructor = ApplicationPreconditions.class.getDeclaredConstructor();

    constructor.setAccessible(true);

    assertThatThrownBy(constructor::newInstance)
      .hasCauseInstanceOf(UnsupportedOperationException.class)
      .hasRootCauseMessage("No instances allowed for this class");
  }
}