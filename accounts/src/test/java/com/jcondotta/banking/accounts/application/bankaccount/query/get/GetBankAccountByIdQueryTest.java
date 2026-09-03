package com.jcondotta.banking.accounts.application.bankaccount.query.get;

import com.jcondotta.banking.accounts.domain.bankaccount.identity.BankAccountId;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GetBankAccountByIdQueryTest {

  @Test
  void shouldCreateQuery_whenBankAccountIdIsProvided() {
    var bankAccountId = BankAccountId.newId();

    var query = new GetBankAccountByIdQuery(bankAccountId);

    assertThat(query.bankAccountId()).isEqualTo(bankAccountId);
  }

  @Test
  void shouldThrowException_whenBankAccountIdIsNull() {
    assertThatThrownBy(() -> new GetBankAccountByIdQuery(null))
      .isInstanceOf(NullPointerException.class)
      .hasMessage(GetBankAccountByIdQuery.BANK_ACCOUNT_ID_REQUIRED);
  }
}