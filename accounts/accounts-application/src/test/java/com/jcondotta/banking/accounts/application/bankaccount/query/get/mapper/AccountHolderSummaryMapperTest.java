package com.jcondotta.banking.accounts.application.bankaccount.query.get.mapper;

import com.jcondotta.banking.accounts.application.bankaccount.query.get.model.AccountHolderSummary;
import com.jcondotta.banking.accounts.domain.bankaccount.aggregate.AccountHolder;
import com.jcondotta.banking.accounts.domain.bankaccount.testsupport.AccountHolderFixtures;
import com.jcondotta.banking.accounts.domain.bankaccount.testsupport.BankAccountFixture;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class AccountHolderSummaryMapperTest {

  private final IdentityDocumentSummaryMapper identityMapper = new IdentityDocumentSummaryMapperImpl();

  private final AccountHolderSummaryMapper mapper = new AccountHolderSummaryMapperImpl(
    new PersonalInfoSummaryMapperImpl(identityMapper),
    new ContactInfoSummaryMapperImpl(),
    new AddressSummaryMapperImpl()
  );

  @Test
  void shouldMapAccountHolderToSummary_whenAllFieldsArePresent() {
    var accountHolder = BankAccountFixture.createPrimaryHolder(AccountHolderFixtures.JEFFERSON, Instant.now());

    AccountHolderSummary details = mapper.toSummary(accountHolder);

    assertThat(details)
      .satisfies(holderDetails -> {
        assertThat(holderDetails.id()).isEqualTo(accountHolder.getId().value());
        assertThat(holderDetails.type()).isEqualTo(accountHolder.getAccountHolderType());
        assertThat(holderDetails.createdAt()).isEqualTo(accountHolder.getCreatedAt());

        assertPersonalInfo(holderDetails, accountHolder);
        assertContactInfo(holderDetails, accountHolder);
        assertAddress(holderDetails, accountHolder);
      });
  }

  @Test
  void shouldReturnNull_whenAccountHolderIsNull() {
    assertThat(mapper.toSummary(null)).isNull();
  }

  private void assertPersonalInfo(AccountHolderSummary details, AccountHolder accountHolder) {
    var personalInfo = details.personalInfo();
    var source = accountHolder.getPersonalInfo();

    assertThat(personalInfo.firstName()).isEqualTo(source.holderName().firstName());
    assertThat(personalInfo.lastName()).isEqualTo(source.holderName().lastName());
    assertThat(personalInfo.dateOfBirth()).isEqualTo(source.dateOfBirth().value());

    assertThat(personalInfo.identityDocument().country())
      .isEqualTo(source.identityDocument().country().name());
    assertThat(personalInfo.identityDocument().type())
      .isEqualTo(source.identityDocument().type().name());
    assertThat(personalInfo.identityDocument().number())
      .isEqualTo(source.identityDocument().number().value());
  }

  private void assertContactInfo(AccountHolderSummary details, AccountHolder accountHolder) {
    var contactInfo = details.contactInfo();
    var source = accountHolder.getContactInfo();

    assertThat(contactInfo.email()).isEqualTo(source.email().value());
    assertThat(contactInfo.phoneNumber()).isEqualTo(source.phoneNumber().value());
  }

  private void assertAddress(AccountHolderSummary details, AccountHolder accountHolder) {
    var address = details.address();
    var source = accountHolder.getAddress();

    assertThat(address.street()).isEqualTo(source.street().value());
    assertThat(address.streetNumber()).isEqualTo(source.streetNumber().value());
    assertThat(address.addressComplement()).isEqualTo(source.addressComplement().value());
    assertThat(address.postalCode()).isEqualTo(source.postalCode().value());
    assertThat(address.city()).isEqualTo(source.city().value());
  }
}