package com.jcondotta.banking.accounts.domain.bankaccount.value_objects.address;

import com.jcondotta.banking.accounts.domain.bankaccount.arguments_provider.BlankValuesArgumentProvider;
import com.jcondotta.domain.exception.DomainValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class StreetNumberTest {

  private static final String VALID_STREET_NUMBER = "123A";

  @Test
  void shouldCreateStreetNumber_whenValueIsValid() {
    var streetNumber = StreetNumber.of(VALID_STREET_NUMBER);

    assertThat(streetNumber.value()).isEqualTo(VALID_STREET_NUMBER);
  }

  @Test
  void shouldCreateStreetNumber_whenValueIsAtMaxLengthBoundary() {
    var streetNumberAtMaxLength = "1".repeat(StreetNumber.MAX_LENGTH);

    var streetNumber = StreetNumber.of(streetNumberAtMaxLength);

    assertThat(streetNumber.value()).hasSize(StreetNumber.MAX_LENGTH);
  }

  @Test
  void shouldThrowException_whenValueIsNull() {
    assertThatThrownBy(() -> StreetNumber.of(null))
      .isInstanceOf(DomainValidationException.class)
      .hasMessage(StreetNumber.MUST_NOT_BE_EMPTY);
  }

  @ParameterizedTest
  @ArgumentsSource(BlankValuesArgumentProvider.class)
  void shouldThrowException_whenValueIsBlank(String blankStreetNumber) {
    assertThatThrownBy(() -> StreetNumber.of(blankStreetNumber))
      .isInstanceOf(DomainValidationException.class)
      .hasMessage(StreetNumber.MUST_NOT_BE_EMPTY);
  }

  @Test
  void shouldThrowException_whenValueLengthExceedsMaxLength() {
    var streetNumberExceedingMaxLength = "1".repeat(StreetNumber.MAX_LENGTH + 1);

    assertThatThrownBy(() -> StreetNumber.of(streetNumberExceedingMaxLength))
      .isInstanceOf(DomainValidationException.class)
      .hasMessage(
        StreetNumber.MUST_NOT_EXCEED_LENGTH
          .formatted(StreetNumber.MAX_LENGTH)
      );
  }
}