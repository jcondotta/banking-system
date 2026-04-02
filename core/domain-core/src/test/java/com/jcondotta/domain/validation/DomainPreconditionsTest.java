package com.jcondotta.domain.validation;

import com.jcondotta.domain.exception.DomainValidationException;
import com.jcondotta.domain.support.DomainPreconditions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DomainPreconditionsTest {

  private static final String ERROR_MESSAGE = "error";
  private static final String VALID_VALUE = "valid";

  @Test
  void shouldReturnValue_whenRequiredValueIsNotNull() {
    String result = DomainPreconditions.required(VALID_VALUE, ERROR_MESSAGE);

    assertThat(result).isEqualTo(VALID_VALUE);
  }

  @Test
  void shouldThrowDomainValidationException_whenRequiredValueIsNull() {
    assertThatThrownBy(() -> DomainPreconditions.required(null, ERROR_MESSAGE))
      .isInstanceOf(DomainValidationException.class)
      .hasMessage(ERROR_MESSAGE);
  }

  @Test
  void shouldReturnValue_whenRequiredNotBlankValueIsValid() {
    String result = DomainPreconditions.requiredNotBlank(VALID_VALUE, ERROR_MESSAGE);

    assertThat(result).isEqualTo(VALID_VALUE);
  }

  @Test
  @SuppressWarnings("ConstantConditions")
  void shouldThrowDomainValidationException_whenRequiredNotBlankValueIsNull() {
    assertThatThrownBy(() -> DomainPreconditions.requiredNotBlank(null, ERROR_MESSAGE))
      .isInstanceOf(DomainValidationException.class)
      .hasMessage(ERROR_MESSAGE);
  }

  @Test
  void shouldThrowDomainValidationException_whenRequiredNotBlankValueIsBlank() {
    assertThatThrownBy(() -> DomainPreconditions.requiredNotBlank("   ", ERROR_MESSAGE))
      .isInstanceOf(DomainValidationException.class)
      .hasMessage(ERROR_MESSAGE);
  }

  @Test
  void shouldThrowUnsupportedOperationException_whenInstantiatingUtilityClass() throws Exception {
    Constructor<DomainPreconditions> constructor = DomainPreconditions.class.getDeclaredConstructor();

    constructor.setAccessible(true);

    assertThatThrownBy(constructor::newInstance)
      .hasCauseInstanceOf(UnsupportedOperationException.class)
      .hasRootCauseMessage("No instances allowed for this class");
  }
}