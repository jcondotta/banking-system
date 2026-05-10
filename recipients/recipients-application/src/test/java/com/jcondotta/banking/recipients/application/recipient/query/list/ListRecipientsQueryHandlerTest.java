package com.jcondotta.banking.recipients.application.recipient.query.list;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.jcondotta.application.query.PageRequest;
import com.jcondotta.application.query.PageResult;
import com.jcondotta.banking.recipients.application.common.log.LogKey;
import com.jcondotta.banking.recipients.application.common.log.LogOutcome;
import com.jcondotta.banking.recipients.application.common.log.RecipientEventType;
import com.jcondotta.banking.recipients.application.common.log.StructuredLogEventSupport;
import com.jcondotta.banking.recipients.application.recipient.query.model.RecipientSummary;
import com.jcondotta.banking.recipients.domain.recipient.exceptions.RecipientNotFoundException;
import com.jcondotta.banking.recipients.domain.recipient.identity.BankAccountId;
import com.jcondotta.banking.recipients.domain.recipient.identity.RecipientId;
import com.jcondotta.banking.recipients.domain.testsupport.RecipientTestData;
import com.jcondotta.banking.recipients.domain.testsupport.TimeFactory;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListRecipientsQueryHandlerTest {

  private static final UUID RECIPIENT_ID_JEFFERSON = UUID.randomUUID();
  private static final UUID RECIPIENT_ID_PATRIZIO = UUID.randomUUID();

  private static final BankAccountId BANK_ACCOUNT_ID = BankAccountId.of(UUID.randomUUID());
  private static final PageRequest PAGE_REQUEST = new PageRequest(0, 20);
  private static final ListRecipientsFilter FILTER = ListRecipientsFilter.none();

  private static final Instant CREATED_AT = TimeFactory.FIXED_INSTANT;

  @Mock
  private RecipientQueryRepository queryRepository;

  private ListAppender<ILoggingEvent> logAppender;

  private ListRecipientsQueryHandler handler;

  @BeforeEach
  void setUp() {
    handler = new ListRecipientsQueryHandler(queryRepository);
    logAppender = StructuredLogEventSupport.attachAppender(ListRecipientsQueryHandler.class);
  }

  @org.junit.jupiter.api.AfterEach
  void tearDown() {
    StructuredLogEventSupport.detachAppender(ListRecipientsQueryHandler.class, logAppender);
  }

  @Test
  void shouldReturnRecipients_whenBankAccountHasRecipients() {
    var recipientsSummary = List.of(
      recipientSummary(RECIPIENT_ID_JEFFERSON, RecipientTestData.JEFFERSON),
      recipientSummary(RECIPIENT_ID_PATRIZIO, RecipientTestData.PATRIZIO)
    );

    var page = new PageResult<>(recipientsSummary, 0, 20, 2, 1);

    when(queryRepository.findByBankAccountId(BANK_ACCOUNT_ID, PAGE_REQUEST, FILTER))
      .thenReturn(page);

    var query = new ListRecipientsQuery(BANK_ACCOUNT_ID, PAGE_REQUEST, FILTER);
    var queryResult = handler.handle(query);

    assertThat(queryResult.recipients()).containsExactlyElementsOf(recipientsSummary);
    assertThat(queryResult.page()).isEqualTo(page);

    verify(queryRepository).findByBankAccountId(BANK_ACCOUNT_ID, PAGE_REQUEST, FILTER);
    verifyNoMoreInteractions(queryRepository);

    assertThat(StructuredLogEventSupport.lastEvent(logAppender, ILoggingEvent::getLevel))
      .isEqualTo(Level.INFO);
    assertThat(StructuredLogEventSupport.lastEventKeyValues(logAppender))
      .containsEntry(LogKey.EVENT_TYPE, RecipientEventType.LIST)
      .containsEntry(LogKey.OUTCOME, LogOutcome.SUCCESS)
      .containsEntry(LogKey.FILTER_NAME_PRESENT, "false");
    assertThat(StructuredLogEventSupport.eventTypes(logAppender))
      .allMatch(eventType -> !eventType.contains(".failed"));
  }

  @Test
  void shouldReturnEmptyList_whenBankAccountHasNoRecipients() {
    var page = new PageResult<RecipientSummary>(List.of(), 0, 20, 0, 0);

    when(queryRepository.findByBankAccountId(BANK_ACCOUNT_ID, PAGE_REQUEST, FILTER))
      .thenReturn(page);

    var query = new ListRecipientsQuery(BANK_ACCOUNT_ID, PAGE_REQUEST, FILTER);
    var queryResult = handler.handle(query);

    assertThat(queryResult.recipients()).isEmpty();
    assertThat(queryResult.page()).isEqualTo(page);

    verify(queryRepository).findByBankAccountId(BANK_ACCOUNT_ID, PAGE_REQUEST, FILTER);
    verifyNoMoreInteractions(queryRepository);

    assertThat(StructuredLogEventSupport.lastEvent(logAppender, ILoggingEvent::getLevel))
      .isEqualTo(Level.INFO);
    assertThat(StructuredLogEventSupport.lastEventKeyValues(logAppender))
      .containsEntry(LogKey.EVENT_TYPE, RecipientEventType.LIST)
      .containsEntry(LogKey.OUTCOME, LogOutcome.SUCCESS)
      .containsEntry(LogKey.FILTER_NAME_PRESENT, "false");
    assertThat(StructuredLogEventSupport.eventTypes(logAppender))
      .allMatch(eventType -> !eventType.contains(".failed"));
  }

  @Test
  void shouldThrowDomainException_whenRepositoryThrowsDomainException() {
    var query = new ListRecipientsQuery(BANK_ACCOUNT_ID, PAGE_REQUEST, FILTER);
    var recipientId = RecipientId.newId();
    var exception = new RecipientNotFoundException(recipientId, BANK_ACCOUNT_ID);

    when(queryRepository.findByBankAccountId(BANK_ACCOUNT_ID, PAGE_REQUEST, FILTER))
      .thenThrow(exception);

    assertThatThrownBy(() -> handler.handle(query))
      .isSameAs(exception);

    verify(queryRepository).findByBankAccountId(BANK_ACCOUNT_ID, PAGE_REQUEST, FILTER);
    verifyNoMoreInteractions(queryRepository);

    assertThat(StructuredLogEventSupport.lastEvent(logAppender, ILoggingEvent::getLevel))
      .isEqualTo(Level.WARN);
    assertThat(StructuredLogEventSupport.lastEventKeyValues(logAppender))
      .containsEntry(LogKey.EVENT_TYPE, RecipientEventType.LIST)
      .containsEntry(LogKey.OUTCOME, LogOutcome.FAILURE);
    assertThat(StructuredLogEventSupport.eventTypes(logAppender))
      .allMatch(eventType -> !eventType.contains(".failed"));
  }

  @Test
  void shouldThrowUnexpectedException_whenRepositoryThrowsUnexpectedException() {
    var query = new ListRecipientsQuery(BANK_ACCOUNT_ID, PAGE_REQUEST, FILTER);
    var exception = new IllegalStateException("database unavailable");

    when(queryRepository.findByBankAccountId(BANK_ACCOUNT_ID, PAGE_REQUEST, FILTER))
      .thenThrow(exception);

    assertThatThrownBy(() -> handler.handle(query))
      .isSameAs(exception);

    verify(queryRepository).findByBankAccountId(BANK_ACCOUNT_ID, PAGE_REQUEST, FILTER);
    verifyNoMoreInteractions(queryRepository);

    assertThat(StructuredLogEventSupport.lastEvent(logAppender, ILoggingEvent::getLevel))
      .isEqualTo(Level.ERROR);
    assertThat(StructuredLogEventSupport.lastEventKeyValues(logAppender))
      .containsEntry(LogKey.EVENT_TYPE, RecipientEventType.LIST)
      .containsEntry(LogKey.OUTCOME, LogOutcome.FAILURE);
    assertThat(StructuredLogEventSupport.eventTypes(logAppender))
      .allMatch(eventType -> !eventType.contains(".failed"));
  }

  private static RecipientSummary recipientSummary(UUID recipientId, RecipientTestData recipientTestData) {
    return new RecipientSummary(
      recipientId,
      recipientTestData.getName(),
      recipientTestData.getIban(),
      CREATED_AT
    );
  }
}
