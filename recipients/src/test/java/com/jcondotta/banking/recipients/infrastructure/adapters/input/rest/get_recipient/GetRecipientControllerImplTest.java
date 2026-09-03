package com.jcondotta.banking.recipients.infrastructure.adapters.input.rest.get_recipient;

import com.jcondotta.application.query.QueryHandler;
import com.jcondotta.banking.recipients.application.recipient.query.get.GetRecipientQuery;
import com.jcondotta.banking.recipients.application.recipient.query.get.GetRecipientQueryResult;
import com.jcondotta.banking.recipients.application.recipient.query.model.RecipientSummary;
import com.jcondotta.banking.recipients.domain.recipient.identity.BankAccountId;
import com.jcondotta.banking.recipients.domain.recipient.identity.RecipientId;
import com.jcondotta.banking.recipients.domain.testsupport.RecipientTestData;
import com.jcondotta.banking.recipients.domain.testsupport.TimeFactory;
import com.jcondotta.banking.recipients.infrastructure.adapters.input.rest.get_recipient.mapper.GetRecipientRestMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetRecipientControllerImplTest {

  private static final BankAccountId BANK_ACCOUNT_ID = BankAccountId.of(UUID.randomUUID());
  private static final RecipientId RECIPIENT_ID = RecipientId.newId();
  private static final GetRecipientQuery QUERY = new GetRecipientQuery(BANK_ACCOUNT_ID, RECIPIENT_ID);

  @Mock
  private QueryHandler<GetRecipientQuery, GetRecipientQueryResult> queryHandler;

  @Mock
  private GetRecipientRestMapper mapper;

  private GetRecipientControllerImpl controller;

  @BeforeEach
  void setUp() {
    controller = new GetRecipientControllerImpl(queryHandler, mapper);
  }

  @Test
  void shouldReturnOkResponse_whenRecipientIsFound() {
    var summary = new RecipientSummary(
      RECIPIENT_ID.value(),
      RecipientTestData.JEFFERSON.getName(),
      RecipientTestData.JEFFERSON.getIban(),
      TimeFactory.FIXED_INSTANT
    );

    when(mapper.toQuery(BANK_ACCOUNT_ID.value(), RECIPIENT_ID.value())).thenReturn(QUERY);
    when(queryHandler.handle(QUERY)).thenReturn(new GetRecipientQueryResult(summary));

    var response = controller.getRecipient(BANK_ACCOUNT_ID.value(), RECIPIENT_ID.value());

    assertThat(response.getStatusCode().value()).isEqualTo(200);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().recipientId()).isEqualTo(RECIPIENT_ID.value());
    assertThat(response.getBody().recipientName()).isEqualTo(RecipientTestData.JEFFERSON.getName());
    assertThat(response.getBody().iban()).isEqualTo(RecipientTestData.JEFFERSON.getIban());
    assertThat(response.getBody().createdAt()).isEqualTo(TimeFactory.FIXED_INSTANT);

    verify(mapper).toQuery(BANK_ACCOUNT_ID.value(), RECIPIENT_ID.value());
    verify(queryHandler).handle(QUERY);
    verifyNoMoreInteractions(mapper, queryHandler);
  }
}
