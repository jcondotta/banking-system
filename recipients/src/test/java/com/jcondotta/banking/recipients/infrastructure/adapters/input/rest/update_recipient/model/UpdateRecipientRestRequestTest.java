package com.jcondotta.banking.recipients.infrastructure.adapters.input.rest.update_recipient.model;

import com.jcondotta.banking.recipients.domain.testsupport.BlankValuesSource;
import com.jcondotta.banking.recipients.domain.testsupport.RecipientFixtures;
import com.jcondotta.banking.recipients.infrastructure.testsupport.factory.ValidatorTestFactory;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;

import static org.assertj.core.api.Assertions.assertThat;

class UpdateRecipientRestRequestTest {

  private static final Validator VALIDATOR = ValidatorTestFactory.getValidator();

  private static final String VALID_NAME = RecipientFixtures.JEFFERSON.toName().value();
  private static final String VALID_IBAN = RecipientFixtures.JEFFERSON.toIban().value();

  @Test
  void shouldNotDetectAnyViolation_whenAllFieldsAreValid() {
    var request = new UpdateRecipientRestRequest(VALID_NAME, VALID_IBAN);
    assertThat(VALIDATOR.validate(request)).isEmpty();
  }

  @ParameterizedTest
  @NullSource
  @BlankValuesSource
  void shouldDetectConstraintViolation_whenRecipientNameIsBlank(String invalidName) {
    var request = new UpdateRecipientRestRequest(invalidName, VALID_IBAN);

    assertThat(VALIDATOR.validate(request))
      .hasSize(1)
      .first()
      .satisfies(
        v -> {
          assertThat(v.getPropertyPath()).hasToString("recipientName");
          assertThat(v.getMessage()).isEqualTo("must not be blank");
        });
  }

  @ParameterizedTest
  @NullSource
  @BlankValuesSource
  void shouldDetectConstraintViolation_whenIbanIsBlank(String invalidIban) {
    var request = new UpdateRecipientRestRequest(VALID_NAME, invalidIban);

    assertThat(VALIDATOR.validate(request))
      .hasSize(1)
      .first()
      .satisfies(
        v -> {
          assertThat(v.getPropertyPath()).hasToString("iban");
          assertThat(v.getMessage()).isEqualTo("must not be blank");
        });
  }
}
