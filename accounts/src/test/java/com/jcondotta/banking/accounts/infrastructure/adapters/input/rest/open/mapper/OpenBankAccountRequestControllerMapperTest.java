package com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.open.mapper;

import com.jcondotta.banking.accounts.application.bankaccount.command.open.model.OpenBankAccountCommand;
import com.jcondotta.banking.accounts.domain.bankaccount.enums.AccountType;
import com.jcondotta.banking.accounts.domain.bankaccount.enums.Currency;
import com.jcondotta.banking.accounts.domain.testsupport.ContactInfoFixtures;
import com.jcondotta.banking.accounts.domain.testsupport.IdentityDocumentFixtures;
import com.jcondotta.banking.accounts.domain.testsupport.PersonalInfoFixtures;
import com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.common.*;
import com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.open.model.AccountTypeRequest;
import com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.open.model.CurrencyRequest;
import com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.open.model.OpenBankAccountRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

class OpenBankAccountRequestControllerMapperTest {

  private final OpenBankAccountRequestControllerMapper mapper =
    Mappers.getMapper(OpenBankAccountRequestControllerMapper.class);

  private static final String VALID_FIRST_NAME = PersonalInfoFixtures.JEFFERSON.accountHolderName().firstName();
  private static final String VALID_LAST_NAME = PersonalInfoFixtures.JEFFERSON.accountHolderName().lastName();
  private static final String VALID_DOCUMENT_NUMBER = IdentityDocumentFixtures.SPANISH_NIE.documentNumber().value();
  private static final String VALID_EMAIL = ContactInfoFixtures.JEFFERSON.email().value();
  private static final String VALID_PHONE = ContactInfoFixtures.JEFFERSON.phoneNumber().value();

  @ParameterizedTest
  @EnumSource(AccountTypeRequest.class)
  void shouldMapOpenBankAccountRequestToCommand_whenValuesAreValid(AccountTypeRequest accountType) {
    var request = buildValidRequest(accountType, CurrencyRequest.EUR);

    OpenBankAccountCommand command = mapper.toCommand(request);

    assertThat(command).isNotNull();
    assertThat(command.personalInfo().holderName().firstName()).isEqualTo(VALID_FIRST_NAME);
    assertThat(command.personalInfo().holderName().lastName()).isEqualTo(VALID_LAST_NAME);
    assertThat(command.personalInfo().identityDocument().number().value()).isEqualTo(VALID_DOCUMENT_NUMBER);
    assertThat(command.contactInfo().email().value()).isEqualTo(VALID_EMAIL);
    assertThat(command.contactInfo().phoneNumber().value()).isEqualTo(VALID_PHONE);
    assertThat(command.accountType()).isEqualTo(AccountType.valueOf(accountType.name()));
    assertThat(command.currency()).isEqualTo(Currency.EUR);
  }

  @Test
  void shouldReturnNull_whenMappedRequestIsNull() {
    OpenBankAccountCommand command = mapper.toCommand(null);

    assertThat(command).isNull();
  }

  private OpenBankAccountRequest buildValidRequest(AccountTypeRequest accountType, CurrencyRequest currency) {
    var identityDoc = new IdentityDocumentRequest(
      DocumentTypeRequest.FOREIGNER_ID,
      DocumentCountryRequest.SPAIN,
      VALID_DOCUMENT_NUMBER
    );
    var personalInfo = new PersonalInfoRequest(
      VALID_FIRST_NAME,
      VALID_LAST_NAME,
      identityDoc,
      PersonalInfoFixtures.JEFFERSON.dateOfBirth().value()
    );
    var contactInfo = new ContactInfoRequest(VALID_EMAIL, VALID_PHONE);
    var address = new AddressRequest("Carrer de Mallorca", "401", null, "08013", "Barcelona");
    var holder = new AccountHolderRequest(personalInfo, contactInfo, address);
    return new OpenBankAccountRequest(accountType, currency, holder);
  }
}
