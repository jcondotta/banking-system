package com.jcondotta.banking.accounts.domain.bankaccount.value_objects.contact;

import com.jcondotta.domain.exception.DomainValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ContactInfoTest {

  private static final Email VALID_EMAIL = Email.of("jefferson.condotta@email.com");
  private static final PhoneNumber VALID_PHONE_NUMBER = PhoneNumber.of("+34600111222");

  @Test
  void shouldCreateContactInfo_whenValuesAreValid() {
    var contactInfo = ContactInfo.of(VALID_EMAIL, VALID_PHONE_NUMBER);

    assertThat(contactInfo.email()).isEqualTo(VALID_EMAIL);
    assertThat(contactInfo.phoneNumber()).isEqualTo(VALID_PHONE_NUMBER);
  }

  @Test
  void shouldThrowException_whenEmailIsNull() {
    assertThatThrownBy(() -> ContactInfo.of(null, VALID_PHONE_NUMBER))
      .isInstanceOf(DomainValidationException.class)
      .hasMessage(ContactInfo.EMAIL_NOT_PROVIDED);
  }

  @Test
  void shouldThrowException_whenPhoneNumberIsNull() {
    assertThatThrownBy(() -> ContactInfo.of(VALID_EMAIL, null))
      .isInstanceOf(DomainValidationException.class)
      .hasMessage(ContactInfo.PHONE_NUMBER_NOT_PROVIDED);
  }

  @Test
  void shouldBeEqual_whenValuesAreEqual() {
    var contactInfo1 = ContactInfo.of(VALID_EMAIL, VALID_PHONE_NUMBER);
    var contactInfo2 = ContactInfo.of(VALID_EMAIL, VALID_PHONE_NUMBER);

    assertThat(contactInfo1)
      .isEqualTo(contactInfo2)
      .hasSameHashCodeAs(contactInfo2);
  }

  @Test
  void shouldNotBeEqual_whenValuesAreDifferent() {
    var contactInfo1 = ContactInfo.of(VALID_EMAIL, VALID_PHONE_NUMBER);

    var contactInfo2 = ContactInfo.of(
      Email.of("other@email.com"),
      VALID_PHONE_NUMBER
    );

    assertThat(contactInfo1).isNotEqualTo(contactInfo2);
  }
}