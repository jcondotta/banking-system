package com.jcondotta.banking.accounts.domain.bankaccount.exceptions;

import com.jcondotta.banking.accounts.domain.bankaccount.enums.AccountStatus;
import com.jcondotta.domain.exception.DomainRuleValidationException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;

class BankAccountNotActiveExceptionTest {

  @ParameterizedTest
  @EnumSource(value = AccountStatus.class, names = "ACTIVE", mode = EnumSource.Mode.EXCLUDE)
  void shouldCreateBankAccountNotActiveException_whenStatusIsNotActive(AccountStatus accountStatus) {
    var exception = new BankAccountNotActiveException(accountStatus);

    assertThat(exception)
      .isInstanceOf(DomainRuleValidationException.class)
      .hasMessage(BankAccountNotActiveException.BANK_ACCOUNT_MUST_BE_ACTIVE.formatted(accountStatus.name()));
  }
}