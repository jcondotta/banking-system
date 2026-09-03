package com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.add_joint_holder.model;

import com.jcondotta.banking.accounts.domain.testsupport.ContactInfoFixtures;
import com.jcondotta.banking.accounts.domain.testsupport.IdentityDocumentFixtures;
import com.jcondotta.banking.accounts.domain.testsupport.PersonalInfoFixtures;
import com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.add_joint_holder.model.AddJointHolderRequest;
import com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.common.*;
import com.jcondotta.banking.accounts.infrastructure.config.ValidatorTestFactory;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AddJointAccountHolderRequestTest {

  private static final Validator VALIDATOR = ValidatorTestFactory.getValidator();

  private static final PersonalInfoRequest VALID_PERSONAL_INFO = new PersonalInfoRequest(
    PersonalInfoFixtures.JEFFERSON.accountHolderName().firstName(),
    PersonalInfoFixtures.JEFFERSON.accountHolderName().lastName(),
    new IdentityDocumentRequest(
      DocumentTypeRequest.FOREIGNER_ID,
      DocumentCountryRequest.SPAIN,
      IdentityDocumentFixtures.SPANISH_NIE.documentNumber().value()
    ),
    PersonalInfoFixtures.JEFFERSON.dateOfBirth().value()
  );

  private static final ContactInfoRequest VALID_CONTACT_INFO = new ContactInfoRequest(
    ContactInfoFixtures.JEFFERSON.email().value(),
    ContactInfoFixtures.JEFFERSON.phoneNumber().value()
  );

  private static final AddressRequest VALID_ADDRESS =
    new AddressRequest("Carrer de Mallorca", "401", null, "08013", "Barcelona");

  @Test
  void shouldNotDetectConstraintViolation_whenRequestIsValid() {
    var request = new AddJointHolderRequest(VALID_PERSONAL_INFO, VALID_CONTACT_INFO, VALID_ADDRESS);

    assertThat(VALIDATOR.validate(request)).isEmpty();
  }

  @Test
  void shouldDetectConstraintViolation_whenPersonalInfoIsNull() {
    var request = new AddJointHolderRequest(null, VALID_CONTACT_INFO, VALID_ADDRESS);

    assertThat(VALIDATOR.validate(request))
      .hasSize(1)
      .first()
      .satisfies(violation -> assertThat(violation.getPropertyPath()).hasToString("personalInfo"));
  }

  @Test
  void shouldDetectConstraintViolation_whenContactInfoIsNull() {
    var request = new AddJointHolderRequest(VALID_PERSONAL_INFO, null, VALID_ADDRESS);

    assertThat(VALIDATOR.validate(request))
      .hasSize(1)
      .first()
      .satisfies(violation -> assertThat(violation.getPropertyPath()).hasToString("contactInfo"));
  }

  @Test
  void shouldDetectConstraintViolation_whenAddressIsNull() {
    var request = new AddJointHolderRequest(VALID_PERSONAL_INFO, VALID_CONTACT_INFO, null);

    assertThat(VALIDATOR.validate(request))
      .hasSize(1)
      .first()
      .satisfies(violation -> assertThat(violation.getPropertyPath()).hasToString("address"));
  }

  @Test
  void shouldDetectConstraintViolation_whenPersonalInfoIsInvalid() {
    var invalidPersonalInfo = new PersonalInfoRequest(null, null, null, null);
    var request = new AddJointHolderRequest(invalidPersonalInfo, VALID_CONTACT_INFO, VALID_ADDRESS);

    assertThat(VALIDATOR.validate(request)).isNotEmpty();
  }
}
