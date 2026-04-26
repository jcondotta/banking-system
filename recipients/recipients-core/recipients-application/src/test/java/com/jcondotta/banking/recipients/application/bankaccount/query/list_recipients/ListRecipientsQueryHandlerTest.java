package com.jcondotta.banking.recipients.application.bankaccount.query.list_recipients;

import com.jcondotta.banking.recipients.application.bankaccount.query.model.RecipientSummary;
import com.jcondotta.banking.recipients.domain.recipient.identity.BankAccountId;
import com.jcondotta.banking.recipients.domain.testsupport.ClockTestFactory;
import com.jcondotta.banking.recipients.domain.testsupport.RecipientTestData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListRecipientsQueryHandlerTest {

  private static final UUID RECIPIENT_ID_JEFFERSON = UUID.randomUUID();
  private static final UUID RECIPIENT_ID_PATRIZIO = UUID.randomUUID();

  private static final BankAccountId BANK_ACCOUNT_ID = BankAccountId.of(UUID.randomUUID());

  private static final Instant CREATED_AT = ClockTestFactory.FIXED_CLOCK.instant();

  @Mock
  private RecipientQueryRepository queryRepository;

  @InjectMocks
  private ListRecipientsQueryHandler handler;

  @Test
  void shouldReturnRecipients_whenBankAccountHasRecipients() {
    var recipientsSummary = List.of(
      recipientSummary(RECIPIENT_ID_JEFFERSON, RecipientTestData.JEFFERSON),
      recipientSummary(RECIPIENT_ID_PATRIZIO, RecipientTestData.PATRIZIO)
    );

    when(queryRepository.findByBankAccountId(BANK_ACCOUNT_ID))
      .thenReturn(recipientsSummary);

    var query = new ListRecipientsQuery(BANK_ACCOUNT_ID);
    var queryResult = handler.handle(query);

    assertThat(queryResult.recipients()).containsExactlyElementsOf(recipientsSummary);

    verify(queryRepository).findByBankAccountId(BANK_ACCOUNT_ID);
  }

  @Test
  void shouldReturnEmptyList_whenBankAccountHasNoRecipients() {
    when(queryRepository.findByBankAccountId(BANK_ACCOUNT_ID))
      .thenReturn(List.of());

    var query = new ListRecipientsQuery(BANK_ACCOUNT_ID);
    var queryResult = handler.handle(query);

    assertThat(queryResult.recipients()).isEmpty();

    verify(queryRepository).findByBankAccountId(BANK_ACCOUNT_ID);
  }

  private static RecipientSummary recipientSummary(UUID recipientId, RecipientTestData recipientTestData) {
    return new RecipientSummary(
      recipientId,
      BANK_ACCOUNT_ID.value(),
      recipientTestData.getName(),
      recipientTestData.getIban(),
      CREATED_AT
    );
  }
}
