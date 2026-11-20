package com.jcondotta.banking.recipients.application.recipient.query.list;

import com.jcondotta.application.query.PageResult;
import com.jcondotta.banking.recipients.application.recipient.query.model.RecipientSummary;
import com.jcondotta.banking.recipients.domain.testsupport.RecipientTestData;
import com.jcondotta.banking.recipients.domain.testsupport.TimeFactory;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ListRecipientsQueryResultTest {

  private static final UUID RECIPIENT_ID = UUID.randomUUID();
  private static final UUID BANK_ACCOUNT_ID = UUID.randomUUID();

  private static final String RECIPIENT_NAME = RecipientTestData.JEFFERSON.getName();
  private static final String IBAN = RecipientTestData.JEFFERSON.getIban();

  private static final Instant CREATED_AT = TimeFactory.FIXED_INSTANT;

  @Test
  void shouldCreateResult_whenRecipientsAreProvided() {
    var recipientSummary = recipientSummary();
    var page = new PageResult<>(List.of(recipientSummary), 0, 20, 1, 1);

    var queryResult = new ListRecipientsQueryResult(page);

    assertThat(queryResult.recipients()).containsExactly(recipientSummary);
    assertThat(queryResult.page()).isEqualTo(page);
  }

  @Test
  void shouldThrowException_whenPageIsNull() {
    assertThatThrownBy(() -> new ListRecipientsQueryResult(null))
      .isInstanceOf(NullPointerException.class)
      .hasMessage(ListRecipientsQueryResult.PAGE_REQUIRED);
  }

  @Test
  void shouldCreateDefensiveCopy_whenRecipientsAreProvided() {
    var recipientSummary = recipientSummary();
    var recipients = new ArrayList<>(List.of(recipientSummary));

    var queryResult = new ListRecipientsQueryResult(new PageResult<>(recipients, 0, 20, 1, 1));

    recipients.clear();

    assertThat(queryResult.recipients()).containsExactly(recipientSummary);
  }

  @Test
  void shouldReturnImmutableRecipients_whenTryingToModifyResult() {
    var recipientSummary = recipientSummary();
    var queryResult = new ListRecipientsQueryResult(new PageResult<>(List.of(recipientSummary), 0, 20, 1, 1));

    assertThatThrownBy(() -> queryResult.recipients().clear())
      .isInstanceOf(UnsupportedOperationException.class);
  }

  private static RecipientSummary recipientSummary() {
    return new RecipientSummary(
      RECIPIENT_ID,
      RECIPIENT_NAME,
      IBAN,
      CREATED_AT
    );
  }
}
