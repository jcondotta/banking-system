package com.jcondotta.banking.recipients.application.recipient.query.get;

import com.jcondotta.banking.recipients.application.recipient.query.model.RecipientSummary;
import com.jcondotta.banking.recipients.domain.testsupport.TimeFactory;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GetRecipientQueryResultTest {

  @Test
  void shouldCreateResult_whenRecipientIsProvided() {
    var summary = summary();

    var result = new GetRecipientQueryResult(summary);

    assertThat(result.recipient()).isEqualTo(summary);
  }

  @Test
  void shouldThrowException_whenRecipientIsNull() {
    assertThatThrownBy(() -> new GetRecipientQueryResult(null))
      .isInstanceOf(NullPointerException.class)
      .hasMessage(GetRecipientQueryResult.RECIPIENT_REQUIRED);
  }

  private static RecipientSummary summary() {
    return new RecipientSummary(
      UUID.randomUUID(),
      "Jefferson Condotta",
      "ES3801283316232166447417",
      TimeFactory.FIXED_INSTANT
    );
  }
}
