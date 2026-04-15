package com.jcondotta.banking.recipients.application.bankaccount.query.list_recipients;

import com.jcondotta.banking.recipients.application.bankaccount.query.model.RecipientSummary;
import com.jcondotta.banking.recipients.domain.bankaccount.testsupport.RecipientFixtures;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
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

  @Test
  void shouldCreateImmutableResult_whenRecipientsAreProvided() {
    var recipient = RecipientFixtures.JEFFERSON.toRecipient();
    var recipientSummary = new RecipientSummary(
      recipient.getId().value(),
      recipient.getRecipientName().value(),
      recipient.getIban().value(),
      recipient.getCreatedAt()
    );
    var recipients = new ArrayList<>(List.of(recipientSummary));

    var queryResult = new ListRecipientsQueryResult(recipients);
    recipients.clear();

    assertThat(queryResult.recipients()).containsExactly(recipientSummary);
    assertThatThrownBy(() -> queryResult.recipients().clear())
      .isInstanceOf(UnsupportedOperationException.class);
  }
}
