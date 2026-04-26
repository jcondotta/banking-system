package com.jcondotta.banking.recipients.domain.recipient.exceptions;

import com.jcondotta.banking.recipients.domain.recipient.identity.RecipientId;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RecipientAlreadyExistsExceptionTest {

  private static final RecipientId RECIPIENT_ID = RecipientId.newId();

  @Test
  void shouldCreateExceptionWithCorrectMessage_whenRecipientIdIsProvided() {
    var exception = new RecipientAlreadyExistsException(RECIPIENT_ID);

    assertThat(exception)
      .isInstanceOf(RuntimeException.class)
      .hasMessage(
        RecipientAlreadyExistsException.RECIPIENT_ALREADY_EXISTS.formatted(RECIPIENT_ID.value())
      );
  }
}
