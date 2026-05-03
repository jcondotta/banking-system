package com.jcondotta.banking.recipients.infrastructure.adapters.input.rest.list_recipients;

import com.jcondotta.application.query.PageResult;
import com.jcondotta.banking.recipients.application.recipient.query.list.ListRecipientsQueryResult;
import com.jcondotta.banking.recipients.application.recipient.query.model.RecipientSummary;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertAll;

class ListRecipientsResponseTest {

  private static final UUID RECIPIENT_ID = UUID.randomUUID();
  private static final UUID BANK_ACCOUNT_ID = UUID.randomUUID();
  private static final Instant CREATED_AT = Instant.parse("2026-01-01T00:00:00Z");

  @Test
  void shouldCreateResponse_whenQueryResultIsProvided() {
    var queryResult = new ListRecipientsQueryResult(new PageResult<>(List.of(summary()), 1, 10, 21, 3));

    var response = ListRecipientsResponse.from(queryResult);

    assertThat(response.recipients())
      .singleElement()
      .satisfies(recipient -> assertAll(
        () -> assertThat(recipient.recipientId()).isEqualTo(RECIPIENT_ID),
        () -> assertThat(recipient.recipientName()).isEqualTo("Jefferson Condotta"),
        () -> assertThat(recipient.maskedIban()).isEqualTo("ES3801283316232166447417"),
        () -> assertThat(recipient.createdAt()).isEqualTo(CREATED_AT)
      ));
    assertThat(response.page()).isEqualTo(1);
    assertThat(response.size()).isEqualTo(10);
    assertThat(response.totalElements()).isEqualTo(21);
    assertThat(response.totalPages()).isEqualTo(3);
    assertThat(response.hasNext()).isTrue();
    assertThat(response.hasPrevious()).isTrue();
  }

  @Test
  void shouldCreateImmutableRecipientsList_whenRecipientsAreProvided() {
    var recipients = new ArrayList<RecipientRestResponse>();
    recipients.add(RecipientRestResponse.from(summary()));

    var response = new ListRecipientsResponse(recipients, 0, 20, 1, 1, false, false);
    recipients.clear();

    assertThat(response.recipients()).hasSize(1);
    assertThatExceptionOfType(UnsupportedOperationException.class)
      .isThrownBy(() -> response.recipients().add(RecipientRestResponse.from(summary())));
  }

  private static RecipientSummary summary() {
    return new RecipientSummary(
      RECIPIENT_ID,
      "Jefferson Condotta",
      "ES3801283316232166447417",
      CREATED_AT
    );
  }
}
