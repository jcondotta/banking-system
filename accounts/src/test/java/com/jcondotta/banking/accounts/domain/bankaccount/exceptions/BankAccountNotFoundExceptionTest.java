package com.jcondotta.banking.accounts.domain.bankaccount.exceptions;

import com.jcondotta.banking.accounts.domain.bankaccount.identity.BankAccountId;
import com.jcondotta.domain.exception.DomainNotFoundException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BankAccountNotFoundExceptionTest {

  @Test
  void shouldCreateBankAccountNotFoundException_whenBankAccountIdIsValid() {
    var bankAccountId = BankAccountId.newId();
    var exception = new BankAccountNotFoundException(bankAccountId);

    assertThat(exception)
      .isInstanceOf(DomainNotFoundException.class)
      .hasMessage("Bank account not found with id: " + bankAccountId.value());
  }
}
