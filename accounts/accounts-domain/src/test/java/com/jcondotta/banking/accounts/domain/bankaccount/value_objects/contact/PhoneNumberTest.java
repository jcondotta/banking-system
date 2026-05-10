package com.jcondotta.banking.accounts.domain.bankaccount.value_objects.contact;

import com.jcondotta.banking.accounts.domain.bankaccount.arguments_provider.BlankValuesArgumentProvider;
import com.jcondotta.domain.exception.DomainValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PhoneNumberTest {

  private static final String VALID_PHONE_NUMBER = "+34600111222";
  private static final String INVALID_PHONE_NUMBER = "600111222";

  @Test
  void shouldCreatePhoneNumber_whenValueIsValid() {
    var phoneNumber = PhoneNumber.of(VALID_PHONE_NUMBER);

    assertThat(phoneNumber.value()).isEqualTo(VALID_PHONE_NUMBER);
  }

  @Test
  void shouldThrowException_whenValueIsNull() {
    assertThatThrownBy(() -> PhoneNumber.of(null))
      .isInstanceOf(DomainValidationException.class)
      .hasMessage(PhoneNumber.MUST_NOT_BE_EMPTY);
  }

  @ParameterizedTest
  @ArgumentsSource(BlankValuesArgumentProvider.class)
  void shouldThrowException_whenValueIsBlank(String blankPhoneNumber) {
    assertThatThrownBy(() -> PhoneNumber.of(blankPhoneNumber))
      .isInstanceOf(DomainValidationException.class)
      .hasMessage(PhoneNumber.MUST_NOT_BE_EMPTY);
  }

  @Test
  void shouldThrowException_whenFormatIsInvalid() {
    assertThatThrownBy(() -> PhoneNumber.of(INVALID_PHONE_NUMBER))
      .isInstanceOf(DomainValidationException.class)
      .hasMessage(PhoneNumber.INVALID_FORMAT);
  }

  @Test
  void shouldBeEqual_whenValuesAreEqual() {
    var phoneNumber1 = PhoneNumber.of(VALID_PHONE_NUMBER);
    var phoneNumber2 = PhoneNumber.of(VALID_PHONE_NUMBER);

    assertThat(phoneNumber1)
      .isEqualTo(phoneNumber2)
      .hasSameHashCodeAs(phoneNumber2);
  }

  @Test
  void shouldNotBeEqual_whenValuesAreDifferent() {
    var phoneNumber1 = PhoneNumber.of(VALID_PHONE_NUMBER);
    var phoneNumber2 = PhoneNumber.of("+12025550123");

    assertThat(phoneNumber1).isNotEqualTo(phoneNumber2);
  }
}