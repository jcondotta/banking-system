package com.jcondotta.banking.recipients.infrastructure.adapters.input.rest.list_recipients;

import com.jcondotta.application.query.QueryHandler;
import com.jcondotta.banking.recipients.application.bankaccount.query.list_recipients.ListRecipientsQuery;
import com.jcondotta.banking.recipients.application.bankaccount.query.list_recipients.ListRecipientsQueryResult;
import com.jcondotta.banking.recipients.application.bankaccount.query.model.RecipientSummary;
import com.jcondotta.banking.recipients.domain.recipient.identity.BankAccountId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListRecipientsControllerImplTest {

  private static final BankAccountId BANK_ACCOUNT_ID = BankAccountId.of(UUID.randomUUID());

  @Mock
  private QueryHandler<ListRecipientsQuery, ListRecipientsQueryResult> queryHandler;

  @Captor
  private ArgumentCaptor<ListRecipientsQuery> queryCaptor;

  private ListRecipientsControllerImpl controller;

  @BeforeEach
  void setUp() {
    controller = new ListRecipientsControllerImpl(queryHandler);
  }

  @Test
  void shouldReturnOkResponse_whenRecipientsAreFound() {
    var queryResult = new ListRecipientsQueryResult(List.of(
      new RecipientSummary(
        UUID.randomUUID(),
        "Jefferson Condotta",
        "ES3801283316232166447417",
        Instant.parse("2026-01-01T00:00:00Z")
      )
    ));

    when(queryHandler.handle(queryCaptor.capture())).thenReturn(queryResult);

    var response = controller.listRecipients(BANK_ACCOUNT_ID.value());

    assertThat(response.getStatusCode().value()).isEqualTo(200);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().recipients())
      .singleElement()
      .extracting(RecipientRestResponse::recipientName)
      .isEqualTo("Jefferson Condotta");
    assertThat(queryCaptor.getValue().bankAccountId()).isEqualTo(BANK_ACCOUNT_ID);
  }

  @Test
  void shouldReturnOkResponse_whenRecipientsAreEmpty() {
    when(queryHandler.handle(queryCaptor.capture())).thenReturn(new ListRecipientsQueryResult(List.of()));

    var response = controller.listRecipients(BANK_ACCOUNT_ID.value());

    assertThat(response.getStatusCode().value()).isEqualTo(200);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().recipients()).isEmpty();
    assertThat(queryCaptor.getValue().bankAccountId()).isEqualTo(BANK_ACCOUNT_ID);
    verify(queryHandler).handle(queryCaptor.getValue());
  }
}
