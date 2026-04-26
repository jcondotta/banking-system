package com.jcondotta.banking.recipients.domain.recipient.exceptions;

import com.jcondotta.banking.recipients.domain.recipient.identity.BankAccountId;
import com.jcondotta.banking.recipients.domain.recipient.value_objects.Iban;
import com.jcondotta.banking.recipients.domain.testsupport.RecipientTestData;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class DuplicateRecipientIbanExceptionTest {

  private static final Iban IBAN_JEFFERSON = Iban.of(RecipientTestData.JEFFERSON.getIban());
  private static final BankAccountId BANK_ACCOUNT_ID = BankAccountId.of(UUID.randomUUID());

  @Test
  void shouldCreateExceptionWithCorrectMessage_whenIbanIsProvided() {
    var exception = new DuplicateRecipientIbanException(IBAN_JEFFERSON, BANK_ACCOUNT_ID);

    assertThat(exception)
      .isInstanceOf(RuntimeException.class)
      .hasMessage(DuplicateRecipientIbanException.MESSAGE);

    assertThat(exception.getMaskedIban()).isNotBlank();
    assertThat(exception.getBankAccountId()).isEqualTo(BANK_ACCOUNT_ID.value().toString());
  }
}
