package com.jcondotta.banking.recipients.domain.recipient.exceptions;

import com.jcondotta.banking.recipients.domain.common.FailureReason;
import com.jcondotta.banking.recipients.domain.recipient.identity.BankAccountId;
import com.jcondotta.banking.recipients.domain.recipient.identity.RecipientId;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class RecipientOwnershipMismatchExceptionTest {

  @Test
  void shouldCreateExceptionWithRecipientIdAndBankAccountId() {
    var recipientId = RecipientId.of(UUID.randomUUID());
    var bankAccountId = BankAccountId.of(UUID.randomUUID());

    var exception = new RecipientOwnershipMismatchException(recipientId, bankAccountId);

    assertThat(exception)
      .hasMessage(RecipientOwnershipMismatchException.MESSAGE);
    assertThat(exception.reason()).isEqualTo(FailureReason.OWNERSHIP_MISMATCH);
    assertThat(exception.getRecipientId()).isEqualTo(recipientId.asString());
    assertThat(exception.getBankAccountId()).isEqualTo(bankAccountId.asString());
  }
}
