package com.jcondotta.banking.recipients.application.recipient.command.update;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.jcondotta.application.logging.LogKey;
import com.jcondotta.application.logging.LogOutcome;
import com.jcondotta.application.logging.StructuredLogEventSupport;
import com.jcondotta.banking.recipients.application.common.log.RecipientEventType;
import com.jcondotta.banking.recipients.domain.recipient.aggregate.Recipient;
import com.jcondotta.banking.recipients.domain.recipient.exceptions.DuplicateRecipientIbanException;
import com.jcondotta.banking.recipients.domain.recipient.exceptions.RecipientNotFoundException;
import com.jcondotta.banking.recipients.domain.recipient.identity.BankAccountId;
import com.jcondotta.banking.recipients.domain.recipient.identity.RecipientId;
import com.jcondotta.banking.recipients.domain.recipient.repository.RecipientRepository;
import com.jcondotta.banking.recipients.domain.recipient.value_objects.Iban;
import com.jcondotta.banking.recipients.domain.recipient.value_objects.RecipientName;
import com.jcondotta.banking.recipients.domain.testsupport.RecipientFixtures;
import com.jcondotta.banking.recipients.domain.testsupport.TimeFactory;
import org.junit.jupiter.api.AfterEach;
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
class UpdateRecipientCommandHandlerTest {

  private static final BankAccountId BANK_ACCOUNT_ID = BankAccountId.of(UUID.randomUUID());
  private static final RecipientId RECIPIENT_ID = RecipientId.newId();
  private static final RecipientName RECIPIENT_NAME = RecipientFixtures.JEFFERSON.toName();
  private static final Iban IBAN = RecipientFixtures.JEFFERSON.toIban();
  private static final RecipientName NEW_RECIPIENT_NAME = RecipientFixtures.PATRIZIO.toName();
  private static final Iban NEW_IBAN = RecipientFixtures.PATRIZIO.toIban();
  private static final Instant CREATED_AT = TimeFactory.FIXED_INSTANT;

  @Mock
  private RecipientRepository recipientRepository;

  private ListAppender<ILoggingEvent> logAppender;

  private UpdateRecipientCommandHandler commandHandler;

  @BeforeEach
  void setUp() {
    commandHandler = new UpdateRecipientCommandHandler(recipientRepository);
    logAppender = StructuredLogEventSupport.attachAppender(UpdateRecipientCommandHandler.class);
  }

  @AfterEach
  void tearDown() {
    StructuredLogEventSupport.detachAppender(UpdateRecipientCommandHandler.class, logAppender);
  }

  @Test
  void shouldUpdateRecipient_whenRecipientExistsForBankAccount() {
    var recipient = recipient();
    var command = new UpdateRecipientCommand(BANK_ACCOUNT_ID, RECIPIENT_ID, NEW_RECIPIENT_NAME, NEW_IBAN);

    when(recipientRepository.findByBankAccountIdAndId(BANK_ACCOUNT_ID, RECIPIENT_ID))
      .thenReturn(Optional.of(recipient));

    commandHandler.handle(command);

    assertThat(recipient.getRecipientName()).isEqualTo(NEW_RECIPIENT_NAME);
    assertThat(recipient.getIban()).isEqualTo(NEW_IBAN);

    verify(recipientRepository).findByBankAccountIdAndId(BANK_ACCOUNT_ID, RECIPIENT_ID);
    verify(recipientRepository).save(recipient);
    verifyNoMoreInteractions(recipientRepository);

    assertThat(StructuredLogEventSupport.lastEvent(logAppender, ILoggingEvent::getLevel))
      .isEqualTo(Level.INFO);
    assertThat(StructuredLogEventSupport.lastEventKeyValues(logAppender))
      .containsEntry(LogKey.EVENT_TYPE, RecipientEventType.UPDATE)
      .containsEntry(LogKey.OUTCOME, LogOutcome.SUCCESS);
    assertThat(StructuredLogEventSupport.eventTypes(logAppender))
      .allMatch(eventType -> !eventType.contains(".failed"));
  }

