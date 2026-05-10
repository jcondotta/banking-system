package com.jcondotta.banking.accounts.domain.bankaccount.value_objects;

import com.jcondotta.banking.accounts.domain.bankaccount.arguments_provider.BlankValuesArgumentProvider;
import com.jcondotta.domain.exception.DomainValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class IbanTest {

  private static final String VALID_IBAN_1 = "DE44500105175407324931";
  private static final String VALID_IBAN_2 = "FR1420041010050500013M02606";

  @Test
  void shouldCreateIban_whenValueIsValid() {
    var iban = Iban.of(VALID_IBAN_1);

    assertThat(iban)
      .extracting(Iban::value)
      .isEqualTo(VALID_IBAN_1);
  }

  @Test
  void shouldNormalizeIbanValue() {
    var iban = Iban.of("  de44 5001 0517 5407 3249 31  ");

    assertThat(iban.value()).isEqualTo(VALID_IBAN_1);
  }

  @Test
  void shouldThrowException_whenValueIsNull() {
    assertThatThrownBy(() -> Iban.of(null))
      .isInstanceOf(DomainValidationException.class)
      .hasMessage(Iban.IBAN_NOT_PROVIDED);
  }

  @ParameterizedTest
  @ArgumentsSource(BlankValuesArgumentProvider.class)
  void shouldThrowException_whenValueIsBlank(String blankValue) {
    assertThatThrownBy(() -> Iban.of(blankValue))
      .isInstanceOf(DomainValidationException.class)
      .hasMessage(Iban.IBAN_NOT_PROVIDED);
  }

  @Test
  void shouldThrowException_whenIbanIsTooShort() {
    var tooShortIban = "A".repeat(14);

    assertThatThrownBy(() -> Iban.of(tooShortIban))
      .isInstanceOf(DomainValidationException.class)
      .hasMessage(Iban.IBAN_INVALID_FORMAT);
  }

  @Test
  void shouldThrowException_whenIbanIsTooLong() {
    var tooLongIban = "A".repeat(35);

    assertThatThrownBy(() -> Iban.of(tooLongIban))
      .isInstanceOf(DomainValidationException.class)
      .hasMessage(Iban.IBAN_INVALID_FORMAT);
  }

  @Test
  void shouldThrowException_whenIbanContainsInvalidCharacter() {
    assertThatThrownBy(() -> Iban.of("DE44$00105175407324931"))
      .isInstanceOf(DomainValidationException.class)
      .hasMessage(Iban.IBAN_INVALID_FORMAT);
  }

  @Test
  void shouldThrowException_whenIbanChecksumIsInvalid() {
    assertThatThrownBy(() -> Iban.of("DE44500105175407324932"))
      .isInstanceOf(DomainValidationException.class)
      .hasMessage(Iban.IBAN_INVALID_FORMAT);
  }

  @Test
  void shouldBeEqual_whenIbansHaveSameValue() {
    var iban1 = Iban.of(VALID_IBAN_1);
    var iban2 = Iban.of(VALID_IBAN_1);

    assertThat(iban1)
      .isEqualTo(iban2)
      .hasSameHashCodeAs(iban2);
  }

  @Test
  void shouldNotBeEqual_whenIbansHaveDifferentValues() {
    var iban1 = Iban.of(VALID_IBAN_1);
    var iban2 = Iban.of(VALID_IBAN_2);

    assertThat(iban1).isNotEqualTo(iban2);
  }
}