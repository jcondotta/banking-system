package com.jcondotta.banking.recipients.domain.recipient.exceptions;

import com.jcondotta.banking.recipients.domain.recipient.identity.BankAccountId;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class BankAccountNotFoundExceptionTest {

  @Test
  void shouldCreateExceptionWithCorrectMessage_whenBankAccountIdIsProvided() {
    var bankAccountId = BankAccountId.of(UUID.randomUUID());

    var exception = new BankAccountNotFoundException(bankAccountId);

    assertThat(exception)
      .isInstanceOf(RuntimeException.class)
      .hasMessage(
        BankAccountNotFoundException.BANK_ACCOUNT_NOT_FOUND.formatted(bankAccountId)
      );
  }
}