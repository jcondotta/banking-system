package com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.common;

import com.jcondotta.banking.accounts.domain.bankaccount.value_objects.personal.AccountHolderName;
import com.jcondotta.banking.accounts.domain.testsupport.IdentityDocumentFixtures;
import com.jcondotta.banking.accounts.domain.testsupport.PersonalInfoFixtures;
import com.jcondotta.banking.accounts.domain.testsupport.BlankValuesSource;
import com.jcondotta.banking.accounts.infrastructure.config.ValidatorTestFactory;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class PersonalInfoRequestTest {

  private static final Validator VALIDATOR = ValidatorTestFactory.getValidator();

  private static final String VALID_FIRST_NAME = PersonalInfoFixtures.JEFFERSON.accountHolderName().firstName();
  private static final String VALID_LAST_NAME = PersonalInfoFixtures.JEFFERSON.accountHolderName().lastName();
  private static final LocalDate VALID_DATE_OF_BIRTH = PersonalInfoFixtures.JEFFERSON.dateOfBirth().value();
  private static final IdentityDocumentRequest VALID_IDENTITY_DOCUMENT = new IdentityDocumentRequest(
    DocumentTypeRequest.FOREIGNER_ID,
    DocumentCountryRequest.SPAIN,
    IdentityDocumentFixtures.SPANISH_NIE.documentNumber().value()
  );

  @Test
  void shouldNotDetectConstraintViolation_whenRequestIsValid() {
    var request = new PersonalInfoRequest(VALID_FIRST_NAME, VALID_LAST_NAME, VALID_IDENTITY_DOCUMENT, VALID_DATE_OF_BIRTH);

    assertThat(VALIDATOR.validate(request)).isEmpty();
  }

  @ParameterizedTest
  @BlankValuesSource
  void shouldDetectConstraintViolation_whenFirstNameIsBlank(String blankFirstName) {
    var request = new PersonalInfoRequest(blankFirstName, VALID_LAST_NAME, VALID_IDENTITY_DOCUMENT, VALID_DATE_OF_BIRTH);

    assertThat(VALIDATOR.validate(request))
      .hasSize(1)
      .first()
      .satisfies(violation -> assertThat(violation.getPropertyPath()).hasToString("firstName"));
  }

  @Test
  void shouldDetectConstraintViolation_whenFirstNameExceedsMaxLength() {
    var longFirstName = "A".repeat(AccountHolderName.MAX_LENGTH + 1);
    var request = new PersonalInfoRequest(longFirstName, VALID_LAST_NAME, VALID_IDENTITY_DOCUMENT, VALID_DATE_OF_BIRTH);

    assertThat(VALIDATOR.validate(request))
      .hasSize(1)
      .first()
      .satisfies(violation -> assertThat(violation.getPropertyPath()).hasToString("firstName"));
  }

  @ParameterizedTest
  @BlankValuesSource
  void shouldDetectConstraintViolation_whenLastNameIsBlank(String blankLastName) {
    var request = new PersonalInfoRequest(VALID_FIRST_NAME, blankLastName, VALID_IDENTITY_DOCUMENT, VALID_DATE_OF_BIRTH);

    assertThat(VALIDATOR.validate(request))
      .hasSize(1)
      .first()
      .satisfies(violation -> assertThat(violation.getPropertyPath()).hasToString("lastName"));
  }

  @Test
  void shouldDetectConstraintViolation_whenLastNameExceedsMaxLength() {
    var longLastName = "A".repeat(AccountHolderName.MAX_LENGTH + 1);
    var request = new PersonalInfoRequest(VALID_FIRST_NAME, longLastName, VALID_IDENTITY_DOCUMENT, VALID_DATE_OF_BIRTH);

    assertThat(VALIDATOR.validate(request))
      .hasSize(1)
      .first()
      .satisfies(violation -> assertThat(violation.getPropertyPath()).hasToString("lastName"));
  }

  @Test
  void shouldDetectConstraintViolation_whenIdentityDocumentIsNull() {
    var request = new PersonalInfoRequest(VALID_FIRST_NAME, VALID_LAST_NAME, null, VALID_DATE_OF_BIRTH);

    assertThat(VALIDATOR.validate(request))
      .hasSize(1)
      .first()
      .satisfies(violation -> assertThat(violation.getPropertyPath()).hasToString("identityDocument"));
  }

  @Test
  void shouldDetectConstraintViolation_whenDateOfBirthIsNull() {
    var request = new PersonalInfoRequest(VALID_FIRST_NAME, VALID_LAST_NAME, VALID_IDENTITY_DOCUMENT, null);

    assertThat(VALIDATOR.validate(request))
      .hasSize(1)
      .first()
      .satisfies(violation -> assertThat(violation.getPropertyPath()).hasToString("dateOfBirth"));
  }

  @Test
  void shouldDetectConstraintViolation_whenDateOfBirthIsInTheFuture() {
    var futureDate = LocalDate.now().plusDays(1);
    var request = new PersonalInfoRequest(VALID_FIRST_NAME, VALID_LAST_NAME, VALID_IDENTITY_DOCUMENT, futureDate);

    assertThat(VALIDATOR.validate(request))
      .hasSize(1)
      .first()
      .satisfies(violation -> assertThat(violation.getPropertyPath()).hasToString("dateOfBirth"));
  }
}
