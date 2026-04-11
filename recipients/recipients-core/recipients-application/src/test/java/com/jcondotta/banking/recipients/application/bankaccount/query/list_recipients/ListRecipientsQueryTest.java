package com.jcondotta.banking.recipients.application.bankaccount.query.list_recipients;

import com.jcondotta.banking.recipients.domain.recipient.identity.BankAccountId;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ListRecipientsQueryTest {

  @Test
  void shouldCreateQuery_whenBankAccountIdIsProvided() {
    var bankAccountId = BankAccountId.of(UUID.randomUUID());

    var query = new ListRecipientsQuery(bankAccountId);

    assertThat(query.bankAccountId()).isEqualTo(bankAccountId);
  }

  @Test
  void shouldThrowException_whenBankAccountIdIsNull() {
    assertThatThrownBy(() -> new ListRecipientsQuery(null))
      .isInstanceOf(NullPointerException.class)
      .hasMessage(ListRecipientsQuery.BANK_ACCOUNT_ID_REQUIRED);
  }
}