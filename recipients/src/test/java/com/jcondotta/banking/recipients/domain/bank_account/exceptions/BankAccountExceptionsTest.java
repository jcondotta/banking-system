package com.jcondotta.banking.recipients.domain.bank_account.exceptions;

import com.jcondotta.banking.recipients.domain.bank_account.enums.BankAccountStatus;
import com.jcondotta.banking.recipients.domain.recipient.identity.BankAccountId;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class BankAccountExceptionsTest {

  @Test
  void shouldExposeBankAccountId_whenAccountIsNotFound() {
    var bankAccountId = BankAccountId.of(UUID.randomUUID());
    var exception = new BankAccountNotFoundException(bankAccountId);

    assertThat(exception.getMessage()).isEqualTo(BankAccountNotFoundException.MESSAGE);
    assertThat(exception.getBankAccountId()).isEqualTo(bankAccountId);
  }

  @Test
  void shouldExposeStatus_whenAccountIsNotActive() {
    var exception = new BankAccountNotActiveException(BankAccountStatus.BLOCKED);

    assertThat(exception.getMessage()).isEqualTo(BankAccountNotActiveException.MESSAGE);
    assertThat(exception.getStatus()).isEqualTo(BankAccountStatus.BLOCKED);
  }
}
