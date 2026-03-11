package com.jcondotta.banking.recipients.infrastructure.bankaccount.adapters.input.rest.create_recipient.model;

import com.jcondotta.banking.accounts.domain.bankaccount.testsupport.RecipientFixtures;
import com.jcondotta.banking.recipients.infrastructure.bankaccount.testsupport.argument_provider.BlankValuesArgumentProvider;
import com.jcondotta.banking.recipients.infrastructure.bankaccount.testsupport.factory.ValidatorTestFactory;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.NullSource;

import static org.assertj.core.api.Assertions.assertThat;

class CreateRecipientRestRequestTest {

  private static final Validator VALIDATOR = ValidatorTestFactory.getValidator();

  private static final String VALID_NAME = RecipientFixtures.JEFFERSON.toName().value();
  private static final String VALID_IBAN = RecipientFixtures.JEFFERSON.toIban().value();

  @Test
  void shouldNotDetectAnyViolation_whenAllFieldsAreValid() {
    var request = new CreateRecipientRestRequest(VALID_NAME, VALID_IBAN);
    assertThat(VALIDATOR.validate(request)).isEmpty();
  }

  @Test
  void shouldNotDetectAnyViolationUsingFactoryMethod_whenAllFieldsAreValid() {
    var request = new CreateRecipientRestRequest(VALID_NAME, VALID_IBAN);
    assertThat(VALIDATOR.validate(request)).isEmpty();
  }

  @ParameterizedTest
  @NullSource
  @ArgumentsSource(BlankValuesArgumentProvider.class)
  void shouldDetectConstraintViolation__whenRecipientNameIsBlank(String invalidName) {
    var request = new CreateRecipientRestRequest(invalidName, VALID_IBAN);

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
  @ArgumentsSource(BlankValuesArgumentProvider.class)
  void shouldDetectConstraintViolation__whenIbanIsBlank(String invalidIban) {
    var request = new CreateRecipientRestRequest(VALID_NAME, invalidIban);

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
