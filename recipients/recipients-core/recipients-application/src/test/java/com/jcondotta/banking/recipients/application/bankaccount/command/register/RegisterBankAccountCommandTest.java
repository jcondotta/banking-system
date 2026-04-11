package com.jcondotta.banking.recipients.application.bankaccount.command.register;

import com.jcondotta.banking.recipients.domain.recipient.identity.BankAccountId;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RegisterBankAccountCommandTest {

  private static final BankAccountId BANK_ACCOUNT_ID = BankAccountId.of(UUID.randomUUID());

  @Test
  void shouldCreateCommand_whenBankAccountIdIsValid() {
    var command = new RegisterBankAccountCommand(BANK_ACCOUNT_ID);

    assertThat(command.bankAccountId()).isEqualTo(BANK_ACCOUNT_ID);
  }

  @Test
  void shouldThrowException_whenBankAccountIdIsNull() {
    assertThatThrownBy(() -> new RegisterBankAccountCommand(null))
      .isInstanceOf(NullPointerException.class)
      .hasMessage(RegisterBankAccountCommand.BANK_ACCOUNT_ID_REQUIRED);
  }
}