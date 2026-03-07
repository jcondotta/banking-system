package com.jcondotta.banking.recipients.domain.recipient.exceptions;

import com.jcondotta.banking.recipients.domain.recipient.enums.AccountStatus;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BankAccountNotActiveExceptionTest {

  @Test
  void shouldCreateExceptionWithCorrectMessage_whenStatusIsProvided() {
    var status = AccountStatus.BLOCKED;

    var exception = new BankAccountNotActiveException(status);

    assertThat(exception)
      .isInstanceOf(RuntimeException.class)
      .hasMessage(
        BankAccountNotActiveException.BANK_ACCOUNT_MUST_BE_ACTIVE.formatted(status)
      );
  }
}