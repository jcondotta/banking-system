package com.jcondotta.banking.accounts.application.bankaccount.command.unblock.model;

import com.jcondotta.banking.accounts.domain.bankaccount.identity.BankAccountId;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UnblockBankAccountCommandTest {

  @Test
  void shouldCreateCommandWithBankAccountId() {
    var bankAccountId = BankAccountId.newId();

    var command = new UnblockBankAccountCommand(bankAccountId);

    assertThat(command.bankAccountId()).isEqualTo(bankAccountId);
  }

  @Test
  void shouldThrowNullPointerException_whenBankAccountIdIsNull() {
    assertThatThrownBy(() -> new UnblockBankAccountCommand(null))
      .isInstanceOf(NullPointerException.class)
      .hasMessage(UnblockBankAccountCommand.BANK_ACCOUNT_ID_REQUIRED);
  }
}