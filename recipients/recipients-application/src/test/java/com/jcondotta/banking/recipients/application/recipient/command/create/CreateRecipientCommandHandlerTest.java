package com.jcondotta.banking.recipients.application.recipient.command.create;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.jcondotta.application.logging.LogKey;
import com.jcondotta.banking.recipients.application.common.log.RecipientLogKey;
import com.jcondotta.application.logging.LogOutcome;
import com.jcondotta.banking.recipients.application.common.log.RecipientEventType;
import com.jcondotta.application.logging.StructuredLogEventSupport;
import com.jcondotta.banking.recipients.domain.recipient.aggregate.Recipient;
import com.jcondotta.banking.recipients.domain.recipient.exceptions.DuplicateRecipientIbanException;
import com.jcondotta.banking.recipients.domain.recipient.identity.BankAccountId;
import com.jcondotta.banking.recipients.domain.recipient.repository.RecipientRepository;
import com.jcondotta.banking.recipients.domain.recipient.value_objects.Iban;
import com.jcondotta.banking.recipients.domain.recipient.value_objects.RecipientName;
import com.jcondotta.banking.recipients.domain.testsupport.RecipientFixtures;
import com.jcondotta.banking.recipients.domain.testsupport.TimeFactory;
import com.jcondotta.domain.exception.DomainException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateRecipientCommandHandlerTest {

  private static final BankAccountId BANK_ACCOUNT_ID = BankAccountId.of(UUID.randomUUID());

  private static final RecipientName RECIPIENT_NAME = RecipientFixtures.JEFFERSON.toName();
  private static final Iban IBAN = RecipientFixtures.JEFFERSON.toIban();

  private static final Clock CLOCK = Clock.fixed(TimeFactory.FIXED_INSTANT, ZoneOffset.UTC);

  @Mock
  private RecipientRepository recipientRepository;

  @Captor
  private ArgumentCaptor<Recipient> recipientCaptor;

  private ListAppender<ILoggingEvent> logAppender;

  private CreateRecipientCommandHandler commandHandler;

  @BeforeEach
  void setUp() {
    commandHandler = new CreateRecipientCommandHandler(recipientRepository, CLOCK);
    logAppender = StructuredLogEventSupport.attachAppender(CreateRecipientCommandHandler.class);
  }

  @org.junit.jupiter.api.AfterEach
  void tearDown() {
    StructuredLogEventSupport.detachAppender(CreateRecipientCommandHandler.class, logAppender);
  }

  @Test
  void shouldCreateRecipient_whenCommandIsValid() {
    var command = new CreateRecipientCommand(
      BANK_ACCOUNT_ID,
      RECIPIENT_NAME,
      IBAN
    );

    var recipientId = commandHandler.handle(command);

    verify(recipientRepository).save(recipientCaptor.capture());
    var savedRecipient = recipientCaptor.getValue();

    assertThat(savedRecipient.getId()).isEqualTo(recipientId);
    assertThat(savedRecipient.getBankAccountId()).isEqualTo(BANK_ACCOUNT_ID);
    assertThat(savedRecipient.getRecipientName()).isEqualTo(RECIPIENT_NAME);
    assertThat(savedRecipient.getIban()).isEqualTo(IBAN);
    assertThat(savedRecipient.getCreatedAt()).isEqualTo(CLOCK.instant());
    assertThat(savedRecipient.getVersion()).isNull();

    assertThat(StructuredLogEventSupport.lastEvent(logAppender, ILoggingEvent::getLevel))
      .isEqualTo(Level.INFO);
    assertThat(StructuredLogEventSupport.lastEventKeyValues(logAppender))
      .containsEntry(LogKey.EVENT_TYPE, RecipientEventType.CREATE)
      .containsEntry(LogKey.OUTCOME, LogOutcome.SUCCESS);
    assertThat(StructuredLogEventSupport.eventTypes(logAppender))
      .allMatch(eventType -> !eventType.contains(".failed"));
  }

  @Test
  void shouldCallRepositoryCreate_whenCommandIsValid() {
    var command = new CreateRecipientCommand(
      BANK_ACCOUNT_ID,
      RECIPIENT_NAME,
      IBAN
    );

    commandHandler.handle(command);

    verify(recipientRepository).save(recipientCaptor.capture());
    verifyNoMoreInteractions(recipientRepository);
  }

  @Test
  void shouldReturnRecipientId_whenRecipientIsCreated() {
    var command = new CreateRecipientCommand(
      BANK_ACCOUNT_ID,
      RECIPIENT_NAME,
      IBAN
    );

    var recipientId = commandHandler.handle(command);

    verify(recipientRepository).save(recipientCaptor.capture());
    assertThat(recipientId).isEqualTo(recipientCaptor.getValue().getId());
  }

  @Test
  void shouldThrowDuplicateRecipientIbanException_whenRepositoryThrowsDomainException() {
    var command = new CreateRecipientCommand(
      BANK_ACCOUNT_ID,
      RECIPIENT_NAME,
      IBAN
    );
    var exception = new DuplicateRecipientIbanException(IBAN, BANK_ACCOUNT_ID);

    doThrow(exception)
      .when(recipientRepository)
      .save(any(Recipient.class));

    assertThatThrownBy(() -> commandHandler.handle(command))
      .isSameAs(exception);

    verify(recipientRepository).save(any(Recipient.class));
    verifyNoMoreInteractions(recipientRepository);

    assertThat(StructuredLogEventSupport.lastEvent(logAppender, ILoggingEvent::getLevel))
      .isEqualTo(Level.WARN);
    assertThat(StructuredLogEventSupport.lastEventKeyValues(logAppender))
      .containsEntry(LogKey.EVENT_TYPE, RecipientEventType.CREATE)
      .containsEntry(LogKey.OUTCOME, LogOutcome.FAILURE);
    assertThat(StructuredLogEventSupport.eventTypes(logAppender))
      .allMatch(eventType -> !eventType.contains(".failed"));
  }

  @Test
  void shouldThrowDomainException_whenRepositoryThrowsDomainExceptionWithoutSpecificLogContext() {
    var command = new CreateRecipientCommand(
      BANK_ACCOUNT_ID,
      RECIPIENT_NAME,
      IBAN
    );
    var exception = new TestDomainException();

    doThrow(exception)
      .when(recipientRepository)
      .save(any(Recipient.class));

    assertThatThrownBy(() -> commandHandler.handle(command))
      .isSameAs(exception);

    verify(recipientRepository).save(any(Recipient.class));
    verifyNoMoreInteractions(recipientRepository);

    assertThat(StructuredLogEventSupport.lastEvent(logAppender, ILoggingEvent::getLevel))
      .isEqualTo(Level.WARN);
    assertThat(StructuredLogEventSupport.lastEventKeyValues(logAppender))
      .containsEntry(LogKey.EVENT_TYPE, RecipientEventType.CREATE)
      .containsEntry(LogKey.OUTCOME, LogOutcome.FAILURE);
    assertThat(StructuredLogEventSupport.eventTypes(logAppender))
      .allMatch(eventType -> !eventType.contains(".failed"));
  }

  @Test
  void shouldThrowUnexpectedException_whenRepositoryThrowsUnexpectedException() {
    var command = new CreateRecipientCommand(
      BANK_ACCOUNT_ID,
      RECIPIENT_NAME,
      IBAN
    );
    var exception = new IllegalStateException("database unavailable");

    doThrow(exception)
      .when(recipientRepository)
      .save(any(Recipient.class));

    assertThatThrownBy(() -> commandHandler.handle(command))
      .isSameAs(exception);

    verify(recipientRepository).save(any(Recipient.class));
    verifyNoMoreInteractions(recipientRepository);

    assertThat(StructuredLogEventSupport.lastEvent(logAppender, ILoggingEvent::getLevel))
      .isEqualTo(Level.ERROR);
    assertThat(StructuredLogEventSupport.lastEventKeyValues(logAppender))
      .containsEntry(LogKey.EVENT_TYPE, RecipientEventType.CREATE)
      .containsEntry(LogKey.OUTCOME, LogOutcome.FAILURE);
    assertThat(StructuredLogEventSupport.eventTypes(logAppender))
      .allMatch(eventType -> !eventType.contains(".failed"));
  }

  private static final class TestDomainException extends DomainException {

    private TestDomainException() {
      super("domain error");
    }
  }
}
