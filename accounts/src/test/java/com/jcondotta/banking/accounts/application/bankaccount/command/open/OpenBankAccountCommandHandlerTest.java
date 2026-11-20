package com.jcondotta.banking.accounts.application.bankaccount.command.open;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.jcondotta.application.command.CommandHandlerWithResult;
import com.jcondotta.application.idempotency.IdempotencyRecord;
import com.jcondotta.application.idempotency.IdempotentCommandFingerprint;
import com.jcondotta.application.idempotency.IdempotentSummary;
import com.jcondotta.application.idempotency.exceptions.IdempotencyKeyConflictException;
import com.jcondotta.application.idempotency.identity.IdempotencyKey;
import com.jcondotta.application.idempotency.repository.IdempotencyRepository;
import com.jcondotta.application.logging.StructuredLogEventSupport;
import com.jcondotta.banking.accounts.application.bankaccount.command.open.model.OpenBankAccountCommand;
import com.jcondotta.banking.accounts.application.bankaccount.ports.output.BankAccountTransactionalWriter;
import com.jcondotta.banking.accounts.application.common.idempotency.IdempotencyService;
import com.jcondotta.banking.accounts.application.common.log.BankAccountLogKey;
import com.jcondotta.banking.accounts.application.common.log.BankAccountOperation;
import com.jcondotta.banking.accounts.domain.bankaccount.aggregate.BankAccount;
import com.jcondotta.banking.accounts.domain.bankaccount.enums.AccountType;
import com.jcondotta.banking.accounts.domain.bankaccount.enums.Currency;
import com.jcondotta.banking.accounts.domain.bankaccount.identity.BankAccountId;
import com.jcondotta.banking.accounts.domain.testsupport.AccountHolderFixtures;
import com.jcondotta.domain.exception.DomainException;
import com.jcondotta.scopedlogging.LogScopeAspect;
import com.jcondotta.scopedlogging.application.logscope.LogScopeExecutor;
import com.jcondotta.scopedlogging.application.logscope.ScopedLogContext;
import com.jcondotta.scopedlogging.domain.logging.LogKey;
import com.jcondotta.scopedlogging.infrastructure.slf4j.Slf4jLogEventPublisher;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OpenBankAccountCommandHandlerTest {

  @Mock
  private BankAccountTransactionalWriter bankAccountRepository;

  @Mock
  private IdempotencyRepository<IdempotencyRecord<BankAccountId>> records;

  @Mock
  private IdempotentCommandFingerprint fingerprint;

  @Captor
  private ArgumentCaptor<BankAccount> bankAccountCaptor;

  private CommandHandlerWithResult<OpenBankAccountCommand, BankAccountId> commandHandler;

  private ListAppender<ILoggingEvent> logAppender;

  @BeforeEach
  void setUp() {
    var logContext = new ScopedLogContext();
    when(fingerprint.hash(any())).thenReturn("request-hash");
    var proxy = new AspectJProxyFactory(new OpenBankAccountCommandHandler(
      new IdempotencyService(records, fingerprint, logContext), bankAccountRepository, logContext));
    proxy.setProxyTargetClass(true);
    proxy.addAspect(new LogScopeAspect(new LogScopeExecutor(logContext, new Slf4jLogEventPublisher())));
    commandHandler = proxy.getProxy();
    logAppender = StructuredLogEventSupport.attachAppender(OpenBankAccountCommandHandler.class);
  }

  @AfterEach
  void tearDown() {
    StructuredLogEventSupport.detachAppender(OpenBankAccountCommandHandler.class, logAppender);
  }

  @Test
  void shouldOpenCheckingEuroBankAccount_whenCommandIsValid() {
    shouldOpenBankAccount_whenCommandIsValid(AccountType.CHECKING, Currency.EUR);
  }

  @Test
  void shouldOpenCheckingUsdBankAccount_whenCommandIsValid() {
    shouldOpenBankAccount_whenCommandIsValid(AccountType.CHECKING, Currency.USD);
  }

  @Test
  void shouldOpenSavingsEuroBankAccount_whenCommandIsValid() {
    shouldOpenBankAccount_whenCommandIsValid(AccountType.SAVINGS, Currency.EUR);
  }

  @Test
  void shouldOpenSavingsUsdBankAccount_whenCommandIsValid() {
    shouldOpenBankAccount_whenCommandIsValid(AccountType.SAVINGS, Currency.USD);
  }

  private void shouldOpenBankAccount_whenCommandIsValid(AccountType accountType, Currency currency) {
    var personalInfo = AccountHolderFixtures.JEFFERSON.personalInfo();
    var contactInfo = AccountHolderFixtures.JEFFERSON.contactInfo();
    var address = AccountHolderFixtures.JEFFERSON.address();

    var command = new OpenBankAccountCommand(
      personalInfo,
      contactInfo,
      address,
      accountType,
      currency,
      key()
    );

    var bankAccountId = commandHandler.handle(command);

    verify(bankAccountRepository).save(bankAccountCaptor.capture(), any(IdempotentSummary.class));
    verifyNoMoreInteractions(bankAccountRepository);

    assertThat(bankAccountId).isEqualTo(bankAccountCaptor.getValue().getId());
    assertLog(Level.INFO, "success", null);

    assertThat(bankAccountCaptor.getValue())
      .satisfies(bankAccount -> {
        assertThat(bankAccount.getId()).isNotNull();
        assertThat(bankAccount.getAccountType()).isEqualTo(accountType);
        assertThat(bankAccount.getCurrency()).isEqualTo(currency);
        assertThat(bankAccount.getIban()).isEmpty();
        assertThat(bankAccount.getAccountStatus()).isEqualTo(BankAccount.ACCOUNT_STATUS_ON_OPENING);
        assertThat(bankAccount.getCreatedAt()).isNotNull();
        assertThat(bankAccount.getActiveHolders())
          .hasSize(1)
          .singleElement()
          .satisfies(accountHolder -> {
            assertThat(accountHolder.getId()).isNotNull();
            assertThat(accountHolder.getPersonalInfo()).isEqualTo(personalInfo);
            assertThat(accountHolder.getContactInfo()).isEqualTo(contactInfo);
            assertThat(accountHolder.getAddress()).isEqualTo(address);
            assertThat(accountHolder.isPrimary()).isTrue();
            assertThat(accountHolder.getCreatedAt()).isNotNull();
          });
      });
  }

  @Test
  void shouldThrowDomainException_whenRepositoryThrowsDomainException() {
    var exception = new TestDomainException();

    doThrow(exception)
      .when(bankAccountRepository)
      .save(any(BankAccount.class), any(IdempotentSummary.class));

    var command = command(AccountType.SAVINGS, Currency.USD);

    assertThatThrownBy(() -> commandHandler.handle(command))
      .isSameAs(exception);
    assertLog(Level.ERROR, "failure", "domain_error");

    verify(bankAccountRepository).save(any(BankAccount.class), any(IdempotentSummary.class));
    verifyNoMoreInteractions(bankAccountRepository);
  }

  @Test
  void shouldThrowUnexpectedException_whenRepositoryThrowsUnexpectedException() {
    var exception = new IllegalStateException("database unavailable");

    doThrow(exception)
      .when(bankAccountRepository)
      .save(any(BankAccount.class), any(IdempotentSummary.class));

    var command = command(AccountType.SAVINGS, Currency.USD);

    assertThatThrownBy(() -> commandHandler.handle(command))
      .isSameAs(exception);
    assertLog(Level.ERROR, "failure", "internal_error");

    verify(bankAccountRepository).save(any(BankAccount.class), any(IdempotentSummary.class));
    verifyNoMoreInteractions(bankAccountRepository);
  }

  private static OpenBankAccountCommand command(AccountType accountType, Currency currency) {
    return new OpenBankAccountCommand(
      AccountHolderFixtures.JEFFERSON.personalInfo(),
      AccountHolderFixtures.JEFFERSON.contactInfo(),
      AccountHolderFixtures.JEFFERSON.address(),
      accountType,
      currency,
      key()
    );
  }

  @Test
  void shouldReturnExistingAccountWithoutWriting_whenCommandIsReplayed() {
    var command = command(AccountType.CHECKING, Currency.EUR);
    var accountId = BankAccountId.newId();
    when(records.find(command.idempotencyKey())).thenReturn(java.util.Optional.of(
      new IdempotencyRecord<>(command.idempotencyKey(), "request-hash", accountId)));

    assertThat(commandHandler.handle(command)).isEqualTo(accountId);

    verifyNoInteractions(bankAccountRepository);
    assertLog(Level.INFO, "success", null);
    assertThat(StructuredLogEventSupport.lastEventKeyValues(logAppender))
      .containsEntry(BankAccountLogKey.EXECUTION_RESULT, "replayed")
      .containsEntry(BankAccountLogKey.BANK_ACCOUNT_ID, accountId.value().toString());
  }

  @Test
  void shouldLogConflictWithoutWriting_whenKeyIsReusedWithDifferentData() {
    var command = command(AccountType.CHECKING, Currency.EUR);
    when(records.find(command.idempotencyKey())).thenReturn(java.util.Optional.of(
      new IdempotencyRecord<>(command.idempotencyKey(), "different-hash", BankAccountId.newId())));

    assertThatThrownBy(() -> commandHandler.handle(command)).isInstanceOf(IdempotencyKeyConflictException.class);

    verifyNoInteractions(bankAccountRepository);
    assertLog(Level.ERROR, "failure", "idempotency_conflict");
  }

  private void assertLog(Level level, String outcome, String reason) {
    assertThat(logAppender.list).hasSize(1);
    assertThat(StructuredLogEventSupport.lastEvent(logAppender, ILoggingEvent::getLevel)).isEqualTo(level);
    assertThat(StructuredLogEventSupport.lastEventKeyValues(logAppender))
      .containsEntry(LogKey.OPERATION, BankAccountOperation.OPEN)
      .containsEntry(LogKey.OUTCOME, outcome)
      .containsKey(LogKey.DURATION_MS);
    if (reason != null) {
      assertThat(StructuredLogEventSupport.lastEventKeyValues(logAppender)).containsEntry(LogKey.REASON, reason);
    }
  }

  private static IdempotencyKey key() {
    return IdempotencyKey.of(java.util.UUID.randomUUID());
  }

  private static final class TestDomainException extends DomainException {

    private TestDomainException() {
      super("domain error");
    }
  }
}
