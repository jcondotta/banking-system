package com.jcondotta.banking.recipients.application.recipient.command.update;

import com.jcondotta.banking.recipients.domain.recipient.identity.BankAccountId;
import com.jcondotta.banking.recipients.domain.recipient.identity.RecipientId;
import com.jcondotta.banking.recipients.domain.recipient.value_objects.Iban;
import com.jcondotta.banking.recipients.domain.recipient.value_objects.RecipientName;
import com.jcondotta.banking.recipients.domain.testsupport.RecipientFixtures;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UpdateRecipientCommandTest {

  private static final BankAccountId BANK_ACCOUNT_ID = BankAccountId.of(UUID.randomUUID());
  private static final RecipientId RECIPIENT_ID = RecipientId.of(UUID.randomUUID());
  private static final RecipientName RECIPIENT_NAME = RecipientFixtures.JEFFERSON.toName();
  private static final Iban IBAN = RecipientFixtures.JEFFERSON.toIban();

  @Test
  void shouldCreateCommand_whenAllParamsAreValid() {
    var command = new UpdateRecipientCommand(BANK_ACCOUNT_ID, RECIPIENT_ID, RECIPIENT_NAME, IBAN);

    assertThat(command.bankAccountId()).isEqualTo(BANK_ACCOUNT_ID);
    assertThat(command.recipientId()).isEqualTo(RECIPIENT_ID);
    assertThat(command.recipientName()).isEqualTo(RECIPIENT_NAME);
    assertThat(command.iban()).isEqualTo(IBAN);
  }

  @Test
  void shouldThrowException_whenBankAccountIdIsNull() {
    assertThatThrownBy(() -> new UpdateRecipientCommand(null, RECIPIENT_ID, RECIPIENT_NAME, IBAN))
      .isInstanceOf(NullPointerException.class)
      .hasMessage(UpdateRecipientCommand.BANK_ACCOUNT_ID_REQUIRED);
  }

  @Test
  void shouldThrowException_whenRecipientIdIsNull() {
    assertThatThrownBy(() -> new UpdateRecipientCommand(BANK_ACCOUNT_ID, null, RECIPIENT_NAME, IBAN))
      .isInstanceOf(NullPointerException.class)
      .hasMessage(UpdateRecipientCommand.RECIPIENT_ID_REQUIRED);
  }

  @Test
  void shouldThrowException_whenRecipientNameIsNull() {
    assertThatThrownBy(() -> new UpdateRecipientCommand(BANK_ACCOUNT_ID, RECIPIENT_ID, null, IBAN))
      .isInstanceOf(NullPointerException.class)
      .hasMessage(UpdateRecipientCommand.RECIPIENT_NAME_REQUIRED);
  }

  @Test
  void shouldThrowException_whenIbanIsNull() {
    assertThatThrownBy(() -> new UpdateRecipientCommand(BANK_ACCOUNT_ID, RECIPIENT_ID, RECIPIENT_NAME, null))
      .isInstanceOf(NullPointerException.class)
      .hasMessage(UpdateRecipientCommand.IBAN_REQUIRED);
  }
}
