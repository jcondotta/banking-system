package com.jcondotta.banking.accounts.application.bankaccount.command.block;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.jcondotta.application.command.CommandHandler;
import com.jcondotta.banking.accounts.application.common.log.BankAccountEventType;
import com.jcondotta.application.logging.LogKey;
import com.jcondotta.banking.accounts.application.common.log.BankAccountLogKey;
import com.jcondotta.application.logging.LogOutcome;
import com.jcondotta.application.logging.StructuredLogEventSupport;
import com.jcondotta.banking.accounts.application.bankaccount.command.block.model.BlockBankAccountCommand;
import com.jcondotta.banking.accounts.domain.bankaccount.enums.AccountStatus;
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
class BlockBankAccountCommandHandlerTest {

  @Mock
  private BankAccountRepository bankAccountRepository;

  private ListAppender<ILoggingEvent> logAppender;

  private CommandHandler<BlockBankAccountCommand> commandHandler;

  @BeforeEach
  void setUp() {
    commandHandler = new BlockBankAccountCommandHandler(bankAccountRepository);
    logAppender = StructuredLogEventSupport.attachAppender(BlockBankAccountCommandHandler.class);
  }

  @org.junit.jupiter.api.AfterEach
  void tearDown() {
    StructuredLogEventSupport.detachAppender(BlockBankAccountCommandHandler.class, logAppender);
  }

  @Test
  void shouldBlockBankAccount_whenCommandIsValid() {
    var bankAccount = BankAccountFixture.openActiveAccount(AccountHolderFixtures.JEFFERSON);
    bankAccount.pullEvents();

    when(bankAccountRepository.findById(bankAccount.getId()))
      .thenReturn(Optional.of(bankAccount));

    var command = new BlockBankAccountCommand(bankAccount.getId());

    commandHandler.handle(command);

    assertThat(bankAccount.getAccountStatus())
      .isEqualTo(AccountStatus.BLOCKED);

    verify(bankAccountRepository).findById(bankAccount.getId());
    verify(bankAccountRepository).save(bankAccount);
    verifyNoMoreInteractions(bankAccountRepository);

    assertThat(StructuredLogEventSupport.lastEvent(logAppender, ILoggingEvent::getLevel))
      .isEqualTo(Level.INFO);
    assertThat(StructuredLogEventSupport.lastEventKeyValues(logAppender))
      .containsEntry(LogKey.EVENT_TYPE, BankAccountEventType.BLOCK)
      .containsEntry(LogKey.OUTCOME, LogOutcome.SUCCESS)
      .containsEntry(BankAccountLogKey.BANK_ACCOUNT_ID, bankAccount.getId().value().toString());
    assertThat(StructuredLogEventSupport.eventTypes(logAppender))
      .allMatch(eventType -> !eventType.contains(".failed"));
  }

  @Test
  void shouldThrowBankAccountNotFoundException_whenAccountDoesNotExist() {

    var bankAccountId = BankAccountId.newId();

    when(bankAccountRepository.findById(bankAccountId))
      .thenReturn(Optional.empty());

    var command = new BlockBankAccountCommand(bankAccountId);

    assertThatThrownBy(() -> commandHandler.handle(command))
      .isInstanceOf(BankAccountNotFoundException.class);

    verify(bankAccountRepository).findById(bankAccountId);
    verifyNoMoreInteractions(bankAccountRepository);

    assertThat(StructuredLogEventSupport.lastEvent(logAppender, ILoggingEvent::getLevel))
      .isEqualTo(Level.WARN);
    assertThat(StructuredLogEventSupport.lastEventKeyValues(logAppender))
      .containsEntry(LogKey.EVENT_TYPE, BankAccountEventType.BLOCK)
      .containsEntry(LogKey.OUTCOME, LogOutcome.FAILURE)
      .containsEntry(LogKey.REASON, "not_found");
    assertThat(StructuredLogEventSupport.eventTypes(logAppender))
      .allMatch(eventType -> !eventType.contains(".failed"));
  }

  @Test
  void shouldThrowUnexpectedException_whenRepositoryThrowsUnexpectedException() {
    var bankAccountId = BankAccountId.newId();
    var exception = new IllegalStateException("database unavailable");

    when(bankAccountRepository.findById(bankAccountId))
      .thenThrow(exception);

    var command = new BlockBankAccountCommand(bankAccountId);

    assertThatThrownBy(() -> commandHandler.handle(command))
      .isSameAs(exception);

    verify(bankAccountRepository).findById(bankAccountId);
    verifyNoMoreInteractions(bankAccountRepository);

    assertThat(StructuredLogEventSupport.lastEvent(logAppender, ILoggingEvent::getLevel))
      .isEqualTo(Level.ERROR);
    assertThat(StructuredLogEventSupport.lastEventKeyValues(logAppender))
      .containsEntry(LogKey.EVENT_TYPE, BankAccountEventType.BLOCK)
      .containsEntry(LogKey.OUTCOME, LogOutcome.FAILURE)
      .containsEntry(LogKey.REASON, "internal_error");
    assertThat(StructuredLogEventSupport.eventTypes(logAppender))
      .allMatch(eventType -> !eventType.contains(".failed"));
  }
}
