package com.jcondotta.banking.accounts.domain.bankaccount.policies;

import com.jcondotta.banking.accounts.domain.bankaccount.enums.DocumentCountry;
import com.jcondotta.banking.accounts.domain.bankaccount.enums.DocumentType;
import com.jcondotta.banking.accounts.domain.bankaccount.value_objects.personal.DocumentNumber;
import com.jcondotta.domain.exception.DomainValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;

class SpanishNieNumberValidatorTest {

  private final SpanishNieNumberValidator validator = new SpanishNieNumberValidator();

  @Test
  void shouldSupportSpainAsCountry() {
    assertThat(validator.supportedCountry()).isEqualTo(DocumentCountry.SPAIN);
  }

  @Test
  void shouldSupportNieAsType() {
    assertThat(validator.supportedType()).isEqualTo(DocumentType.FOREIGNER_ID);
  }

  @ParameterizedTest
  @ValueSource(strings = {
    "X1234567L",
    "Y7654321A",
    "Z0000000T",
    "X9999999Z",
    "Y0000001B"
  })
  void shouldNotThrowException_whenNieNumberIsValid(String validValue) {
    var validNumber = DocumentNumber.of(validValue);

    assertThatCode(() -> validator.validate(validNumber))
      .doesNotThrowAnyException();
  }

  @ParameterizedTest
  @ValueSource(strings = {
    "A1234567L",
    "X123456L",
    "X12345678L",
    "X1234567",
    "X1234567l",
    "x1234567L",
    "12345678L",
    "XABCDEFGH"
  })
  void shouldThrowException_whenNieNumberIsInvalid(String invalidValue) {
    var invalidNumber = DocumentNumber.of(invalidValue);

    assertThatThrownBy(() -> validator.validate(invalidNumber))
      .isInstanceOf(DomainValidationException.class)
      .hasMessage(SpanishNieNumberValidator.NIE_NUMBER_INVALID_FORMAT);
  }
}