package com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.open.model;

import com.jcondotta.banking.accounts.domain.testsupport.ContactInfoFixtures;
import com.jcondotta.banking.accounts.domain.testsupport.IdentityDocumentFixtures;
import com.jcondotta.banking.accounts.domain.testsupport.PersonalInfoFixtures;
import com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.common.*;
import com.jcondotta.banking.accounts.infrastructure.config.ValidatorTestFactory;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class OpenBankAccountRequestTest {

  private static final Validator VALIDATOR = ValidatorTestFactory.getValidator();

  private static final AccountHolderRequest VALID_ACCOUNT_HOLDER = buildValidAccountHolder();

  @ParameterizedTest
  @EnumSource(AccountTypeRequest.class)
  void shouldNotDetectConstraintViolation_whenRequestIsValid(AccountTypeRequest accountType) {
    var request = new OpenBankAccountRequest(accountType, CurrencyRequest.EUR, VALID_ACCOUNT_HOLDER);

    assertThat(VALIDATOR.validate(request)).isEmpty();
  }

  @ParameterizedTest
  @EnumSource(CurrencyRequest.class)
  void shouldDetectConstraintViolation_whenAccountTypeIsNull(CurrencyRequest currency) {
    var request = new OpenBankAccountRequest(null, currency, VALID_ACCOUNT_HOLDER);

    assertThat(VALIDATOR.validate(request))
      .hasSize(1)
      .first()
      .satisfies(violation -> assertThat(violation.getPropertyPath()).hasToString("accountType"));
  }

  @ParameterizedTest
  @EnumSource(AccountTypeRequest.class)
  void shouldDetectConstraintViolation_whenCurrencyIsNull(AccountTypeRequest accountType) {
    var request = new OpenBankAccountRequest(accountType, null, VALID_ACCOUNT_HOLDER);

    assertThat(VALIDATOR.validate(request))
      .hasSize(1)
      .first()
      .satisfies(violation -> assertThat(violation.getPropertyPath()).hasToString("currency"));
  }

  @ParameterizedTest
  @EnumSource(AccountTypeRequest.class)
  void shouldDetectConstraintViolation_whenAccountHolderIsNull(AccountTypeRequest accountType) {
    var request = new OpenBankAccountRequest(accountType, CurrencyRequest.EUR, null);

    assertThat(VALIDATOR.validate(request))
      .hasSize(1)
      .first()
      .satisfies(violation -> assertThat(violation.getPropertyPath()).hasToString("primaryHolder"));
  }

  @Test
  void shouldDetectConstraintViolation_whenAccountHolderIsInvalid() {
    var invalidPersonalInfo = new PersonalInfoRequest(null, null, null, null);
    var invalidAccountHolder = new AccountHolderRequest(invalidPersonalInfo, null, null);

    var request = new OpenBankAccountRequest(AccountTypeRequest.SAVINGS, CurrencyRequest.EUR, invalidAccountHolder);

    assertThat(VALIDATOR.validate(request)).isNotEmpty();
  }

  private static AccountHolderRequest buildValidAccountHolder() {
    var personalInfo = new PersonalInfoRequest(
      PersonalInfoFixtures.JEFFERSON.accountHolderName().firstName(),
      PersonalInfoFixtures.JEFFERSON.accountHolderName().lastName(),
      new IdentityDocumentRequest(
        DocumentTypeRequest.FOREIGNER_ID,
        DocumentCountryRequest.SPAIN,
        IdentityDocumentFixtures.SPANISH_NIE.documentNumber().value()
      ),
      PersonalInfoFixtures.JEFFERSON.dateOfBirth().value()
    );
    var contactInfo = new ContactInfoRequest(
      ContactInfoFixtures.JEFFERSON.email().value(),
      ContactInfoFixtures.JEFFERSON.phoneNumber().value()
    );
    var address = new AddressRequest("Carrer de Mallorca", "401", null, "08013", "Barcelona");
    return new AccountHolderRequest(personalInfo, contactInfo, address);
  }
}
