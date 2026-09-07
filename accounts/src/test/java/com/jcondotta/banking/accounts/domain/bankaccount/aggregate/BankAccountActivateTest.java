package com.jcondotta.banking.accounts.domain.bankaccount.aggregate;

import com.jcondotta.banking.accounts.domain.bankaccount.enums.AccountStatus;
import com.jcondotta.banking.accounts.domain.bankaccount.enums.AccountType;
import com.jcondotta.banking.accounts.domain.bankaccount.enums.Currency;
import com.jcondotta.banking.accounts.domain.bankaccount.events.BankAccountActivatedEvent;
import com.jcondotta.banking.accounts.domain.bankaccount.exceptions.InvalidBankAccountStateTransitionException;
import com.jcondotta.banking.accounts.domain.bankaccount.validation.BankAccountErrors;
import com.jcondotta.banking.accounts.domain.testsupport.TimeTestFactory;
import com.jcondotta.banking.accounts.domain.bankaccount.fixtures.AccountHolderFixtures;
import com.jcondotta.banking.accounts.domain.bankaccount.fixtures.BankAccountTestFixture;
import com.jcondotta.banking.accounts.domain.bankaccount.identity.BankAccountId;
import com.jcondotta.banking.accounts.domain.bankaccount.value_objects.Iban;
import com.jcondotta.domain.exception.DomainValidationException;
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

  private static final Instant ACCOUNT_CREATED_AT = TimeTestFactory.FIXED_INSTANT;

  @Test
  void shouldActivateBankAccount_whenStatusIsPending() {
    var bankAccount = BankAccountTestFixture.openPendingAccount(PRIMARY_ACCOUNT_HOLDER);
    bankAccount.pullEvents();

    var activated = bankAccount.activate(VALID_IBAN);

    assertThat(activated.getAccountStatus().isActive()).isTrue();
    assertThat(activated.getIban()).contains(VALID_IBAN);

    var events = activated.pullEvents();

    assertThat(events)
      .hasSize(1)
      .singleElement()
      .isInstanceOfSatisfying(BankAccountActivatedEvent.class, event -> {
        assertThat(event.aggregateId()).isEqualTo(activated.getId());
        assertThat(event.iban()).isEqualTo(VALID_IBAN.value());
        assertThat(event.currency()).isEqualTo(BankAccountTestFixture.DEFAULT_CURRENCY);
        assertThat(event.occurredAt()).isNotNull();
      });
  }

  @Test
  void shouldNotThrowAnyException_whenActivateIsCalledTwice() {
    var bankAccount = BankAccountTestFixture.openPendingAccount(PRIMARY_ACCOUNT_HOLDER, ACCOUNT_TYPE_SAVINGS, CURRENCY_USD);

    var activated = bankAccount.activate(VALID_IBAN);
    var activatedAgain = activated.activate(VALID_IBAN);

    assertThat(activatedAgain).isSameAs(activated);
    assertThat(activatedAgain.getAccountStatus().isActive()).isTrue();
    assertThat(activatedAgain.getIban()).contains(VALID_IBAN);
  }

  @Test
  void shouldThrowDomainValidationException_whenIbanIsNull() {
    var bankAccount = BankAccountTestFixture.openActiveAccount(PRIMARY_ACCOUNT_HOLDER);

    assertThatThrownBy(() -> bankAccount.activate(null))
      .isInstanceOf(DomainValidationException.class)
      .hasMessage(BankAccountErrors.IBAN_MUST_BE_PROVIDED);
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

    assertThatThrownBy(() -> bankAccount.activate(VALID_IBAN))
      .isInstanceOf(InvalidBankAccountStateTransitionException.class);
  }
}
