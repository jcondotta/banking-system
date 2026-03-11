package com.jcondotta.banking.accounts.application.bankaccount.query.get.mapper;

import com.jcondotta.banking.accounts.application.bankaccount.query.get.model.AccountHolderSummary;
import com.jcondotta.banking.accounts.application.bankaccount.query.get.model.BankAccountSummary;
import com.jcondotta.banking.accounts.domain.bankaccount.aggregate.BankAccount;
import com.jcondotta.banking.accounts.domain.bankaccount.enums.HolderType;
import com.jcondotta.banking.accounts.domain.bankaccount.testsupport.AccountHolderFixtures;
import com.jcondotta.banking.accounts.domain.bankaccount.testsupport.BankAccountFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BankAccountSummaryMapperImplTest {

  private BankAccountSummaryMapper bankAccountMapper;

  @BeforeEach
  void setUp() {
    var accountHolderMapper = new AccountHolderSummaryMapperImpl(
      new PersonalInfoSummaryMapperImpl(new IdentityDocumentSummaryMapper() {
      }),
      new ContactInfoSummaryMapper() {
      },
      new AddressSummaryMapper() {}
    );

    bankAccountMapper = new BankAccountSummaryMapperImpl(accountHolderMapper);
  }

  @Test
  void shouldMapBankAccount_whenOnlyPrimaryHolderIsPresent() {
    BankAccount bankAccount = BankAccountFixture.openPendingAccount(AccountHolderFixtures.JEFFERSON);

    BankAccountSummary bankAccountSummary = bankAccountMapper.toSummary(bankAccount);

    assertThat(bankAccountSummary.id()).isEqualTo(bankAccount.getId().value());
    assertThat(bankAccountSummary.accountType()).isEqualTo(bankAccount.getAccountType());
    assertThat(bankAccountSummary.currency()).isEqualTo(bankAccount.getCurrency());
    assertThat(bankAccountSummary.iban()).isEqualTo(bankAccount.getIban().value());
    assertThat(bankAccountSummary.accountStatus()).isEqualTo(bankAccount.getAccountStatus());
    assertThat(bankAccountSummary.createdAt()).isNotNull();

    assertThat(bankAccountSummary.holders())
      .extracting(AccountHolderSummary::type)
      .containsExactly(HolderType.PRIMARY);
  }

  @Test
  void shouldMapBankAccount_whenPrimaryAndJointHolderArePresent() {
    BankAccount bankAccount = BankAccountFixture.openActiveAccount(AccountHolderFixtures.JEFFERSON);

    bankAccount.addJointHolder(
      AccountHolderFixtures.PATRIZIO.personalInfo(),
      AccountHolderFixtures.PATRIZIO.contactInfo(),
      AccountHolderFixtures.PATRIZIO.address()
    );

    BankAccountSummary details = bankAccountMapper.toSummary(bankAccount);
    assertThat(details.holders())
      .hasSize(2)
      .extracting(AccountHolderSummary::type)
      .containsExactlyInAnyOrder(HolderType.PRIMARY, HolderType.JOINT);
  }

  @Test
  void shouldReturnNull_whenBankAccountIsNull() {
    assertThat(bankAccountMapper.toSummary(null)).isNull();
  }
}
