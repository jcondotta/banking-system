package com.jcondotta.banking.recipients.application.bankaccount.query.list_recipients;

import com.jcondotta.banking.recipients.application.bankaccount.query.model.RecipientSummary;
import com.jcondotta.banking.recipients.domain.testsupport.ClockTestFactory;
import com.jcondotta.banking.recipients.domain.testsupport.RecipientTestData;
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

  private static final Instant CREATED_AT = ClockTestFactory.FIXED_CLOCK.instant();

  @Test
  void shouldCreateResult_whenRecipientsAreProvided() {
    var recipientSummary = recipientSummary();

    var queryResult = new ListRecipientsQueryResult(List.of(recipientSummary));

    assertThat(queryResult.recipients()).containsExactly(recipientSummary);
  }

  @Test
  void shouldThrowException_whenRecipientsIsNull() {
    assertThatThrownBy(() -> new ListRecipientsQueryResult(null))
      .isInstanceOf(NullPointerException.class)
      .hasMessage(ListRecipientsQueryResult.RECIPIENTS_REQUIRED);
  }

  @Test
  void shouldCreateDefensiveCopy_whenRecipientsAreProvided() {
    var recipientSummary = recipientSummary();
    var recipients = new ArrayList<>(List.of(recipientSummary));

    var queryResult = new ListRecipientsQueryResult(recipients);

    recipients.clear();

    assertThat(queryResult.recipients()).containsExactly(recipientSummary);
  }

  @Test
  void shouldReturnImmutableRecipients_whenTryingToModifyResult() {
    var recipientSummary = recipientSummary();
    var queryResult = new ListRecipientsQueryResult(List.of(recipientSummary));

    assertThatThrownBy(() -> queryResult.recipients().clear())
      .isInstanceOf(UnsupportedOperationException.class);
  }

  private static RecipientSummary recipientSummary() {
    return new RecipientSummary(
      RECIPIENT_ID,
      BANK_ACCOUNT_ID,
      RECIPIENT_NAME,
      IBAN,
      CREATED_AT
    );
  }
}
