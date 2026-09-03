package com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.common;

import com.jcondotta.banking.accounts.domain.bankaccount.value_objects.contact.Email;
import com.jcondotta.banking.accounts.domain.testsupport.ContactInfoFixtures;
import com.jcondotta.banking.accounts.domain.testsupport.BlankValuesSource;
import com.jcondotta.banking.accounts.infrastructure.config.ValidatorTestFactory;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;

import static org.assertj.core.api.Assertions.assertThat;

class ContactInfoRequestTest {

  private static final Validator VALIDATOR = ValidatorTestFactory.getValidator();

  private static final String VALID_EMAIL = ContactInfoFixtures.JEFFERSON.email().value();
  private static final String VALID_PHONE_NUMBER = ContactInfoFixtures.JEFFERSON.phoneNumber().value();

  @Test
  void shouldNotDetectConstraintViolation_whenRequestIsValid() {
    var request = new ContactInfoRequest(VALID_EMAIL, VALID_PHONE_NUMBER);

    assertThat(VALIDATOR.validate(request)).isEmpty();
  }

  @ParameterizedTest
  @BlankValuesSource
  void shouldDetectConstraintViolation_whenEmailIsBlank(String blankEmail) {
    var request = new ContactInfoRequest(blankEmail, VALID_PHONE_NUMBER);

    assertThat(VALIDATOR.validate(request))
      .isNotEmpty()
      .allSatisfy(violation -> assertThat(violation.getPropertyPath()).hasToString("email"));
  }

  @Test
  void shouldDetectConstraintViolation_whenEmailExceedsMaxLength() {
    var longEmail = "a".repeat(Email.MAX_LENGTH + 1) + "@email.com";
    var request = new ContactInfoRequest(longEmail, VALID_PHONE_NUMBER);

    assertThat(VALIDATOR.validate(request))
      .isNotEmpty()
      .allSatisfy(violation -> assertThat(violation.getPropertyPath()).hasToString("email"));
  }

  @ParameterizedTest
  @BlankValuesSource
  void shouldDetectConstraintViolation_whenPhoneNumberIsBlank(String blankPhoneNumber) {
    var request = new ContactInfoRequest(VALID_EMAIL, blankPhoneNumber);

    assertThat(VALIDATOR.validate(request))
      .hasSize(1)
      .first()
      .satisfies(violation -> assertThat(violation.getPropertyPath()).hasToString("phoneNumber"));
  }
}
