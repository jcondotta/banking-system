package com.jcondotta.banking.accounts.domain.bankaccount.aggregate;

import com.jcondotta.banking.accounts.domain.bankaccount.factory.ClockTestFactory;
import com.jcondotta.banking.accounts.domain.bankaccount.fixtures.AccountHolderFixtures;
import com.jcondotta.banking.accounts.domain.bankaccount.fixtures.BankAccountTestFixture;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class AccountHolderDeactivateTest {

  private static final Instant CREATED_AT = Instant.now(ClockTestFactory.FIXED_CLOCK);

  @Test
  void shouldDeactivateAccountHolder_whenAccountHolderIsActive() {
    var accountHolder = BankAccountTestFixture.createJointHolder(AccountHolderFixtures.JEFFERSON, CREATED_AT);
    accountHolder.deactivate();

    assertThat(accountHolder.isActive()).isFalse();
    assertThat(accountHolder.getDeactivatedAt()).isNotNull();
  }

  @Test
  void shouldKeepAccountHolderDeactivated_whenDeactivateIsCalledTwice() {
    var accountHolder = BankAccountTestFixture.createJointHolder(AccountHolderFixtures.JEFFERSON, CREATED_AT);
    accountHolder.deactivate();
    accountHolder.deactivate();

    assertThat(accountHolder.isActive()).isFalse();
    assertThat(accountHolder.getDeactivatedAt()).isNotNull();
  }
}