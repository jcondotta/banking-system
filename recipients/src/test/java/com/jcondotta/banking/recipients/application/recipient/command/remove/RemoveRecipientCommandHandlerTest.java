package com.jcondotta.banking.recipients.application.recipient.command.remove;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.jcondotta.application.logging.LogKey;
import com.jcondotta.banking.recipients.application.common.log.RecipientLogKey;
import com.jcondotta.application.logging.LogOutcome;
import com.jcondotta.banking.recipients.application.common.log.RecipientEventType;
import com.jcondotta.application.logging.StructuredLogEventSupport;
import com.jcondotta.banking.recipients.domain.recipient.aggregate.Recipient;
import com.jcondotta.banking.recipients.domain.recipient.exceptions.RecipientNotFoundException;
import com.jcondotta.banking.recipients.domain.recipient.exceptions.RecipientOwnershipMismatchException;
import com.jcondotta.banking.recipients.domain.recipient.identity.BankAccountId;
import com.jcondotta.banking.recipients.domain.recipient.identity.RecipientId;
import com.jcondotta.banking.recipients.domain.recipient.repository.RecipientRepository;
import com.jcondotta.banking.recipients.domain.recipient.value_objects.Iban;
import com.jcondotta.banking.recipients.domain.recipient.value_objects.RecipientName;
import com.jcondotta.banking.recipients.domain.testsupport.RecipientFixtures;
import com.jcondotta.banking.recipients.domain.testsupport.TimeFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RemoveRecipientCommandHandlerTest {

  private static final BankAccountId BANK_ACCOUNT_ID = BankAccountId.of(UUID.randomUUID());
  private static final RecipientId RECIPIENT_ID = RecipientId.newId();

  private static final RecipientName RECIPIENT_NAME = RecipientFixtures.JEFFERSON.toName();
  private static final Iban IBAN = RecipientFixtures.JEFFERSON.toIban();

  private static final Instant CREATED_AT = TimeFactory.FIXED_INSTANT;

  @Mock
  private RecipientRepository recipientRepository;

  private ListAppender<ILoggingEvent> logAppender;

  private RemoveRecipientCommandHandler commandHandler;

  @BeforeEach
  void setUp() {
    commandHandler = new RemoveRecipientCommandHandler(recipientRepository);
    logAppender = StructuredLogEventSupport.attachAppender(RemoveRecipientCommandHandler.class);
  }

  @org.junit.jupiter.api.AfterEach
  void tearDown() {
    StructuredLogEventSupport.detachAppender(RemoveRecipientCommandHandler.class, logAppender);
  }

  @Test
  void shouldRemoveRecipient_whenRecipientExists() {
    var recipient = recipient();
    var command = new RemoveRecipientCommand(BANK_ACCOUNT_ID, RECIPIENT_ID);

    when(recipientRepository.findById(RECIPIENT_ID))
      .thenReturn(Optional.of(recipient));

    commandHandler.handle(command);

    verify(recipientRepository).findById(RECIPIENT_ID);
    verify(recipientRepository).delete(recipient);
    verifyNoMoreInteractions(recipientRepository);

    assertThat(StructuredLogEventSupport.lastEvent(logAppender, ILoggingEvent::getLevel))
      .isEqualTo(Level.INFO);
    assertThat(StructuredLogEventSupport.lastEventKeyValues(logAppender))
      .containsEntry(LogKey.EVENT_TYPE, RecipientEventType.REMOVE)
      .containsEntry(LogKey.OUTCOME, LogOutcome.SUCCESS);
    assertThat(StructuredLogEventSupport.eventTypes(logAppender))
      .allMatch(eventType -> !eventType.contains(".failed"));
  }

  @Test
  void shouldThrowRecipientOwnershipMismatchException_whenOwnershipMismatch() {
    var recipient = recipient();
    var anotherBankAccountId = BankAccountId.of(UUID.randomUUID());
    var command = new RemoveRecipientCommand(anotherBankAccountId, RECIPIENT_ID);

    when(recipientRepository.findById(RECIPIENT_ID))
      .thenReturn(Optional.of(recipient));

    assertThatThrownBy(() -> commandHandler.handle(command))
      .isInstanceOf(RecipientOwnershipMismatchException.class);

    verify(recipientRepository).findById(RECIPIENT_ID);
    verify(recipientRepository, never()).delete(recipient);
    verifyNoMoreInteractions(recipientRepository);

    assertThat(StructuredLogEventSupport.lastEvent(logAppender, ILoggingEvent::getLevel))
      .isEqualTo(Level.WARN);
    assertThat(StructuredLogEventSupport.lastEventKeyValues(logAppender))
      .containsEntry(LogKey.EVENT_TYPE, RecipientEventType.REMOVE)
      .containsEntry(LogKey.OUTCOME, LogOutcome.FAILURE);
    assertThat(StructuredLogEventSupport.eventTypes(logAppender))
      .allMatch(eventType -> !eventType.contains(".failed"));
  }

  @Test
  void shouldThrowRecipientNotFoundException_whenRecipientDoesNotExist() {
    var command = new RemoveRecipientCommand(BANK_ACCOUNT_ID, RECIPIENT_ID);

    when(recipientRepository.findById(RECIPIENT_ID))
      .thenReturn(Optional.empty());

    assertThatThrownBy(() -> commandHandler.handle(command))
      .isInstanceOf(RecipientNotFoundException.class);

    verify(recipientRepository).findById(RECIPIENT_ID);
    verifyNoMoreInteractions(recipientRepository);

    assertThat(StructuredLogEventSupport.lastEvent(logAppender, ILoggingEvent::getLevel))
      .isEqualTo(Level.WARN);
    assertThat(StructuredLogEventSupport.lastEventKeyValues(logAppender))
      .containsEntry(LogKey.EVENT_TYPE, RecipientEventType.REMOVE)
      .containsEntry(LogKey.OUTCOME, LogOutcome.FAILURE);
    assertThat(StructuredLogEventSupport.eventTypes(logAppender))
      .allMatch(eventType -> !eventType.contains(".failed"));
  }

  @Test
  void shouldThrowUnexpectedException_whenRepositoryThrowsUnexpectedException() {
    var command = new RemoveRecipientCommand(BANK_ACCOUNT_ID, RECIPIENT_ID);
    var exception = new IllegalStateException("database unavailable");

    when(recipientRepository.findById(RECIPIENT_ID))
      .thenThrow(exception);

    assertThatThrownBy(() -> commandHandler.handle(command))
      .isSameAs(exception);

    verify(recipientRepository).findById(RECIPIENT_ID);
    verifyNoMoreInteractions(recipientRepository);

    assertThat(StructuredLogEventSupport.lastEvent(logAppender, ILoggingEvent::getLevel))
      .isEqualTo(Level.ERROR);
    assertThat(StructuredLogEventSupport.lastEventKeyValues(logAppender))
      .containsEntry(LogKey.EVENT_TYPE, RecipientEventType.REMOVE)
      .containsEntry(LogKey.OUTCOME, LogOutcome.FAILURE);
    assertThat(StructuredLogEventSupport.eventTypes(logAppender))
      .allMatch(eventType -> !eventType.contains(".failed"));
  }

  private static Recipient recipient() {
    return Recipient.restore(
      RECIPIENT_ID,
      BANK_ACCOUNT_ID,
      RECIPIENT_NAME,
      IBAN,
      CREATED_AT,
      0L
    );
  }
}
