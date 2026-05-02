package com.jcondotta.banking.recipients.infrastructure.adapters.input.rest.list_recipients;

import com.jcondotta.application.query.QueryHandler;
import com.jcondotta.banking.recipients.application.recipient.query.list.ListRecipientsQuery;
import com.jcondotta.banking.recipients.application.recipient.query.list.ListRecipientsQueryResult;
import com.jcondotta.application.query.PageRequest;
import com.jcondotta.application.query.PageResult;
import com.jcondotta.banking.recipients.application.recipient.query.model.RecipientSummary;
import com.jcondotta.banking.recipients.domain.recipient.identity.BankAccountId;
import com.jcondotta.banking.recipients.domain.testsupport.RecipientTestData;
import com.jcondotta.banking.recipients.domain.testsupport.TimeFactory;
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
  private static final int PAGE = 1;
  private static final int SIZE = 10;
  private static final PageRequest PAGE_REQUEST = new PageRequest(PAGE, SIZE);
  private static final ListRecipientsQuery QUERY = new ListRecipientsQuery(BANK_ACCOUNT_ID, PAGE_REQUEST);

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
    var queryResult = new ListRecipientsQueryResult(new PageResult<>(List.of(
      new RecipientSummary(
        UUID.randomUUID(),
        BANK_ACCOUNT_ID.value(),
        recipientTestData.getName(),
        recipientTestData.getIban(),
        TimeFactory.FIXED_INSTANT
      )
    ), PAGE, SIZE, 1, 1));

    when(mapper.toQuery(BANK_ACCOUNT_ID.value(), PAGE, SIZE)).thenReturn(QUERY);
    when(queryHandler.handle(QUERY)).thenReturn(queryResult);

    var response = controller.listRecipients(BANK_ACCOUNT_ID.value(), PAGE, SIZE);

    assertThat(response.getStatusCode().value()).isEqualTo(200);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().recipients())
      .singleElement()
      .extracting(RecipientRestResponse::recipientName)
      .isEqualTo(recipientTestData.getName());
    assertThat(response.getBody().page()).isEqualTo(PAGE);
    assertThat(response.getBody().size()).isEqualTo(SIZE);
    assertThat(response.getBody().totalElements()).isEqualTo(1);
    assertThat(response.getBody().totalPages()).isEqualTo(1);

    verify(mapper).toQuery(BANK_ACCOUNT_ID.value(), PAGE, SIZE);
    verify(queryHandler).handle(QUERY);
    verifyNoMoreInteractions(mapper, queryHandler);
  }

  @Test
  void shouldReturnOkResponse_whenRecipientsAreEmpty() {
    when(mapper.toQuery(BANK_ACCOUNT_ID.value(), PAGE, SIZE)).thenReturn(QUERY);
    when(queryHandler.handle(QUERY)).thenReturn(new ListRecipientsQueryResult(new PageResult<>(List.of(), PAGE, SIZE, 0, 0)));

    var response = controller.listRecipients(BANK_ACCOUNT_ID.value(), PAGE, SIZE);

    assertThat(response.getStatusCode().value()).isEqualTo(200);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().recipients()).isEmpty();
    assertThat(response.getBody().page()).isEqualTo(PAGE);
    assertThat(response.getBody().size()).isEqualTo(SIZE);
    assertThat(response.getBody().totalElements()).isZero();
    assertThat(response.getBody().totalPages()).isZero();

    verify(mapper).toQuery(BANK_ACCOUNT_ID.value(), PAGE, SIZE);
    verify(queryHandler).handle(QUERY);
    verifyNoMoreInteractions(mapper, queryHandler);
  }
}
