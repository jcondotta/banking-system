package com.jcondotta.banking.accounts.application.bankaccount.query.get.mapper;

import com.jcondotta.banking.accounts.application.bankaccount.query.get.model.AccountHolderSummary;
import com.jcondotta.banking.accounts.domain.bankaccount.aggregate.BankAccount;
import com.jcondotta.banking.accounts.domain.bankaccount.enums.AccountStatus;
import com.jcondotta.banking.accounts.domain.bankaccount.enums.AccountType;
import com.jcondotta.banking.accounts.domain.bankaccount.enums.Currency;
import com.jcondotta.banking.accounts.domain.bankaccount.enums.HolderType;
import com.jcondotta.banking.accounts.domain.bankaccount.identity.BankAccountId;
import com.jcondotta.banking.accounts.domain.testsupport.AccountHolderFixtures;
import com.jcondotta.banking.accounts.domain.testsupport.BankAccountFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BankAccountSummaryMapperImplTest {

  private final IdentityDocumentSummaryMapper identityMapper = new IdentityDocumentSummaryMapperImpl();

  private final AccountHolderSummaryMapper accountHolderSummaryMapper = new AccountHolderSummaryMapperImpl(
    new PersonalInfoSummaryMapperImpl(identityMapper),
    new ContactInfoSummaryMapperImpl(),
    new AddressSummaryMapperImpl()
  );

  private BankAccountSummaryMapper bankAccountMapper;

  @BeforeEach
  void setUp() {
    bankAccountMapper = new BankAccountSummaryMapperImpl(accountHolderSummaryMapper);
  }

  @Test
  void shouldMapBankAccount_whenOnlyPrimaryHolderIsPresent() {
    var bankAccount = BankAccountFixture.openPendingAccount(AccountHolderFixtures.JEFFERSON);
    var bankAccountSummary = bankAccountMapper.toSummary(bankAccount);

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
    var bankAccount = BankAccountFixture.openActiveAccount(AccountHolderFixtures.JEFFERSON);

    bankAccount.addJointHolder(
      AccountHolderFixtures.PATRIZIO.personalInfo(),
      AccountHolderFixtures.PATRIZIO.contactInfo(),
      AccountHolderFixtures.PATRIZIO.address()
    );

    var details = bankAccountMapper.toSummary(bankAccount);
    assertThat(details.holders())
      .hasSize(2)
      .extracting(AccountHolderSummary::type)
      .containsExactlyInAnyOrder(HolderType.PRIMARY, HolderType.JOINT);
  }

  @Test
  void shouldReturnNull_whenBankAccountIsNull() {
    assertThat(bankAccountMapper.toSummary(null)).isNull();
  }

  @Test
  void shouldThrowNullPointerException_whenBankAccountIbanIsNull() {
    var bankAccount = mock(BankAccount.class);
    mockRequiredValues(bankAccount);

    assertThatThrownBy(() -> bankAccountMapper.toSummary(bankAccount))
      .isInstanceOf(NullPointerException.class)
      .hasMessage("iban must be provided");
  }

  @Test
  void shouldThrowNullPointerException_whenBankAccountIdIsNull() {
    var bankAccount = mock(BankAccount.class);

    assertThatThrownBy(() -> bankAccountMapper.toSummary(bankAccount))
      .isInstanceOf(NullPointerException.class)
      .hasMessage("id must be provided");
  }

  @Test
  void shouldThrowNullPointerException_whenBankAccountHoldersAreNull() {
    var bankAccount = mock(BankAccount.class);
    mockRequiredValues(bankAccount);
    when(bankAccount.getIban()).thenReturn(BankAccountFixture.VALID_IBAN);
    when(bankAccount.getActiveHolders()).thenReturn(null);

    assertThatThrownBy(() -> bankAccountMapper.toSummary(bankAccount))
      .isInstanceOf(NullPointerException.class)
      .hasMessage("holders must be provided");
  }

  private static void mockRequiredValues(BankAccount bankAccount) {
    when(bankAccount.getId()).thenReturn(BankAccountId.newId());
    when(bankAccount.getAccountType()).thenReturn(AccountType.CHECKING);
    when(bankAccount.getCurrency()).thenReturn(Currency.EUR);
    when(bankAccount.getAccountStatus()).thenReturn(AccountStatus.PENDING);
    when(bankAccount.getCreatedAt()).thenReturn(Instant.now());
    when(bankAccount.getActiveHolders()).thenReturn(List.of());
  }
}
