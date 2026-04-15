package com.jcondotta.banking.recipients.domain.recipient.exceptions;

import com.jcondotta.banking.recipients.domain.bankaccount.testsupport.RecipientTestData;
import com.jcondotta.banking.recipients.domain.recipient.value_objects.Iban;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DuplicateRecipientIbanExceptionTest {

  private static final Iban IBAN_JEFFERSON = Iban.of(RecipientTestData.JEFFERSON.getIban());

  @Test
  void shouldCreateExceptionWithCorrectMessage_whenIbanIsProvided() {
    var exception = new DuplicateRecipientIbanException(IBAN_JEFFERSON.value());

    assertThat(exception)
      .isInstanceOf(RuntimeException.class)
      .hasMessage(
        DuplicateRecipientIbanException.RECIPIENT_WITH_IBAN_ALREADY_EXISTS.formatted(IBAN_JEFFERSON.value())
      );
  }
}