  @Test
  void shouldThrowRecipientNotFoundException_whenRecipientDoesNotExistForBankAccount() {
    var command = new UpdateRecipientCommand(BANK_ACCOUNT_ID, RECIPIENT_ID, NEW_RECIPIENT_NAME, NEW_IBAN);

    when(recipientRepository.findByBankAccountIdAndId(BANK_ACCOUNT_ID, RECIPIENT_ID))
      .thenReturn(Optional.empty());

    assertThatThrownBy(() -> commandHandler.handle(command))
      .isInstanceOf(RecipientNotFoundException.class);

    verify(recipientRepository).findByBankAccountIdAndId(BANK_ACCOUNT_ID, RECIPIENT_ID);
    verifyNoMoreInteractions(recipientRepository);

    assertThat(StructuredLogEventSupport.lastEvent(logAppender, ILoggingEvent::getLevel))
      .isEqualTo(Level.WARN);
    assertThat(StructuredLogEventSupport.lastEventKeyValues(logAppender))
      .containsEntry(LogKey.EVENT_TYPE, RecipientEventType.UPDATE)
      .containsEntry(LogKey.OUTCOME, LogOutcome.FAILURE);
    assertThat(StructuredLogEventSupport.eventTypes(logAppender))
      .allMatch(eventType -> !eventType.contains(".failed"));
  }

  @Test
  void shouldRethrowDomainException_whenRepositoryRejectsDuplicateIban() {
    var recipient = recipient();
    var command = new UpdateRecipientCommand(BANK_ACCOUNT_ID, RECIPIENT_ID, NEW_RECIPIENT_NAME, NEW_IBAN);
    var exception = new DuplicateRecipientIbanException(NEW_IBAN, BANK_ACCOUNT_ID);

    when(recipientRepository.findByBankAccountIdAndId(BANK_ACCOUNT_ID, RECIPIENT_ID))
      .thenReturn(Optional.of(recipient));
    doThrow(exception).when(recipientRepository).save(recipient);

    assertThatThrownBy(() -> commandHandler.handle(command))
      .isSameAs(exception);

    verify(recipientRepository).findByBankAccountIdAndId(BANK_ACCOUNT_ID, RECIPIENT_ID);
    verify(recipientRepository).save(recipient);
    verifyNoMoreInteractions(recipientRepository);

    assertThat(StructuredLogEventSupport.lastEvent(logAppender, ILoggingEvent::getLevel))
      .isEqualTo(Level.WARN);
    assertThat(StructuredLogEventSupport.lastEventKeyValues(logAppender))
      .containsEntry(LogKey.EVENT_TYPE, RecipientEventType.UPDATE)
      .containsEntry(LogKey.OUTCOME, LogOutcome.FAILURE);
    assertThat(StructuredLogEventSupport.eventTypes(logAppender))
      .allMatch(eventType -> !eventType.contains(".failed"));
  }

  @Test
  void shouldThrowUnexpectedException_whenRepositoryThrowsUnexpectedException() {
    var command = new UpdateRecipientCommand(BANK_ACCOUNT_ID, RECIPIENT_ID, NEW_RECIPIENT_NAME, NEW_IBAN);
    var exception = new IllegalStateException("database unavailable");

    when(recipientRepository.findByBankAccountIdAndId(BANK_ACCOUNT_ID, RECIPIENT_ID))
      .thenThrow(exception);

    assertThatThrownBy(() -> commandHandler.handle(command))
      .isSameAs(exception);

    verify(recipientRepository).findByBankAccountIdAndId(BANK_ACCOUNT_ID, RECIPIENT_ID);
    verifyNoMoreInteractions(recipientRepository);

    assertThat(StructuredLogEventSupport.lastEvent(logAppender, ILoggingEvent::getLevel))
      .isEqualTo(Level.ERROR);
    assertThat(StructuredLogEventSupport.lastEventKeyValues(logAppender))
      .containsEntry(LogKey.EVENT_TYPE, RecipientEventType.UPDATE)
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
