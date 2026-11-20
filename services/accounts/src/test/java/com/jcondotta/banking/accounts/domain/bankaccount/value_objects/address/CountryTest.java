package com.jcondotta.banking.accounts.domain.bankaccount.value_objects.address;

import com.jcondotta.banking.accounts.domain.testsupport.BlankValuesSource;
import com.jcondotta.domain.exception.DomainValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CountryTest {

  @ParameterizedTest
  @ValueSource(strings = {"ES", "DE", "US"})
  void shouldCreateCountry_whenIsoCodeIsValid(String isoCode) {
    var country = Country.of(isoCode);

    assertThat(country.isoCode()).isEqualTo(isoCode);
  }

  @ParameterizedTest
  @BlankValuesSource
  void shouldThrowException_whenIsoCodeIsBlank(String isoCode) {
    assertThatThrownBy(() -> Country.of(isoCode))
      .isInstanceOf(DomainValidationException.class)
      .hasMessage(Country.ISO_CODE_MUST_BE_PROVIDED);
  }

  @Test
  void shouldThrowException_whenIsoCodeIsNull() {
    assertThatThrownBy(() -> Country.of(null))
      .isInstanceOf(DomainValidationException.class)
      .hasMessage(Country.ISO_CODE_MUST_BE_PROVIDED);
  }

  @ParameterizedTest
  @ValueSource(strings = {"es", "ZZ", "E", "ESP", " ES "})
  void shouldThrowException_whenIsoCodeIsInvalid(String isoCode) {
    assertThatThrownBy(() -> Country.of(isoCode))
      .isInstanceOf(DomainValidationException.class)
      .hasMessage(Country.ISO_CODE_MUST_BE_VALID);
  }
}
