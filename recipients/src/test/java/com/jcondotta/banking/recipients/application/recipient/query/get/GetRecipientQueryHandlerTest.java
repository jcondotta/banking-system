package com.jcondotta.banking.recipients.application.recipient.query.get;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.jcondotta.application.logging.LogKey;
import com.jcondotta.application.logging.LogOutcome;
import com.jcondotta.application.logging.StructuredLogEventSupport;
import com.jcondotta.banking.recipients.application.common.log.RecipientEventType;
import com.jcondotta.banking.recipients.application.common.log.RecipientLogKey;
import com.jcondotta.banking.recipients.application.recipient.query.RecipientQueryRepository;
import com.jcondotta.banking.recipients.application.recipient.query.model.RecipientSummary;
import com.jcondotta.banking.recipients.domain.recipient.exceptions.RecipientNotFoundException;
import com.jcondotta.banking.recipients.domain.recipient.identity.BankAccountId;
import com.jcondotta.banking.recipients.domain.recipient.identity.RecipientId;
import com.jcondotta.banking.recipients.domain.testsupport.RecipientTestData;
import com.jcondotta.banking.recipients.domain.testsupport.TimeFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetRecipientQueryHandlerTest {

  private static final BankAccountId BANK_ACCOUNT_ID = BankAccountId.of(UUID.randomUUID());
  private static final RecipientId RECIPIENT_ID = RecipientId.newId();

  @Mock
  private RecipientQueryRepository queryRepository;

  private ListAppender<ILoggingEvent> logAppender;

  private GetRecipientQueryHandler handler;

  @BeforeEach
  void setUp() {
    handler = new GetRecipientQueryHandler(queryRepository);
    logAppender = StructuredLogEventSupport.attachAppender(GetRecipientQueryHandler.class);
  }

  @AfterEach
  void tearDown() {
    StructuredLogEventSupport.detachAppender(GetRecipientQueryHandler.class, logAppender);
  }

  @Test
  void shouldReturnRecipient_whenRecipientBelongsToBankAccount() {
    var summary = recipientSummary();
    var query = new GetRecipientQuery(BANK_ACCOUNT_ID, RECIPIENT_ID);

    when(queryRepository.findByBankAccountIdAndRecipientId(BANK_ACCOUNT_ID, RECIPIENT_ID))
      .thenReturn(Optional.of(summary));

    var result = handler.handle(query);

    assertThat(result.recipient()).isEqualTo(summary);
    verify(queryRepository).findByBankAccountIdAndRecipientId(BANK_ACCOUNT_ID, RECIPIENT_ID);
    verifyNoMoreInteractions(queryRepository);

    assertThat(StructuredLogEventSupport.lastEvent(logAppender, ILoggingEvent::getLevel))
      .isEqualTo(Level.INFO);
    assertThat(StructuredLogEventSupport.lastEventKeyValues(logAppender))
      .containsEntry(LogKey.EVENT_TYPE, RecipientEventType.GET)
      .containsEntry(LogKey.OUTCOME, LogOutcome.SUCCESS)
      .containsEntry(RecipientLogKey.BANK_ACCOUNT_ID, BANK_ACCOUNT_ID.asString())
      .containsEntry(RecipientLogKey.RECIPIENT_ID, RECIPIENT_ID.asString());
    assertThat(StructuredLogEventSupport.eventTypes(logAppender))
      .allMatch(eventType -> !eventType.contains(".failed"));
  }

  @Test
  void shouldThrowNotFoundException_whenRecipientIsNotFoundForBankAccount() {
    var query = new GetRecipientQuery(BANK_ACCOUNT_ID, RECIPIENT_ID);

    when(queryRepository.findByBankAccountIdAndRecipientId(BANK_ACCOUNT_ID, RECIPIENT_ID))
      .thenReturn(Optional.empty());

    assertThatThrownBy(() -> handler.handle(query))
      .isInstanceOf(RecipientNotFoundException.class)
      .hasMessage(RecipientNotFoundException.MESSAGE);

    verify(queryRepository).findByBankAccountIdAndRecipientId(BANK_ACCOUNT_ID, RECIPIENT_ID);
    verifyNoMoreInteractions(queryRepository);

    assertThat(StructuredLogEventSupport.lastEvent(logAppender, ILoggingEvent::getLevel))
      .isEqualTo(Level.WARN);
    assertThat(StructuredLogEventSupport.lastEventKeyValues(logAppender))
      .containsEntry(LogKey.EVENT_TYPE, RecipientEventType.GET)
      .containsEntry(LogKey.OUTCOME, LogOutcome.FAILURE);
    assertThat(StructuredLogEventSupport.eventTypes(logAppender))
      .allMatch(eventType -> !eventType.contains(".failed"));
  }

  @Test
  void shouldThrowUnexpectedException_whenRepositoryThrowsUnexpectedException() {
    var query = new GetRecipientQuery(BANK_ACCOUNT_ID, RECIPIENT_ID);
    var exception = new IllegalStateException("database unavailable");

    when(queryRepository.findByBankAccountIdAndRecipientId(BANK_ACCOUNT_ID, RECIPIENT_ID))
      .thenThrow(exception);

    assertThatThrownBy(() -> handler.handle(query))
      .isSameAs(exception);

    verify(queryRepository).findByBankAccountIdAndRecipientId(BANK_ACCOUNT_ID, RECIPIENT_ID);
    verifyNoMoreInteractions(queryRepository);

    assertThat(StructuredLogEventSupport.lastEvent(logAppender, ILoggingEvent::getLevel))
      .isEqualTo(Level.ERROR);
    assertThat(StructuredLogEventSupport.lastEventKeyValues(logAppender))
      .containsEntry(LogKey.EVENT_TYPE, RecipientEventType.GET)
      .containsEntry(LogKey.OUTCOME, LogOutcome.FAILURE);
    assertThat(StructuredLogEventSupport.eventTypes(logAppender))
      .allMatch(eventType -> !eventType.contains(".failed"));
  }

  private static RecipientSummary recipientSummary() {
    return new RecipientSummary(
      RECIPIENT_ID.value(),
      RecipientTestData.JEFFERSON.getName(),
      RecipientTestData.JEFFERSON.getIban(),
      TimeFactory.FIXED_INSTANT
    );
  }
}
