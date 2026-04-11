package com.jcondotta.banking.recipients.application.bankaccount.command.remove_recipient;

import com.jcondotta.banking.recipients.domain.recipient.identity.BankAccountId;
import com.jcondotta.banking.recipients.domain.recipient.identity.RecipientId;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RemoveRecipientCommandTest {

  private static final BankAccountId BANK_ACCOUNT_ID = BankAccountId.of(UUID.randomUUID());
  private static final RecipientId RECIPIENT_ID = RecipientId.newId();

  @Test
  void shouldCreateCommand_whenAllParamsAreValid() {
    var command = new RemoveRecipientCommand(BANK_ACCOUNT_ID, RECIPIENT_ID);

    assertThat(command.bankAccountId()).isEqualTo(BANK_ACCOUNT_ID);
    assertThat(command.recipientId()).isEqualTo(RECIPIENT_ID);
  }

  @Test
  void shouldThrowException_whenBankAccountIdIsNull() {
    assertThatThrownBy(() -> new RemoveRecipientCommand(null, RECIPIENT_ID))
      .isInstanceOf(NullPointerException.class)
      .hasMessage(RemoveRecipientCommand.BANK_ACCOUNT_ID_REQUIRED);
  }

  @Test
  void shouldThrowException_whenRecipientIdIsNull() {
    assertThatThrownBy(() -> new RemoveRecipientCommand(BANK_ACCOUNT_ID, null))
      .isInstanceOf(NullPointerException.class)
      .hasMessage(RemoveRecipientCommand.RECIPIENT_ID_REQUIRED);
  }
}