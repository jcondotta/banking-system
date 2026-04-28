package com.jcondotta.banking.recipients.application.recipient.query.list;

import com.jcondotta.banking.recipients.application.recipient.query.model.RecipientSummary;
import com.jcondotta.banking.recipients.domain.recipient.exceptions.RecipientNotFoundException;
import com.jcondotta.banking.recipients.domain.recipient.identity.BankAccountId;
import com.jcondotta.banking.recipients.domain.recipient.identity.RecipientId;
import com.jcondotta.banking.recipients.domain.testsupport.ClockTestFactory;
import com.jcondotta.banking.recipients.domain.testsupport.RecipientTestData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListRecipientsQueryHandlerTest {

  private static final UUID RECIPIENT_ID_JEFFERSON = UUID.randomUUID();
  private static final UUID RECIPIENT_ID_PATRIZIO = UUID.randomUUID();

  private static final BankAccountId BANK_ACCOUNT_ID = BankAccountId.of(UUID.randomUUID());

  private static final Instant CREATED_AT = ClockTestFactory.FIXED_CLOCK.instant();

  @Mock
  private RecipientQueryRepository queryRepository;

  private ListRecipientsQueryHandler handler;

  @BeforeEach
  void setUp() {
    handler = new ListRecipientsQueryHandler(queryRepository);
  }

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

  @Test
  void shouldThrowDomainException_whenRepositoryThrowsDomainException() {
    var query = new ListRecipientsQuery(BANK_ACCOUNT_ID);
    var recipientId = RecipientId.newId();
    var exception = new RecipientNotFoundException(recipientId, BANK_ACCOUNT_ID);

    when(queryRepository.findByBankAccountId(BANK_ACCOUNT_ID))
      .thenThrow(exception);

    assertThatThrownBy(() -> handler.handle(query))
      .isSameAs(exception);

    verify(queryRepository).findByBankAccountId(BANK_ACCOUNT_ID);
    verifyNoMoreInteractions(queryRepository);
  }

  @Test
  void shouldThrowUnexpectedException_whenRepositoryThrowsUnexpectedException() {
    var query = new ListRecipientsQuery(BANK_ACCOUNT_ID);
    var exception = new IllegalStateException("database unavailable");

    when(queryRepository.findByBankAccountId(BANK_ACCOUNT_ID))
      .thenThrow(exception);

    assertThatThrownBy(() -> handler.handle(query))
      .isSameAs(exception);

    verify(queryRepository).findByBankAccountId(BANK_ACCOUNT_ID);
    verifyNoMoreInteractions(queryRepository);
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
