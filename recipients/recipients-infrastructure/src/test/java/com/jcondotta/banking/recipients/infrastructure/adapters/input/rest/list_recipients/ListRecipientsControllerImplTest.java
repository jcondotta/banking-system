package com.jcondotta.banking.recipients.infrastructure.adapters.input.rest.list_recipients;

import com.jcondotta.application.query.QueryHandler;
import com.jcondotta.banking.recipients.application.bankaccount.query.list_recipients.ListRecipientsQuery;
import com.jcondotta.banking.recipients.application.bankaccount.query.list_recipients.ListRecipientsQueryResult;
import com.jcondotta.banking.recipients.application.bankaccount.query.model.RecipientSummary;
import com.jcondotta.banking.recipients.domain.recipient.identity.BankAccountId;
import com.jcondotta.banking.recipients.domain.testsupport.ClockTestFactory;
import com.jcondotta.banking.recipients.domain.testsupport.RecipientTestData;
import com.jcondotta.banking.recipients.infrastructure.adapters.input.rest.list_recipients.mapper.ListRecipientsRestMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListRecipientsControllerImplTest {

  private static final BankAccountId BANK_ACCOUNT_ID = BankAccountId.of(UUID.randomUUID());
  private static final ListRecipientsQuery QUERY = new ListRecipientsQuery(BANK_ACCOUNT_ID);

  @Mock
  private QueryHandler<ListRecipientsQuery, ListRecipientsQueryResult> queryHandler;

  @Mock
  private ListRecipientsRestMapper mapper;

  private ListRecipientsControllerImpl controller;

  @BeforeEach
  void setUp() {
    controller = new ListRecipientsControllerImpl(queryHandler, mapper);
  }

  @Test
  void shouldReturnOkResponse_whenRecipientsAreFound() {
    var recipientTestData = RecipientTestData.JEFFERSON;
    var queryResult = new ListRecipientsQueryResult(List.of(
      new RecipientSummary(
        UUID.randomUUID(),
        BANK_ACCOUNT_ID.value(),
        recipientTestData.getName(),
        recipientTestData.getIban(),
        ClockTestFactory.FIXED_CLOCK.instant()
      )
    ));

    when(mapper.toQuery(BANK_ACCOUNT_ID.value())).thenReturn(QUERY);
    when(queryHandler.handle(QUERY)).thenReturn(queryResult);

    var response = controller.listRecipients(BANK_ACCOUNT_ID.value());

    assertThat(response.getStatusCode().value()).isEqualTo(200);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().recipients())
      .singleElement()
      .extracting(RecipientRestResponse::recipientName)
      .isEqualTo(recipientTestData.getName());

    verify(mapper).toQuery(BANK_ACCOUNT_ID.value());
    verify(queryHandler).handle(QUERY);
    verifyNoMoreInteractions(mapper, queryHandler);
  }

  @Test
  void shouldReturnOkResponse_whenRecipientsAreEmpty() {
    when(mapper.toQuery(BANK_ACCOUNT_ID.value())).thenReturn(QUERY);
    when(queryHandler.handle(QUERY)).thenReturn(new ListRecipientsQueryResult(List.of()));

    var response = controller.listRecipients(BANK_ACCOUNT_ID.value());

    assertThat(response.getStatusCode().value()).isEqualTo(200);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().recipients()).isEmpty();

    verify(mapper).toQuery(BANK_ACCOUNT_ID.value());
    verify(queryHandler).handle(QUERY);
    verifyNoMoreInteractions(mapper, queryHandler);
  }
}
