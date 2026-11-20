package com.jcondotta.banking.accounts.application.bankaccount.command.add_joint_holder;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.jcondotta.application.command.CommandHandler;
import com.jcondotta.application.idempotency.IdempotencyRecord;
import com.jcondotta.application.idempotency.IdempotentCommandFingerprint;
import com.jcondotta.application.idempotency.IdempotentSummary;
import com.jcondotta.application.idempotency.exceptions.IdempotencyKeyConflictException;
import com.jcondotta.application.idempotency.identity.IdempotencyKey;
import com.jcondotta.application.idempotency.repository.IdempotencyRepository;
import com.jcondotta.application.logging.StructuredLogEventSupport;
import com.jcondotta.banking.accounts.application.bankaccount.command.add_joint_holder.model.AddJointHolderCommand;
import com.jcondotta.banking.accounts.application.bankaccount.ports.output.BankAccountTransactionalWriter;
import com.jcondotta.banking.accounts.application.common.idempotency.IdempotencyService;
import com.jcondotta.banking.accounts.application.common.log.BankAccountLogKey;
import com.jcondotta.banking.accounts.application.common.log.BankAccountOperation;
import com.jcondotta.banking.accounts.domain.bankaccount.aggregate.BankAccount;
import com.jcondotta.banking.accounts.domain.bankaccount.exceptions.BankAccountNotActiveException;
import com.jcondotta.banking.accounts.domain.bankaccount.exceptions.BankAccountNotFoundException;
import com.jcondotta.banking.accounts.domain.bankaccount.identity.BankAccountId;
import com.jcondotta.banking.accounts.domain.bankaccount.repository.BankAccountRepository;
import com.jcondotta.banking.accounts.domain.testsupport.AccountHolderFixtures;
import com.jcondotta.banking.accounts.domain.testsupport.BankAccountFixture;
import com.jcondotta.scopedlogging.LogScopeAspect;
import com.jcondotta.scopedlogging.application.logscope.LogScopeExecutor;
import com.jcondotta.scopedlogging.application.logscope.ScopedLogContext;
import com.jcondotta.scopedlogging.domain.logging.LogKey;
import com.jcondotta.scopedlogging.infrastructure.slf4j.Slf4jLogEventPublisher;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddJointHolderCommandHandlerTest {

  @Mock
  private BankAccountRepository bankAccountRepository;

  @Mock
  private IdempotencyRepository<IdempotencyRecord<BankAccountId>> records;
  @Mock
  private BankAccountTransactionalWriter writer;

  @Mock
  private IdempotentCommandFingerprint fingerprint;

  private CommandHandler<AddJointHolderCommand> commandHandler;

  private ListAppender<ILoggingEvent> logAppender;

  @BeforeEach
  void setUp() {
    var logContext = new ScopedLogContext();
    when(fingerprint.hash(any())).thenReturn("request-hash");
    var proxy = new AspectJProxyFactory(new AddJointHolderCommandHandler(
      bankAccountRepository, new IdempotencyService(records, fingerprint, logContext), writer, logContext));
    proxy.setProxyTargetClass(true);
    proxy.addAspect(new LogScopeAspect(new LogScopeExecutor(logContext, new Slf4jLogEventPublisher())));
    commandHandler = proxy.getProxy();
    logAppender = StructuredLogEventSupport.attachAppender(AddJointHolderCommandHandler.class);
  }

  @AfterEach
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
      AccountHolderFixtures.VIRGINIO.address(),
      key()
    );

    commandHandler.handle(command);
    verify(bankAccountRepository).findById(bankAccount.getId());
    verify(writer).save(eq(bankAccount), any(IdempotentSummary.class));
    verifyNoMoreInteractions(bankAccountRepository);


    assertThat(bankAccount.getJointHolders())
      .hasSize(1);
    assertLog(Level.INFO, "success", null);
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
      AccountHolderFixtures.VIRGINIO.address(),
      key()
    );

    assertThatThrownBy(() -> commandHandler.handle(command))
      .isInstanceOf(BankAccountNotFoundException.class);
    assertLog(Level.ERROR, "failure", "not_found");

    verify(bankAccountRepository).findById(bankAccountId);
    verifyNoMoreInteractions(bankAccountRepository);
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
      AccountHolderFixtures.VIRGINIO.address(),
      key()
    );

    assertThatThrownBy(() -> commandHandler.handle(command))
      .isInstanceOf(BankAccountNotActiveException.class);
    assertLog(Level.ERROR, "failure", "not_active");

    verify(bankAccountRepository).findById(bankAccount.getId());
    verifyNoMoreInteractions(bankAccountRepository);
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
      AccountHolderFixtures.VIRGINIO.address(),
      key()
    );

    assertThatThrownBy(() -> commandHandler.handle(command))
      .isSameAs(exception);
    assertLog(Level.ERROR, "failure", "internal_error");

    verify(bankAccountRepository).findById(bankAccountId);
    verifyNoMoreInteractions(bankAccountRepository);
  }

  @Test
  void shouldSkipAccountLookupAndWrite_whenCommandIsReplayed() {
    var accountId = BankAccountId.newId();
    var command = new AddJointHolderCommand(accountId, AccountHolderFixtures.VIRGINIO.personalInfo(),
      AccountHolderFixtures.VIRGINIO.contactInfo(), AccountHolderFixtures.VIRGINIO.address(), key());
    when(records.find(command.idempotencyKey())).thenReturn(Optional.of(
      new IdempotencyRecord<>(command.idempotencyKey(), "request-hash", accountId)));

    commandHandler.handle(command);

    verifyNoInteractions(bankAccountRepository, writer);
    assertLog(Level.INFO, "success", null);
    assertThat(StructuredLogEventSupport.lastEventKeyValues(logAppender))
      .containsEntry(BankAccountLogKey.EXECUTION_RESULT, "replayed");
  }

  @Test
  void shouldLogConflictWithoutWriting_whenKeyIsReusedWithDifferentData() {
    var accountId = BankAccountId.newId();
    var command = new AddJointHolderCommand(accountId, AccountHolderFixtures.VIRGINIO.personalInfo(),
      AccountHolderFixtures.VIRGINIO.contactInfo(), AccountHolderFixtures.VIRGINIO.address(), key());
    when(records.find(command.idempotencyKey())).thenReturn(Optional.of(
      new IdempotencyRecord<>(command.idempotencyKey(), "different-hash", accountId)));

    assertThatThrownBy(() -> commandHandler.handle(command)).isInstanceOf(IdempotencyKeyConflictException.class);

    verifyNoInteractions(bankAccountRepository, writer);
    assertLog(Level.ERROR, "failure", "idempotency_conflict");
  }

  private void assertLog(Level level, String outcome, String reason) {
    assertThat(logAppender.list).hasSize(1);
    assertThat(StructuredLogEventSupport.lastEvent(logAppender, ILoggingEvent::getLevel)).isEqualTo(level);
    assertThat(StructuredLogEventSupport.lastEventKeyValues(logAppender))
      .containsEntry(LogKey.OPERATION, BankAccountOperation.ADD_JOINT_HOLDER)
      .containsEntry(LogKey.OUTCOME, outcome)
      .containsKey(LogKey.DURATION_MS);
    if (reason != null) {
      assertThat(StructuredLogEventSupport.lastEventKeyValues(logAppender)).containsEntry(LogKey.REASON, reason);
    }
  }

  private static IdempotencyKey key() {
    return IdempotencyKey.of(java.util.UUID.randomUUID());
  }
}
