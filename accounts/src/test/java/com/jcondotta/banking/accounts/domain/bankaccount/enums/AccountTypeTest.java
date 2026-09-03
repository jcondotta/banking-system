package com.jcondotta.banking.accounts.domain.bankaccount.enums;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AccountTypeTest {

  @Test
  void shouldIdentifySavingsAccountType_whenTypeIsSaving() {
    assertThat(AccountType.SAVINGS.isSavings()).isTrue();
    assertThat(AccountType.CHECKING.isSavings()).isFalse();
  }

  @Test
  void shouldIdentifyCheckingAccountType_whenTypeIsChecking() {
    assertThat(AccountType.CHECKING.isChecking()).isTrue();
    assertThat(AccountType.SAVINGS.isChecking()).isFalse();
  }
}