package com.jcondotta.banking.accounts.domain.bankaccount.aggregate;

import com.jcondotta.banking.accounts.domain.bankaccount.enums.AccountStatus;
import com.jcondotta.banking.accounts.domain.bankaccount.enums.AccountType;
import com.jcondotta.banking.accounts.domain.bankaccount.enums.Currency;
import com.jcondotta.banking.accounts.domain.bankaccount.events.BankAccountActivatedEvent;
import com.jcondotta.banking.accounts.domain.bankaccount.exceptions.InvalidBankAccountStateTransitionException;
import com.jcondotta.banking.accounts.domain.bankaccount.factory.ClockTestFactory;
import com.jcondotta.banking.accounts.domain.bankaccount.fixtures.AccountHolderFixtures;
import com.jcondotta.banking.accounts.domain.bankaccount.fixtures.BankAccountTestFixture;
import com.jcondotta.banking.accounts.domain.bankaccount.identity.BankAccountId;
import com.jcondotta.banking.accounts.domain.bankaccount.value_objects.Iban;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BankAccountActivateTest {

  private static final AccountHolderFixtures PRIMARY_ACCOUNT_HOLDER = AccountHolderFixtures.JEFFERSON;

  private static final Iban VALID_IBAN = BankAccountTestFixture.VALID_IBAN;
  private static final AccountType ACCOUNT_TYPE_SAVINGS = AccountType.SAVINGS;
  private static final Currency CURRENCY_USD = Currency.USD;

  private static final Instant ACCOUNT_CREATED_AT = Instant.now(ClockTestFactory.FIXED_CLOCK);

  @Test
  void shouldActivateBankAccount_whenStatusIsPending() {
    var bankAccount = BankAccountTestFixture.openPendingAccount(PRIMARY_ACCOUNT_HOLDER);
    bankAccount.pullEvents();

    bankAccount.activate();
    assertThat(bankAccount.getAccountStatus().isActive()).isTrue();

    var events = bankAccount.pullEvents();

    assertThat(events)
      .hasSize(1)
      .singleElement()
      .isInstanceOfSatisfying(BankAccountActivatedEvent.class, event -> {
        assertThat(event.aggregateId()).isEqualTo(bankAccount.getId());
        assertThat(event.occurredAt()).isNotNull();
      });
  }

  @Test
  void shouldNotThrowAnyException_whenActivateIsCalledTwice() {
    var bankAccount = BankAccountTestFixture.openPendingAccount(PRIMARY_ACCOUNT_HOLDER, ACCOUNT_TYPE_SAVINGS, CURRENCY_USD);

    bankAccount.activate();
    bankAccount.activate();

    assertThat(bankAccount.getAccountStatus().isActive()).isTrue();
  }

  @ParameterizedTest
  @EnumSource(value = AccountStatus.class, names = {"PENDING", "ACTIVE"}, mode = EnumSource.Mode.EXCLUDE)
  void shouldThrowInvalidBankAccountStateTransitionException_whenActivatingFromInvalidState(AccountStatus status) {
    var primaryAccountHolder = BankAccountTestFixture.createPrimaryHolder(PRIMARY_ACCOUNT_HOLDER, ACCOUNT_CREATED_AT);

    var bankAccount = BankAccount.restore(
      BankAccountId.newId(),
      ACCOUNT_TYPE_SAVINGS,
      CURRENCY_USD,
      VALID_IBAN,
      status,
      ACCOUNT_CREATED_AT,
      AccountHolders.of(primaryAccountHolder)
    );

    assertThatThrownBy(bankAccount::activate)
      .isInstanceOf(InvalidBankAccountStateTransitionException.class);
  }
}