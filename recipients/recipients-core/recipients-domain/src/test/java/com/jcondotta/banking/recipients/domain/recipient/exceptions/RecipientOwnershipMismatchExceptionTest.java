package com.jcondotta.banking.recipients.domain.recipient.exceptions;

import com.jcondotta.banking.recipients.domain.recipient.identity.BankAccountId;
import com.jcondotta.banking.recipients.domain.recipient.identity.RecipientId;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class RecipientOwnershipMismatchExceptionTest {

  @Test
  void shouldCreateExceptionWithCorrectMessage_whenRecipientIdAndBankAccountIdAreProvided() {
    var recipientId = RecipientId.newId();
    var bankAccountId = BankAccountId.of(UUID.randomUUID());

    var exception = new RecipientOwnershipMismatchException(recipientId, bankAccountId);

    assertThat(exception)
      .isInstanceOf(RuntimeException.class)
      .hasMessage(RecipientOwnershipMismatchException.MESSAGE);

    assertThat(exception.getRecipientId()).isEqualTo(recipientId.asString());
    assertThat(exception.getBankAccountId()).isEqualTo(bankAccountId.asString());
  }
}
