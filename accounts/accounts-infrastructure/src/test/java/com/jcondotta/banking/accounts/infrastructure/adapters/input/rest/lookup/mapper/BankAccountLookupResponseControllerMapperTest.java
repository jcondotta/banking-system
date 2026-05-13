package com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.lookup.mapper;

import com.jcondotta.banking.accounts.application.bankaccount.query.get.model.*;
import com.jcondotta.banking.accounts.domain.bankaccount.enums.AccountStatus;
import com.jcondotta.banking.accounts.domain.bankaccount.enums.AccountType;
import com.jcondotta.banking.accounts.domain.bankaccount.enums.Currency;
import com.jcondotta.banking.accounts.domain.bankaccount.enums.HolderType;
import com.jcondotta.banking.accounts.domain.testsupport.AddressFixtures;
import com.jcondotta.banking.accounts.domain.testsupport.ContactInfoFixtures;
import com.jcondotta.banking.accounts.domain.testsupport.IdentityDocumentFixtures;
import com.jcondotta.banking.accounts.domain.testsupport.PersonalInfoFixtures;
import com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.lookup.model.AccountHolderDetailsResponse;
import com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.lookup.model.BankAccountDetailsResponse;
import com.jcondotta.banking.accounts.domain.testsupport.TimeTestFactory;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class BankAccountLookupResponseControllerMapperTest {

  private static final UUID BANK_ACCOUNT_ID = UUID.randomUUID();
  private static final UUID ACCOUNT_HOLDER_ID = UUID.randomUUID();
  private static final String VALID_IBAN = "ES3801283316232166447417";
  private static final Instant CREATED_AT = TimeTestFactory.FIXED_INSTANT;

  private final BankAccountLookupResponseControllerMapper mapper =
    new BankAccountLookupResponseControllerMapperImpl(new AccountHolderDetailsResponseMapperImpl());

  @ParameterizedTest
  @EnumSource(HolderType.class)
  void shouldMapBankAccountSummaryToBankAccountDetailsResponse_whenValuesAreValid(HolderType holderType) {
    var identityDocument = new IdentityDocumentSummary(
      IdentityDocumentFixtures.SPANISH_NIE.country().name(),
      IdentityDocumentFixtures.SPANISH_NIE.documentType().name(),
      IdentityDocumentFixtures.SPANISH_NIE.documentNumber().value()
    );
    var personalInfo = new PersonalInfoSummary(
      PersonalInfoFixtures.JEFFERSON.accountHolderName().firstName(),
      PersonalInfoFixtures.JEFFERSON.accountHolderName().lastName(),
      identityDocument,
      PersonalInfoFixtures.JEFFERSON.dateOfBirth().value()
    );
    var contactInfo = new ContactInfoSummary(
      ContactInfoFixtures.JEFFERSON.email().value(),
      ContactInfoFixtures.JEFFERSON.phoneNumber().value()
    );
    var address = new AddressSummary(
      AddressFixtures.BARCELONA_APT.address().street().value(),
      AddressFixtures.BARCELONA_APT.address().streetNumber().value(),
      AddressFixtures.BARCELONA_APT.address().addressComplement().value(),
      AddressFixtures.BARCELONA_APT.address().postalCode().value(),
      AddressFixtures.BARCELONA_APT.address().city().value()
    );
    var holderSummary = new AccountHolderSummary(ACCOUNT_HOLDER_ID, personalInfo, contactInfo, address, holderType, CREATED_AT);
    var bankAccountSummary = new BankAccountSummary(
      BANK_ACCOUNT_ID,
      AccountType.CHECKING,
      Currency.EUR,
      VALID_IBAN,
      AccountStatus.ACTIVE,
      CREATED_AT,
      List.of(holderSummary)
    );

    BankAccountDetailsResponse response = mapper.toBankAccountDetailsResponse(bankAccountSummary);

    assertThat(response).isNotNull();
    assertThat(response.id()).isEqualTo(BANK_ACCOUNT_ID);
    assertThat(response.accountType().name()).isEqualTo(AccountType.CHECKING.name());
    assertThat(response.currency().name()).isEqualTo(Currency.EUR.name());
    assertThat(response.iban()).isEqualTo(VALID_IBAN);
    assertThat(response.accountStatus().name()).isEqualTo(AccountStatus.ACTIVE.name());
    assertThat(response.createdAt()).isEqualTo(CREATED_AT);

    assertThat(response.holders()).hasSize(1);
    AccountHolderDetailsResponse holder = response.holders().getFirst();
    assertThat(holder.id()).isEqualTo(ACCOUNT_HOLDER_ID);
    assertThat(holder.type().name()).isEqualTo(holderType.name());
    assertThat(holder.personalInfo().firstName()).isEqualTo(personalInfo.firstName());
    assertThat(holder.personalInfo().lastName()).isEqualTo(personalInfo.lastName());
    assertThat(holder.contactInfo().email()).isEqualTo(contactInfo.email());
    assertThat(holder.address().street()).isEqualTo(address.street());
    assertThat(holder.createdAt()).isEqualTo(CREATED_AT);
  }
}
