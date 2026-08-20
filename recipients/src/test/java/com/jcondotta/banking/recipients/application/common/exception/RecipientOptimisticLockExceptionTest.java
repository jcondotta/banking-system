package com.jcondotta.banking.recipients.application.common.exception;

import com.jcondotta.banking.recipients.domain.common.FailureReason;
import com.jcondotta.banking.recipients.domain.recipient.identity.RecipientId;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class RecipientOptimisticLockExceptionTest {

  @Test
  void shouldCreateExceptionWithRecipientId() {
    var recipientId = RecipientId.of(UUID.randomUUID());

    var exception = new RecipientOptimisticLockException(recipientId);

    assertThat(exception).hasMessage(RecipientOptimisticLockException.RECIPIENT_CONCURRENT_MODIFICATION);
    assertThat(exception.reason()).isEqualTo(FailureReason.OPTIMISTIC_LOCK_CONFLICT);
    assertThat(exception.getRecipientId()).isEqualTo(recipientId.value());
  }
}
