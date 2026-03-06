package com.jcondotta.banking.recipients.domain.recipient.value_objects;

import com.jcondotta.banking.recipients.domain.recipient.argument_provider.BlankValuesArgumentProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class IbanTest {

  private static final String VALID_IBAN_NO_SPACES = "GB82WEST12345698765432";

  @ParameterizedTest(name = "{index} => input={0}")
  @ValueSource(
    strings = {
      "GB82WEST12345698765432",
      "GB82 WEST 1234 5698 7654 32",
      "   GB82WEST12345698765432   ",
      "GB82\tWEST\n1234\t5698\n7654\t32"
    })
  void shouldCreateIban_whenValueIsValid(String rawValidIban) {
    assertThat(Iban.of(rawValidIban)).extracting(Iban::value).isEqualTo(VALID_IBAN_NO_SPACES);
  }

  @Test
  void shouldThrowNullPointerException_whenIbanIsNull() {
    assertThatThrownBy(() -> Iban.of(null))
      .isInstanceOf(NullPointerException.class)
      .hasMessage(Iban.IBAN_NOT_PROVIDED);
  }

  @ParameterizedTest
  @ArgumentsSource(BlankValuesArgumentProvider.class)
  void shouldThrowIllegalArgumentException_whenIbanIsBlank(String blankValue) {
    assertThatThrownBy(() -> Iban.of(blankValue))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage(Iban.IBAN_INVALID_FORMAT_MESSAGE);
  }

  @ParameterizedTest(name = "{index} => invalid input={0}")
  @ValueSource(
    strings = {
      "", // empty
      "GB82WEST123", // too short
      "GB82WEST1234569876543212345678901234567890", // too long
      "1B82WEST12345698765432", // invalid country code
      "GB00WEST12345698765432", // bad check digits
      "GB82WEST1234$698765432" // invalid chars
    })
  void shouldThrowIllegalArgumentException_whenIbanFormatIsInvalid(String invalidIban) {
    assertThatThrownBy(() -> Iban.of(invalidIban))
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage(Iban.IBAN_INVALID_FORMAT_MESSAGE);
  }

  @Test
  void shouldBeEqual_whenIbansHaveSameValue() {
    var iban1 = Iban.of(VALID_IBAN_NO_SPACES);
    var iban2 = Iban.of(VALID_IBAN_NO_SPACES);

    assertThat(iban1).isEqualTo(iban2).hasSameHashCodeAs(iban2);
  }

  @Test
  void shouldNotBeEqual_whenIbansHaveDifferentValues() {
    var iban1 = Iban.of(VALID_IBAN_NO_SPACES);
    var iban2 = Iban.of("GB94BARC10201530093459");

    assertThat(iban1).isNotEqualTo(iban2);
  }
}
