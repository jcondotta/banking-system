package com.jcondotta.banking.recipients.domain.recipient.exceptions;

import com.jcondotta.banking.recipients.domain.common.FailureReason;
import com.jcondotta.banking.recipients.domain.recipient.identity.RecipientId;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class RecipientAlreadyExistsExceptionTest {

  private static final RecipientId RECIPIENT_ID = RecipientId.of(UUID.randomUUID());

  @Test
  void shouldCreateExceptionWithExpectedMessage() {
    var exception = new RecipientAlreadyExistsException(RECIPIENT_ID);

    assertThat(exception)
      .hasMessage(
        RecipientAlreadyExistsException.RECIPIENT_ALREADY_EXISTS.formatted(RECIPIENT_ID.value())
      );
    assertThat(exception.reason()).isEqualTo(FailureReason.ALREADY_EXISTS);
  }
}
