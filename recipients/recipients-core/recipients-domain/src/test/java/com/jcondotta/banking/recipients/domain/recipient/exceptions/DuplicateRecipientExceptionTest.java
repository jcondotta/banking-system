package com.jcondotta.banking.recipients.domain.recipient.exceptions;

import com.jcondotta.banking.recipients.domain.recipient.fixtures.RecipientFixtures;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DuplicateRecipientExceptionTest {

  @Test
  void shouldCreateExceptionWithCorrectMessage_whenIbanIsProvided() {
    var iban = RecipientFixtures.JEFFERSON.toIban();

    var exception = new DuplicateRecipientException(iban);

    assertThat(exception)
      .isInstanceOf(RuntimeException.class)
      .hasMessage(
        DuplicateRecipientException.RECIPIENT_WITH_IBAN_ALREADY_EXISTS.formatted(iban.value())
      );
  }
}