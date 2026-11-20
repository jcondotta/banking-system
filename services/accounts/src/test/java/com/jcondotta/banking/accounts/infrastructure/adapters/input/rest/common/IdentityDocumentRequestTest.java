package com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.common;

import com.jcondotta.banking.accounts.domain.bankaccount.value_objects.personal.DocumentNumber;
import com.jcondotta.banking.accounts.domain.testsupport.IdentityDocumentFixtures;
import com.jcondotta.banking.accounts.domain.testsupport.BlankValuesSource;
import com.jcondotta.banking.accounts.infrastructure.config.ValidatorTestFactory;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;

import static org.assertj.core.api.Assertions.assertThat;

class IdentityDocumentRequestTest {

  private static final Validator VALIDATOR = ValidatorTestFactory.getValidator();

  private static final DocumentTypeRequest VALID_TYPE = DocumentTypeRequest.FOREIGNER_ID;
  private static final DocumentCountryRequest VALID_COUNTRY = DocumentCountryRequest.SPAIN;
  private static final String VALID_NUMBER = IdentityDocumentFixtures.SPANISH_NIE.documentNumber().value();

  @Test
  void shouldNotDetectConstraintViolation_whenRequestIsValid() {
    var request = new IdentityDocumentRequest(VALID_TYPE, VALID_COUNTRY, VALID_NUMBER);

    assertThat(VALIDATOR.validate(request)).isEmpty();
  }

  @Test
  void shouldDetectConstraintViolation_whenTypeIsNull() {
    var request = new IdentityDocumentRequest(null, VALID_COUNTRY, VALID_NUMBER);

    assertThat(VALIDATOR.validate(request))
      .hasSize(1)
      .first()
      .satisfies(violation -> assertThat(violation.getPropertyPath()).hasToString("type"));
  }

  @Test
  void shouldDetectConstraintViolation_whenCountryIsNull() {
    var request = new IdentityDocumentRequest(VALID_TYPE, null, VALID_NUMBER);

    assertThat(VALIDATOR.validate(request))
      .hasSize(1)
      .first()
      .satisfies(violation -> assertThat(violation.getPropertyPath()).hasToString("country"));
  }

  @ParameterizedTest
  @BlankValuesSource
  void shouldDetectConstraintViolation_whenNumberIsBlank(String blankNumber) {
    var request = new IdentityDocumentRequest(VALID_TYPE, VALID_COUNTRY, blankNumber);

    assertThat(VALIDATOR.validate(request))
      .hasSize(1)
      .first()
      .satisfies(violation -> assertThat(violation.getPropertyPath()).hasToString("number"));
  }

  @Test
  void shouldDetectConstraintViolation_whenNumberExceedsMaxLength() {
    var tooLongNumber = "A".repeat(DocumentNumber.MAX_LENGTH + 1);
    var request = new IdentityDocumentRequest(VALID_TYPE, VALID_COUNTRY, tooLongNumber);

    assertThat(VALIDATOR.validate(request))
      .hasSize(1)
      .first()
      .satisfies(violation -> assertThat(violation.getPropertyPath()).hasToString("number"));
  }
}
