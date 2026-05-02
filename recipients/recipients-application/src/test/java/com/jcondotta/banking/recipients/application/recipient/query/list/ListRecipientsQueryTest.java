package com.jcondotta.banking.recipients.application.recipient.query.list;

import com.jcondotta.application.query.PageRequest;
import com.jcondotta.banking.recipients.domain.recipient.identity.BankAccountId;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ListRecipientsQueryTest {

  private static final int ZERO = 0;
  private static final BankAccountId BANK_ACCOUNT_ID = BankAccountId.of(UUID.randomUUID());

  @Test
  void shouldCreateQuery_whenBankAccountIdIsProvided() {
    var pageRequest = new PageRequest(ZERO, 20);

    var query = new ListRecipientsQuery(BANK_ACCOUNT_ID, pageRequest);

    assertThat(query.bankAccountId()).isEqualTo(BANK_ACCOUNT_ID);
    assertThat(query.pageRequest()).isEqualTo(pageRequest);
  }

  @Test
  void shouldThrowException_whenBankAccountIdIsNull() {
    assertThatThrownBy(() -> new ListRecipientsQuery(null, new PageRequest(ZERO, 20)))
      .isInstanceOf(NullPointerException.class)
      .hasMessage(ListRecipientsQuery.BANK_ACCOUNT_ID_REQUIRED);
  }

  @Test
  void shouldThrowException_whenPageRequestIsNull() {
    assertThatThrownBy(() -> new ListRecipientsQuery(BANK_ACCOUNT_ID, null))
      .isInstanceOf(NullPointerException.class)
      .hasMessage(ListRecipientsQuery.PAGE_REQUEST_REQUIRED);
  }
}
