package com.jcondotta.banking.recipients.application.bankaccount.command.create_recipient;

import com.jcondotta.banking.recipients.domain.recipient.identity.BankAccountId;
import com.jcondotta.banking.recipients.domain.bankaccount.testsupport.RecipientFixtures;
import com.jcondotta.banking.recipients.domain.recipient.value_objects.Iban;
import com.jcondotta.banking.recipients.domain.recipient.value_objects.RecipientName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CreateRecipientCommandTest {

  private static final BankAccountId BANK_ACCOUNT_ID = BankAccountId.of(UUID.randomUUID());

  private static final RecipientName RECIPIENT_NAME = RecipientFixtures.JEFFERSON.toName();
  private static final Iban VALID_IBAN = RecipientFixtures.JEFFERSON.toIban();

  @Test
  void shouldCreateCommand_whenAllParamsAreValid() {
    var command = new CreateRecipientCommand(BANK_ACCOUNT_ID, RECIPIENT_NAME, VALID_IBAN);

    assertThat(command.bankAccountId()).isEqualTo(BANK_ACCOUNT_ID);
    assertThat(command.recipientName()).isEqualTo(RECIPIENT_NAME);
    assertThat(command.iban()).isEqualTo(VALID_IBAN);
  }

  @Test
  void shouldThrowException_whenBankAccountIdIsNull() {
    assertThatThrownBy(() -> new CreateRecipientCommand(null, RECIPIENT_NAME, VALID_IBAN))
      .isInstanceOf(NullPointerException.class)
      .hasMessage(CreateRecipientCommand.BANK_ACCOUNT_ID_REQUIRED);
  }

  @Test
  void shouldThrowException_whenRecipientNameIsNull() {
    assertThatThrownBy(() -> new CreateRecipientCommand(BANK_ACCOUNT_ID, null, VALID_IBAN))
      .isInstanceOf(NullPointerException.class)
      .hasMessage(CreateRecipientCommand.RECIPIENT_NAME_REQUIRED);
  }

  @Test
  void shouldThrowException_whenIbanIsNull() {
    assertThatThrownBy(() -> new CreateRecipientCommand(BANK_ACCOUNT_ID, RECIPIENT_NAME, null))
      .isInstanceOf(NullPointerException.class)
      .hasMessage(CreateRecipientCommand.IBAN_REQUIRED);
  }
}