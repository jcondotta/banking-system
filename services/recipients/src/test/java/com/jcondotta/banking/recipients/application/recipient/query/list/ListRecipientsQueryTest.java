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
  private static final ListRecipientsFilter FILTER = ListRecipientsFilter.none();

  @Test
  void shouldCreateQuery_whenRequiredValuesAreProvided() {
    var pageRequest = new PageRequest(ZERO, 20);

    var query = new ListRecipientsQuery(BANK_ACCOUNT_ID, pageRequest, FILTER);

    assertThat(query.bankAccountId()).isEqualTo(BANK_ACCOUNT_ID);
    assertThat(query.pageRequest()).isEqualTo(pageRequest);
    assertThat(query.filter()).isEqualTo(FILTER);
  }

  @Test
  void shouldCreateQuery_whenNameFilterIsProvided() {
    var pageRequest = new PageRequest(ZERO, 20);
    var filter = ListRecipientsFilter.byName("jef");

    var query = new ListRecipientsQuery(BANK_ACCOUNT_ID, pageRequest, filter);

    assertThat(query.filter()).isEqualTo(filter);
    assertThat(query.filter().name()).contains("jef");
  }

  @Test
  void shouldThrowException_whenBankAccountIdIsNull() {
    assertThatThrownBy(() -> new ListRecipientsQuery(null, new PageRequest(ZERO, 20), FILTER))
      .isInstanceOf(NullPointerException.class)
      .hasMessage(ListRecipientsQuery.BANK_ACCOUNT_ID_REQUIRED);
  }

  @Test
  void shouldThrowException_whenPageRequestIsNull() {
    assertThatThrownBy(() -> new ListRecipientsQuery(BANK_ACCOUNT_ID, null, FILTER))
      .isInstanceOf(NullPointerException.class)
      .hasMessage(ListRecipientsQuery.PAGE_REQUEST_REQUIRED);
  }

  @Test
  void shouldThrowException_whenFilterIsNull() {
    assertThatThrownBy(() -> new ListRecipientsQuery(BANK_ACCOUNT_ID, new PageRequest(ZERO, 20), null))
      .isInstanceOf(NullPointerException.class)
      .hasMessage(ListRecipientsQuery.FILTER_REQUIRED);
  }
}
