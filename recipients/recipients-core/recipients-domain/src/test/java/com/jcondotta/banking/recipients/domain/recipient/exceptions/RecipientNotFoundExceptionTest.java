package com.jcondotta.banking.recipients.domain.recipient.exceptions;

import com.jcondotta.banking.recipients.domain.common.FailureReason;
import com.jcondotta.banking.recipients.domain.recipient.identity.BankAccountId;
import com.jcondotta.banking.recipients.domain.recipient.identity.RecipientId;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class RecipientNotFoundExceptionTest {

  @Test
  void shouldCreateExceptionWithRecipientIdAndBankAccountId() {
    var recipientId = RecipientId.of(UUID.randomUUID());
    var bankAccountId = BankAccountId.of(UUID.randomUUID());

    var exception = new RecipientNotFoundException(recipientId, bankAccountId);

    assertThat(exception)
      .hasMessage(RecipientNotFoundException.MESSAGE);
    assertThat(exception.reason()).isEqualTo(FailureReason.NOT_FOUND);
    assertThat(exception.getRecipientId()).isEqualTo(recipientId.value().toString());
    assertThat(exception.getBankAccountId()).isEqualTo(bankAccountId.value().toString());
  }
}
