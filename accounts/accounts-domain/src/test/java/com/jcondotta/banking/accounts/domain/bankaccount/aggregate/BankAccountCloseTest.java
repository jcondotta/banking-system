package com.jcondotta.banking.accounts.domain.bankaccount.aggregate;

import com.jcondotta.banking.accounts.domain.bankaccount.enums.AccountStatus;
import com.jcondotta.banking.accounts.domain.bankaccount.enums.AccountType;
import com.jcondotta.banking.accounts.domain.bankaccount.enums.Currency;
import com.jcondotta.banking.accounts.domain.bankaccount.events.BankAccountStatusChangedEvent;
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

class BankAccountCloseTest {

  private static final AccountHolderFixtures PRIMARY_ACCOUNT_HOLDER = AccountHolderFixtures.JEFFERSON;

  private static final Iban VALID_IBAN = BankAccountTestFixture.VALID_IBAN;
  private static final AccountType ACCOUNT_TYPE_SAVINGS = AccountType.SAVINGS;
  private static final Currency CURRENCY_USD = Currency.USD;

  private static final Instant ACCOUNT_CREATED_AT = Instant.now(ClockTestFactory.FIXED_CLOCK);

  @Test
  void shouldCloseBankAccount_whenStatusIsActive() {
    var bankAccount = BankAccountTestFixture.openActiveAccount(PRIMARY_ACCOUNT_HOLDER);
    bankAccount.pullEvents();

    bankAccount.close();

    assertThat(bankAccount.getAccountStatus().isClosed()).isTrue();

    var events = bankAccount.pullEvents();

    assertThat(events)
      .hasSize(1)
      .singleElement()
      .isInstanceOfSatisfying(BankAccountStatusChangedEvent.class, event -> {
        assertThat(event.eventId()).isNotNull();
        assertThat(event.aggregateId()).isEqualTo(bankAccount.getId());
        assertThat(event.previousStatus()).isEqualTo(AccountStatus.ACTIVE);
        assertThat(event.currentStatus()).isEqualTo(AccountStatus.CLOSED);
        assertThat(event.occurredAt()).isNotNull();
      });
  }

  @Test
  void shouldNotThrowAnyException_whenCloseIsCalledTwice() {
    var bankAccount = BankAccountTestFixture.openActiveAccount(PRIMARY_ACCOUNT_HOLDER);

    bankAccount.close();
    bankAccount.close();

    assertThat(bankAccount.getAccountStatus().isClosed()).isTrue();
  }

  @ParameterizedTest
  @EnumSource(value = AccountStatus.class, names = {"ACTIVE", "CLOSED"}, mode = EnumSource.Mode.EXCLUDE)
  void shouldThrowInvalidBankAccountStateTransitionException_whenClosingFromInvalidState(AccountStatus status) {
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

    assertThatThrownBy(bankAccount::close)
      .isInstanceOf(InvalidBankAccountStateTransitionException.class);
  }
}
