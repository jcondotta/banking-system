package com.jcondotta.banking.accounts.application.bankaccount.command.add_joint_holder;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.jcondotta.application.command.CommandHandler;
import com.jcondotta.banking.accounts.application.common.log.BankAccountEventType;
import com.jcondotta.application.logging.LogKey;
import com.jcondotta.banking.accounts.application.common.log.BankAccountLogKey;
import com.jcondotta.application.logging.LogOutcome;
import com.jcondotta.application.logging.StructuredLogEventSupport;
import com.jcondotta.banking.accounts.application.bankaccount.command.add_joint_holder.model.AddJointHolderCommand;
import com.jcondotta.banking.accounts.domain.bankaccount.aggregate.BankAccount;
import com.jcondotta.banking.accounts.domain.bankaccount.exceptions.BankAccountNotActiveException;
import com.jcondotta.banking.accounts.domain.bankaccount.exceptions.BankAccountNotFoundException;
import com.jcondotta.banking.accounts.domain.bankaccount.identity.BankAccountId;
import com.jcondotta.banking.accounts.domain.bankaccount.repository.BankAccountRepository;
import com.jcondotta.banking.accounts.domain.testsupport.AccountHolderFixtures;
import com.jcondotta.banking.accounts.domain.testsupport.BankAccountFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddJointHolderCommandHandlerTest {

  @Mock
  private BankAccountRepository bankAccountRepository;

  private ListAppender<ILoggingEvent> logAppender;

  private CommandHandler<AddJointHolderCommand> commandHandler;

  @BeforeEach
  void setUp() {
    commandHandler = new AddJointHolderCommandHandler(bankAccountRepository);
    logAppender = StructuredLogEventSupport.attachAppender(AddJointHolderCommandHandler.class);
  }

  @org.junit.jupiter.api.AfterEach
  void tearDown() {
    StructuredLogEventSupport.detachAppender(AddJointHolderCommandHandler.class, logAppender);
  }

  @Test
  void shouldAddJointHolder_whenCommandIsValid() {
    BankAccount bankAccount = BankAccountFixture.openActiveAccount(AccountHolderFixtures.JEFFERSON);

    when(bankAccountRepository.findById(bankAccount.getId()))
      .thenReturn(Optional.of(bankAccount));

    var command = new AddJointHolderCommand(
      bankAccount.getId(),
      AccountHolderFixtures.VIRGINIO.personalInfo(),
      AccountHolderFixtures.VIRGINIO.contactInfo(),
      AccountHolderFixtures.VIRGINIO.address()
    );

    commandHandler.handle(command);
    verify(bankAccountRepository).findById(bankAccount.getId());
    verify(bankAccountRepository).save(bankAccount);
    verifyNoMoreInteractions(bankAccountRepository);


    assertThat(bankAccount.getJointHolders())
      .hasSize(1);

    assertThat(StructuredLogEventSupport.lastEvent(logAppender, ILoggingEvent::getLevel))
      .isEqualTo(Level.INFO);
    assertThat(StructuredLogEventSupport.lastEventKeyValues(logAppender))
      .containsEntry(LogKey.EVENT_TYPE, BankAccountEventType.ADD_JOINT_HOLDER)
      .containsEntry(LogKey.OUTCOME, LogOutcome.SUCCESS)
      .containsEntry(BankAccountLogKey.BANK_ACCOUNT_ID, bankAccount.getId().value().toString());
    assertThat(StructuredLogEventSupport.eventTypes(logAppender))
      .allMatch(eventType -> !eventType.contains(".failed"));
  }

  @Test
  void shouldThrowBankAccountNotFoundException_whenBankAccountDoesNotExist() {
    var bankAccountId = BankAccountId.newId();
    when(bankAccountRepository.findById(bankAccountId))
      .thenReturn(Optional.empty());

    var command = new AddJointHolderCommand(
      bankAccountId,
      AccountHolderFixtures.VIRGINIO.personalInfo(),
      AccountHolderFixtures.VIRGINIO.contactInfo(),
      AccountHolderFixtures.VIRGINIO.address()
    );

    assertThatThrownBy(() -> commandHandler.handle(command))
      .isInstanceOf(BankAccountNotFoundException.class);

    verify(bankAccountRepository).findById(bankAccountId);
    verifyNoMoreInteractions(bankAccountRepository);

    assertThat(StructuredLogEventSupport.lastEvent(logAppender, ILoggingEvent::getLevel))
      .isEqualTo(Level.WARN);
    assertThat(StructuredLogEventSupport.lastEventKeyValues(logAppender))
      .containsEntry(LogKey.EVENT_TYPE, BankAccountEventType.ADD_JOINT_HOLDER)
      .containsEntry(LogKey.OUTCOME, LogOutcome.FAILURE)
      .containsEntry(LogKey.REASON, "not_found");
    assertThat(StructuredLogEventSupport.eventTypes(logAppender))
      .allMatch(eventType -> !eventType.contains(".failed"));
  }

  @Test
  void shouldThrowBankAccountNotActiveException_whenBankAccountIsNotActive() {
    var bankAccount = BankAccountFixture.openPendingAccount(AccountHolderFixtures.JEFFERSON);

    when(bankAccountRepository.findById(bankAccount.getId()))
      .thenReturn(Optional.of(bankAccount));

    var command = new AddJointHolderCommand(
      bankAccount.getId(),
      AccountHolderFixtures.VIRGINIO.personalInfo(),
      AccountHolderFixtures.VIRGINIO.contactInfo(),
      AccountHolderFixtures.VIRGINIO.address()
    );

    assertThatThrownBy(() -> commandHandler.handle(command))
      .isInstanceOf(BankAccountNotActiveException.class);

    verify(bankAccountRepository).findById(bankAccount.getId());
    verifyNoMoreInteractions(bankAccountRepository);

    assertThat(StructuredLogEventSupport.lastEvent(logAppender, ILoggingEvent::getLevel))
      .isEqualTo(Level.WARN);
    assertThat(StructuredLogEventSupport.lastEventKeyValues(logAppender))
      .containsEntry(LogKey.EVENT_TYPE, BankAccountEventType.ADD_JOINT_HOLDER)
      .containsEntry(LogKey.OUTCOME, LogOutcome.FAILURE)
      .containsEntry(LogKey.REASON, "not_active");
    assertThat(StructuredLogEventSupport.eventTypes(logAppender))
      .allMatch(eventType -> !eventType.contains(".failed"));
  }

  @Test
  void shouldThrowUnexpectedException_whenRepositoryThrowsUnexpectedException() {
    var bankAccountId = BankAccountId.newId();
    var exception = new IllegalStateException("database unavailable");

    when(bankAccountRepository.findById(bankAccountId))
      .thenThrow(exception);

    var command = new AddJointHolderCommand(
      bankAccountId,
      AccountHolderFixtures.VIRGINIO.personalInfo(),
      AccountHolderFixtures.VIRGINIO.contactInfo(),
      AccountHolderFixtures.VIRGINIO.address()
    );

    assertThatThrownBy(() -> commandHandler.handle(command))
      .isSameAs(exception);

    verify(bankAccountRepository).findById(bankAccountId);
    verifyNoMoreInteractions(bankAccountRepository);

    assertThat(StructuredLogEventSupport.lastEvent(logAppender, ILoggingEvent::getLevel))
      .isEqualTo(Level.ERROR);
    assertThat(StructuredLogEventSupport.lastEventKeyValues(logAppender))
      .containsEntry(LogKey.EVENT_TYPE, BankAccountEventType.ADD_JOINT_HOLDER)
      .containsEntry(LogKey.OUTCOME, LogOutcome.FAILURE)
      .containsEntry(LogKey.REASON, "internal_error");
    assertThat(StructuredLogEventSupport.eventTypes(logAppender))
      .allMatch(eventType -> !eventType.contains(".failed"));
  }
}
