package com.jcondotta.banking.accounts.domain.bankaccount.value_objects.address;

import com.jcondotta.banking.accounts.domain.testsupport.BlankValuesSource;
import com.jcondotta.domain.exception.DomainValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CityTest {

  private static final String VALID_CITY = "Barcelona";

  @Test
  void shouldCreateCity_whenValueIsValid() {
    var city = City.of(VALID_CITY);

    assertThat(city.value()).isEqualTo(VALID_CITY);
  }

  @Test
  void shouldCreateCity_whenValueIsAtMaxLengthBoundary() {
    var cityAtMaxLength = "a".repeat(City.MAX_LENGTH);
    var city = City.of(cityAtMaxLength);

    assertThat(city.value()).hasSize(City.MAX_LENGTH);
  }

  @Test
  void shouldThrowException_whenValueIsNull() {
    assertThatThrownBy(() -> City.of(null))
      .isInstanceOf(DomainValidationException.class)
      .hasMessage(City.MUST_NOT_BE_EMPTY);
  }

  @ParameterizedTest
  @BlankValuesSource
  void shouldThrowException_whenValueIsBlank(String blankCity) {
    assertThatThrownBy(() -> City.of(blankCity))
      .isInstanceOf(DomainValidationException.class)
      .hasMessage(City.MUST_NOT_BE_EMPTY);
  }

  @Test
  void shouldThrowException_whenValueLengthExceedsMaxLength() {
    var cityExceedingMaxLength = "a".repeat(City.MAX_LENGTH + 1);

    assertThatThrownBy(() -> City.of(cityExceedingMaxLength))
      .isInstanceOf(DomainValidationException.class)
      .hasMessage(
        City.MUST_NOT_EXCEED_LENGTH
          .formatted(City.MAX_LENGTH)
      );
  }
}