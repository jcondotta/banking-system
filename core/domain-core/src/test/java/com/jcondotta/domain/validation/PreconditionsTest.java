package com.jcondotta.domain.validation;

import com.jcondotta.domain.exception.DomainValidationException;
import com.jcondotta.domain.exception.InvalidDomainDataException;
import com.jcondotta.domain.support.Preconditions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PreconditionsTest {

  private static final String ERROR_MESSAGE = "error";
  private static final String VALID_VALUE = "valid";

  @Test
  void shouldReturnValue_whenRequiredValueIsNotNull() {
    String result = Preconditions.required(VALID_VALUE, ERROR_MESSAGE);

    assertThat(result).isEqualTo(VALID_VALUE);
  }

  @Test
  void shouldThrowDomainValidationException_whenRequiredValueIsNull() {
    assertThatThrownBy(() -> Preconditions.required(null, ERROR_MESSAGE))
      .isInstanceOf(InvalidDomainDataException.class)
      .isInstanceOf(DomainValidationException.class)
      .hasMessage(ERROR_MESSAGE);
  }

  @Test
  void shouldReturnValue_whenRequiredNotBlankValueIsValid() {
    String result = Preconditions.requiredNotBlank(VALID_VALUE, ERROR_MESSAGE);

    assertThat(result).isEqualTo(VALID_VALUE);
  }

  @Test
  @SuppressWarnings("ConstantConditions")
  void shouldThrowDomainValidationException_whenRequiredNotBlankValueIsNull() {
    assertThatThrownBy(() -> Preconditions.requiredNotBlank(null, ERROR_MESSAGE))
      .isInstanceOf(InvalidDomainDataException.class)
      .isInstanceOf(DomainValidationException.class)
      .hasMessage(ERROR_MESSAGE);
  }

  @Test
  void shouldThrowDomainValidationException_whenRequiredNotBlankValueIsBlank() {
    assertThatThrownBy(() -> Preconditions.requiredNotBlank("   ", ERROR_MESSAGE))
      .isInstanceOf(InvalidDomainDataException.class)
      .isInstanceOf(DomainValidationException.class)
      .hasMessage(ERROR_MESSAGE);
  }

  @Test
  void shouldPassSilently_whenCheckArgumentConditionIsTrue() {
    assertThatCode(() -> Preconditions.checkArgument(true, ERROR_MESSAGE))
      .doesNotThrowAnyException();
  }

  @Test
  void shouldThrowDomainValidationException_whenCheckArgumentConditionIsFalse() {
    assertThatThrownBy(() -> Preconditions.checkArgument(false, ERROR_MESSAGE))
      .isInstanceOf(InvalidDomainDataException.class)
      .isInstanceOf(DomainValidationException.class)
      .hasMessage(ERROR_MESSAGE);
  }

  @Test
  void shouldThrowUnsupportedOperationException_whenInstantiatingUtilityClass() throws Exception {
    Constructor<Preconditions> constructor = Preconditions.class.getDeclaredConstructor();
    constructor.setAccessible(true);

    assertThatThrownBy(constructor::newInstance)
      .hasCauseInstanceOf(UnsupportedOperationException.class)
      .hasRootCauseMessage("No instances allowed for this class");
  }
}
