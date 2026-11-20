package com.jcondotta.banking.accounts.application.bankaccount.command.block.model;

import com.jcondotta.banking.accounts.domain.bankaccount.identity.BankAccountId;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BlockBankAccountCommandTest {

  @Test
  void shouldCreateCommand_whenBankAccountIdIsProvided() {
    var bankAccountId = BankAccountId.newId();

    var command = new BlockBankAccountCommand(bankAccountId);

    assertThat(command.bankAccountId())
      .isEqualTo(bankAccountId);
  }

  @Test
  void shouldThrowNullPointerException_whenBankAccountIdIsNull() {
    assertThatThrownBy(() -> new BlockBankAccountCommand(null))
      .isInstanceOf(NullPointerException.class)
      .hasMessage(BlockBankAccountCommand.BANK_ACCOUNT_ID_REQUIRED);
  }
}