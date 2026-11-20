package com.jcondotta.banking.accounts.domain.bankaccount.aggregate;

import com.jcondotta.banking.accounts.domain.bankaccount.enums.AccountStatus;
import com.jcondotta.banking.accounts.domain.bankaccount.enums.AccountType;
import com.jcondotta.banking.accounts.domain.bankaccount.enums.BankAccountStateChangeResult;
import com.jcondotta.banking.accounts.domain.bankaccount.enums.Currency;
import com.jcondotta.banking.accounts.domain.bankaccount.events.BankAccountStatusChangedEvent;
import com.jcondotta.banking.accounts.domain.bankaccount.exceptions.InvalidBankAccountStateTransitionException;
import com.jcondotta.banking.accounts.domain.testsupport.TimeTestFactory;
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

class BankAccountBlockTest {

  private static final AccountHolderFixtures PRIMARY_ACCOUNT_HOLDER = AccountHolderFixtures.JEFFERSON;

  private static final Iban VALID_IBAN = BankAccountTestFixture.VALID_IBAN;
  private static final AccountType ACCOUNT_TYPE_SAVINGS = AccountType.SAVINGS;
  private static final Currency CURRENCY_USD = Currency.USD;

  private static final Instant ACCOUNT_CREATED_AT = TimeTestFactory.FIXED_INSTANT;

  @Test
  void shouldBlockBankAccount_whenStatusIsActive() {
    var bankAccount = BankAccountTestFixture.openActiveAccount(PRIMARY_ACCOUNT_HOLDER);
    bankAccount.pullEvents();

    var result = bankAccount.block();

    assertThat(result).isEqualTo(BankAccountStateChangeResult.CHANGED);
    assertThat(bankAccount.getAccountStatus().isBlocked()).isTrue();

    var events = bankAccount.pullEvents();

    assertThat(events)
      .hasSize(1)
      .singleElement()
      .isInstanceOfSatisfying(BankAccountStatusChangedEvent.class, event -> {
        assertThat(event.aggregateId()).isEqualTo(bankAccount.getId());
        assertThat(event.previousStatus()).isEqualTo(AccountStatus.ACTIVE);
        assertThat(event.currentStatus()).isEqualTo(AccountStatus.BLOCKED);
        assertThat(event.occurredAt()).isNotNull();
      });
  }

  @Test
  void shouldReturnAlreadyInTargetStateAndNotRegisterAnotherEvent_whenBlockIsCalledTwice() {
    var bankAccount = BankAccountTestFixture.openActiveAccount(PRIMARY_ACCOUNT_HOLDER);
    bankAccount.pullEvents();

    var firstResult = bankAccount.block();
    var eventsAfterFirstBlock = bankAccount.pullEvents();
    var secondResult = bankAccount.block();
    var eventsAfterSecondBlock = bankAccount.pullEvents();

    assertThat(firstResult).isEqualTo(BankAccountStateChangeResult.CHANGED);
    assertThat(secondResult).isEqualTo(BankAccountStateChangeResult.ALREADY_IN_TARGET_STATE);
    assertThat(bankAccount.getAccountStatus().isBlocked()).isTrue();
    assertThat(eventsAfterFirstBlock).hasSize(1);
    assertThat(eventsAfterSecondBlock).isEmpty();
  }

  @ParameterizedTest
  @EnumSource(value = AccountStatus.class, names = {"ACTIVE", "BLOCKED"}, mode = EnumSource.Mode.EXCLUDE)
  void shouldThrowInvalidBankAccountStateTransitionException_whenBlockingFromInvalidState(AccountStatus status) {
    var primaryAccountHolder = BankAccountTestFixture.createPrimaryHolder(PRIMARY_ACCOUNT_HOLDER, ACCOUNT_CREATED_AT);
    var iban = status.isPending() ? null : VALID_IBAN;

    var bankAccount = BankAccount.restore(
      BankAccountId.newId(),
      ACCOUNT_TYPE_SAVINGS,
      CURRENCY_USD,
      iban,
      status,
      ACCOUNT_CREATED_AT,
      AccountHolders.of(primaryAccountHolder),
      0L
    );

    assertThatThrownBy(bankAccount::block)
      .isInstanceOf(InvalidBankAccountStateTransitionException.class);
  }
}
