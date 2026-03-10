package com.jcondotta.banking.recipients.application.bankaccount.query.list_recipients;

import com.jcondotta.banking.recipients.application.bankaccount.query.model.RecipientSummary;
import com.jcondotta.banking.recipients.domain.recipient.testsupport.RecipientFixtures;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ListRecipientsQueryResultTest {

  @Test
  void shouldCreateResult_whenRecipientsAreProvided() {
    var recipient = RecipientFixtures.JEFFERSON.toRecipient();

    var recipientSummary = new RecipientSummary(
      UUID.randomUUID(),
      recipient.getRecipientName().value(),
      recipient.getIban().value(),
      Instant.now()
    );

    var queryResult = new ListRecipientsQueryResult(List.of(recipientSummary));

    assertThat(queryResult.recipients())
      .hasSize(1)
      .containsExactly(recipientSummary);
  }

  @Test
  void shouldThrowException_whenRecipientsIsNull() {
    assertThatThrownBy(() -> new ListRecipientsQueryResult(null))
      .isInstanceOf(NullPointerException.class)
      .hasMessage(ListRecipientsQueryResult.RECIPIENTS_REQUIRED);
  }
}