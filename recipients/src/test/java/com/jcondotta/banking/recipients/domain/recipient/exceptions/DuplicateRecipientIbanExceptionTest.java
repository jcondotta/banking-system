package com.jcondotta.banking.recipients.domain.recipient.exceptions;

import com.jcondotta.banking.recipients.domain.common.FailureReason;
import com.jcondotta.banking.recipients.domain.recipient.identity.BankAccountId;
import com.jcondotta.banking.recipients.domain.recipient.value_objects.Iban;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class DuplicateRecipientIbanExceptionTest {

  private static final BankAccountId BANK_ACCOUNT_ID = BankAccountId.of(UUID.randomUUID());

  private static final Iban IBAN_JEFFERSON = Iban.of("IS558149818238357257726392");

  @Test
  void shouldMaskIbanAndExposeBankAccountId_whenExceptionIsCreated() {
    var exception = new DuplicateRecipientIbanException(IBAN_JEFFERSON, BANK_ACCOUNT_ID);

    assertThat(exception).hasMessage(DuplicateRecipientIbanException.MESSAGE);
    assertThat(exception.reason()).isEqualTo(FailureReason.DUPLICATE_IBAN);
    assertThat(exception.getMaskedIban()).isEqualTo("IS55****6392");
    assertThat(exception.getBankAccountId()).isEqualTo(BANK_ACCOUNT_ID.value().toString());
  }
}
