package com.jcondotta.banking.recipients.domain.recipient.exceptions;

import com.jcondotta.banking.recipients.domain.recipient.identity.RecipientId;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RecipientNotFoundExceptionTest {

  @Test
  void shouldCreateExceptionWithCorrectMessage_whenRecipientIdIsProvided() {
    var recipientId = RecipientId.newId();

    var exception = new RecipientNotFoundException(recipientId);

    assertThat(exception)
      .isInstanceOf(RuntimeException.class)
      .hasMessage(
        RecipientNotFoundException.RECIPIENT_NOT_FOUND.formatted(recipientId)
      );
  }
}