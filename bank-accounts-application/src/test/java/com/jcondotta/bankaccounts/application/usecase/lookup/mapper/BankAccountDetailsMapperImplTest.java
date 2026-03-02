package com.jcondotta.bankaccounts.application.usecase.lookup.mapper;

import com.jcondotta.bankaccounts.application.fixtures.AccountHolderFixtures;
import com.jcondotta.bankaccounts.application.fixtures.BankAccountTestFixture;
import com.jcondotta.bankaccounts.application.usecase.lookup.model.AccountHolderDetails;
import com.jcondotta.bankaccounts.application.usecase.lookup.model.BankAccountDetails;
import com.jcondotta.bankaccounts.domain.aggregates.AccountHolder;
import com.jcondotta.bankaccounts.domain.aggregates.BankAccount;
import com.jcondotta.bankaccounts.domain.enums.AccountHolderType;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BankAccountDetailsMapperImplTest {

  private static final BankAccountDetailsMapper mapper = new BankAccountDetailsMapperImpl(new AccountHolderDetailsMapperImpl());

  @Test
  void shouldMapBankAccount_whenOnlyPrimaryHolderIsPresent() {
    BankAccount bankAccount = BankAccountTestFixture.openPendingAccount(AccountHolderFixtures.JEFFERSON);

    BankAccountDetails accountDetails = mapper.toDetails(bankAccount);

    assertThat(accountDetails.bankAccountId()).isEqualTo(bankAccount.id());
    assertThat(accountDetails.accountType()).isEqualTo(bankAccount.accountType());
    assertThat(accountDetails.currency()).isEqualTo(bankAccount.currency());
    assertThat(accountDetails.iban()).isEqualTo(bankAccount.iban());
    assertThat(accountDetails.accountStatus()).isEqualTo(bankAccount.accountStatus());
    assertThat(accountDetails.createdAt()).isNotNull();

    assertThat(accountDetails.accountHolders())
      .hasSize(1)
      .singleElement()
      .satisfies(accountHolderDetails ->
        assertMapping(bankAccount.primaryAccountHolder(), accountHolderDetails));
  }

  @Test
  void shouldMapBankAccount_whenPrimaryAndJointHolderArePresent() {
    BankAccount bankAccount = BankAccountTestFixture.openActiveAccount(AccountHolderFixtures.JEFFERSON);

    bankAccount.addJointAccountHolder(
      AccountHolderFixtures.PATRIZIO.personalInfo(),
      AccountHolderFixtures.PATRIZIO.contactInfo(),
      AccountHolderFixtures.PATRIZIO.address()
    );

    BankAccountDetails details = mapper.toDetails(bankAccount);
    assertThat(details.accountHolders()).hasSize(2);

    assertThat(details.accountHolders())
      .filteredOn(holder -> holder.accountHolderType() == AccountHolderType.PRIMARY)
      .singleElement()
      .satisfies(primaryHolder -> assertMapping(bankAccount.primaryAccountHolder(), primaryHolder));

    assertThat(details.accountHolders())
      .filteredOn(holder -> holder.accountHolderType() == AccountHolderType.JOINT)
      .singleElement()
      .satisfies(jointHolder -> assertMapping(bankAccount.jointAccountHolders().getFirst(), jointHolder));
  }

  private void assertMapping(AccountHolder source, AccountHolderDetails target) {
    assertThat(target.id()).isEqualTo(source.id().value());

    assertThat(target.firstName())
      .isEqualTo(source.personalInfo().holderName().firstName());

    assertThat(target.lastName())
      .isEqualTo(source.personalInfo().holderName().lastName());

    assertThat(target.documentType())
      .isEqualTo(source.personalInfo().identityDocument().type().name());

    assertThat(target.documentNumber())
      .isEqualTo(source.personalInfo().identityDocument().number().value());

    assertThat(target.dateOfBirth())
      .isEqualTo(source.personalInfo().dateOfBirth().value());

    assertThat(target.email())
      .isEqualTo(source.contactInfo().email().value());

    assertThat(target.phoneNumber())
      .isEqualTo(source.contactInfo().phoneNumber().value());

    assertThat(target.accountHolderType()).isEqualTo(source.accountHolderType());
    assertThat(target.createdAt()).isEqualTo(source.createdAt());
  }

  @Test
  void shouldReturnNull_whenBankAccountIsNull() {
    assertThat(mapper.toDetails(null)).isNull();
  }
}
