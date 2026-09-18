package com.jcondotta.banking.accounts.application.common.exception;

import com.jcondotta.banking.accounts.domain.bankaccount.identity.BankAccountId;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BankAccountOptimisticLockExceptionTest {

  @Test
  void shouldCreateExceptionWithBankAccountId() {
    var bankAccountId = BankAccountId.newId();

    var exception = new BankAccountOptimisticLockException(bankAccountId);

    assertThat(exception).hasMessage(BankAccountOptimisticLockException.BANK_ACCOUNT_CONCURRENT_MODIFICATION);
    assertThat(exception.getBankAccountId()).isEqualTo(bankAccountId.value());
  }
}
