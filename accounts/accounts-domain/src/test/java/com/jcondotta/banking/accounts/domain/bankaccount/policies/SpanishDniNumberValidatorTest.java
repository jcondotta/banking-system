package com.jcondotta.banking.accounts.domain.bankaccount.policies;

import com.jcondotta.banking.accounts.domain.bankaccount.enums.DocumentCountry;
import com.jcondotta.banking.accounts.domain.bankaccount.enums.DocumentType;
import com.jcondotta.banking.accounts.domain.bankaccount.value_objects.personal.DocumentNumber;
import com.jcondotta.domain.exception.DomainValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;

class SpanishDniNumberValidatorTest {

  private final SpanishDniNumberValidator validator = new SpanishDniNumberValidator();

  @Test
  void shouldSupportSpainAsCountry() {
    assertThat(validator.supportedCountry()).isEqualTo(DocumentCountry.SPAIN);
  }

  @Test
  void shouldSupportDniAsType() {
    assertThat(validator.supportedType()).isEqualTo(DocumentType.NATIONAL_ID);
  }

  @ParameterizedTest
  @ValueSource(strings = {
    "12345678A",
    "00000001Z",
    "87654321M",
    "99999999B",
    "11111111C"
  })
  void shouldNotThrowException_whenDniNumberIsValid(String validValue) {
    var validNumber = DocumentNumber.of(validValue);

    assertThatCode(() -> validator.validate(validNumber))
      .doesNotThrowAnyException();
  }

  @ParameterizedTest
  @ValueSource(strings = {
    "1234567A",
    "123456789A",
    "12345678",
    "12345678a",
    "ABCDEFGH",
    "A2345678Z",
  })
  void shouldThrowException_whenDniNumberIsInvalid(String invalidValue) {
    var invalidNumber = DocumentNumber.of(invalidValue);

    assertThatThrownBy(() -> validator.validate(invalidNumber))
      .isInstanceOf(DomainValidationException.class)
      .hasMessage(SpanishDniNumberValidator.DNI_NUMBER_INVALID_FORMAT);
  }
}