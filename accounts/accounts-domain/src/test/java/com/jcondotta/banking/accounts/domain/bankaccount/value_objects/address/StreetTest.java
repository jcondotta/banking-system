package com.jcondotta.banking.accounts.domain.bankaccount.value_objects.address;

import com.jcondotta.banking.accounts.domain.testsupport.BlankValuesSource;
import com.jcondotta.domain.exception.DomainValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class StreetTest {

  private static final String VALID_STREET = "Gran Via";

  @Test
  void shouldCreateStreet_whenValueIsValid() {
    var street = Street.of(VALID_STREET);

    assertThat(street.value()).isEqualTo(VALID_STREET);
  }

  @Test
  void shouldCreateStreet_whenValueIsAtMaxLengthBoundary() {
    var streetAtMaxLength = "a".repeat(Street.MAX_LENGTH);
    var street = Street.of(streetAtMaxLength);

    assertThat(street.value()).hasSize(Street.MAX_LENGTH);
  }

  @Test
  void shouldThrowException_whenValueIsNull() {
    assertThatThrownBy(() -> Street.of(null))
      .isInstanceOf(DomainValidationException.class)
      .hasMessage(Street.MUST_NOT_BE_EMPTY);
  }

  @ParameterizedTest
  @BlankValuesSource
  void shouldThrowException_whenValueIsBlank(String blankStreet) {
    assertThatThrownBy(() -> Street.of(blankStreet))
      .isInstanceOf(DomainValidationException.class)
      .hasMessage(Street.MUST_NOT_BE_EMPTY);
  }

  @Test
  void shouldThrowException_whenValueLengthExceedsMaxLength() {
    var streetValueExceedingMaxLength = "a".repeat(Street.MAX_LENGTH + 1);

    assertThatThrownBy(() -> Street.of(streetValueExceedingMaxLength))
      .isInstanceOf(DomainValidationException.class)
      .hasMessage(
        Street.MUST_NOT_EXCEED_LENGTH.formatted(Street.MAX_LENGTH)
      );
  }
}