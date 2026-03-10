package com.jcondotta.banking.recipients.application.bankaccount.query.list_recipients;

import com.jcondotta.banking.recipients.application.bankaccount.query.mapper.RecipientSummaryMapper;
import com.jcondotta.banking.recipients.application.bankaccount.query.model.RecipientSummary;
import com.jcondotta.banking.recipients.domain.recipient.aggregate.Recipient;
import com.jcondotta.banking.recipients.domain.recipient.testsupport.RecipientFixtures;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RecipientSummaryMapperTest {

  private final RecipientSummaryMapper mapper =
    Mappers.getMapper(RecipientSummaryMapper.class);

  @Test
  void shouldMapRecipientSummary_whenRecipientIsProvided() {
    Recipient recipient = RecipientFixtures.JEFFERSON.toRecipient();

    RecipientSummary summary = mapper.toSummary(recipient);

    assertThat(summary).satisfies(result -> {
      assertThat(result.recipientId()).isEqualTo(recipient.getId().value());
      assertThat(result.recipientName()).isEqualTo(recipient.getRecipientName().value());
      assertThat(result.iban()).isEqualTo(recipient.getIban().value());
      assertThat(result.createdAt()).isEqualTo(recipient.getCreatedAt());
    });
  }

  @Test
  void shouldMapRecipientSummaryList_whenRecipientsAreProvided() {
    var recipients = List.of(
      RecipientFixtures.JEFFERSON.toRecipient(),
      RecipientFixtures.PATRIZIO.toRecipient(),
      RecipientFixtures.VIRGINIO.toRecipient()
    );

    List<RecipientSummary> summaries = mapper.toSummaryList(recipients);

    assertThat(summaries)
      .hasSize(3)
      .extracting(RecipientSummary::recipientName)
      .containsExactly(
        RecipientFixtures.JEFFERSON.toName().value(),
        RecipientFixtures.PATRIZIO.toName().value(),
        RecipientFixtures.VIRGINIO.toName().value()
      );
  }

  @Test
  void shouldReturnNull_whenRecipientIsNull() {
    assertThat(mapper.toSummary(null)).isNull();
  }

  @Test
  void shouldReturnEmptySummaryList_whenRecipientListIsEmpty() {
    var summaries = mapper.toSummaryList(List.of());

    assertThat(summaries).isEmpty();
  }

  @Test
  void shouldReturnNull_whenRecipientListIsNull() {
    var summaries = mapper.toSummaryList(null);

    assertThat(summaries).isNull();
  }
}