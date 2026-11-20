package com.jcondotta.banking.accounts.application.bankaccount.command.activate.model;

import com.jcondotta.banking.accounts.domain.bankaccount.identity.BankAccountId;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ActivateBankAccountCommandTest {

  @Test
  void shouldCreateCommand_whenBankAccountIdIsProvided() {
    var bankAccountId = BankAccountId.newId();

    var command = new ActivateBankAccountCommand(bankAccountId);

    assertThat(command.bankAccountId())
      .isEqualTo(bankAccountId);
  }

  @Test
  void shouldThrowNullPointerException_whenBankAccountIdIsNull() {
    assertThatThrownBy(() -> new ActivateBankAccountCommand(null))
      .isInstanceOf(NullPointerException.class)
      .hasMessage(ActivateBankAccountCommand.BANK_ACCOUNT_ID_REQUIRED);
  }
}