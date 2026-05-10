package com.jcondotta.banking.accounts.application.bankaccount.command.open;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.jcondotta.application.command.CommandHandlerWithResult;
import com.jcondotta.banking.accounts.application.common.log.BankAccountEventType;
import com.jcondotta.application.logging.LogKey;
import com.jcondotta.banking.accounts.application.common.log.BankAccountLogKey;
import com.jcondotta.application.logging.LogOutcome;
import com.jcondotta.application.logging.StructuredLogEventSupport;
import com.jcondotta.banking.accounts.application.bankaccount.argument_provider.AccountTypeAndCurrencyArgumentsProvider;
import com.jcondotta.banking.accounts.application.bankaccount.command.open.model.OpenBankAccountCommand;
import com.jcondotta.banking.accounts.application.bankaccount.ports.output.facade.IbanGeneratorFacade;
import com.jcondotta.banking.accounts.domain.bankaccount.aggregate.BankAccount;
import com.jcondotta.banking.accounts.domain.bankaccount.enums.AccountType;
import com.jcondotta.banking.accounts.domain.bankaccount.enums.Currency;
import com.jcondotta.banking.accounts.domain.bankaccount.identity.BankAccountId;
import com.jcondotta.banking.accounts.domain.bankaccount.repository.BankAccountRepository;
import com.jcondotta.banking.accounts.domain.testsupport.AccountHolderFixtures;
import com.jcondotta.banking.accounts.domain.testsupport.BankAccountFixture;
import com.jcondotta.banking.accounts.domain.bankaccount.value_objects.Iban;
import com.jcondotta.domain.exception.DomainException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OpenBankAccountCommandHandlerTest {

  private static final Iban GENERATED_IBAN = BankAccountFixture.VALID_IBAN;

  @Mock
  private BankAccountRepository bankAccountRepository;

  @Mock
  private IbanGeneratorFacade ibanGeneratorFacade;

  @Captor
  private ArgumentCaptor<BankAccount> bankAccountCaptor;

  private ListAppender<ILoggingEvent> logAppender;

  private CommandHandlerWithResult<OpenBankAccountCommand, BankAccountId> commandHandler;

  @BeforeEach
  void setUp() {
    commandHandler = new OpenBankAccountCommandHandler(
      bankAccountRepository,
      ibanGeneratorFacade
    );
    logAppender = StructuredLogEventSupport.attachAppender(OpenBankAccountCommandHandler.class);
  }

  @org.junit.jupiter.api.AfterEach
  void tearDown() {
    StructuredLogEventSupport.detachAppender(OpenBankAccountCommandHandler.class, logAppender);
  }

  @ParameterizedTest
  @ArgumentsSource(AccountTypeAndCurrencyArgumentsProvider.class)
  void shouldOpenBankAccount_whenCommandIsValid(AccountType accountType, Currency currency) {
    when(ibanGeneratorFacade.generate()).thenReturn(GENERATED_IBAN);

    var personalInfo = AccountHolderFixtures.JEFFERSON.personalInfo();
    var contactInfo = AccountHolderFixtures.JEFFERSON.contactInfo();
    var address = AccountHolderFixtures.JEFFERSON.address();

    var command = new OpenBankAccountCommand(
      personalInfo,
      contactInfo,
      address,
      accountType,
      currency
    );

    var bankAccountId = commandHandler.handle(command);

    verify(ibanGeneratorFacade).generate();
    verify(bankAccountRepository).save(bankAccountCaptor.capture());
    verifyNoMoreInteractions(ibanGeneratorFacade, bankAccountRepository);

    assertThat(bankAccountId).isEqualTo(bankAccountCaptor.getValue().getId());

    assertThat(bankAccountCaptor.getValue())
      .satisfies(bankAccount -> {
        assertThat(bankAccount.getId()).isNotNull();
        assertThat(bankAccount.getAccountType()).isEqualTo(accountType);
        assertThat(bankAccount.getCurrency()).isEqualTo(currency);
        assertThat(bankAccount.getIban()).isEqualTo(GENERATED_IBAN);
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

    assertThat(StructuredLogEventSupport.lastEvent(logAppender, ILoggingEvent::getLevel))
      .isEqualTo(Level.INFO);
    assertThat(StructuredLogEventSupport.lastEventKeyValues(logAppender))
      .containsEntry(LogKey.EVENT_TYPE, BankAccountEventType.OPEN)
      .containsEntry(LogKey.OUTCOME, LogOutcome.SUCCESS)
      .containsEntry(BankAccountLogKey.BANK_ACCOUNT_ID, bankAccountCaptor.getValue().getId().value().toString())
      .containsEntry(BankAccountLogKey.MASKED_IBAN, GENERATED_IBAN.masked());
    assertThat(StructuredLogEventSupport.eventTypes(logAppender))
      .allMatch(eventType -> !eventType.contains(".failed"));
  }

  @Test
  void shouldThrowDomainExceptionAndLogMaskedIban_whenRepositoryThrowsDomainException() {
    when(ibanGeneratorFacade.generate()).thenReturn(GENERATED_IBAN);

    var exception = new TestDomainException();

    doThrow(exception)
      .when(bankAccountRepository)
      .save(any(BankAccount.class));

    var command = command(AccountType.SAVINGS, Currency.USD);

    assertThatThrownBy(() -> commandHandler.handle(command))
      .isSameAs(exception);

    verify(ibanGeneratorFacade).generate();
    verify(bankAccountRepository).save(any(BankAccount.class));
    verifyNoMoreInteractions(ibanGeneratorFacade, bankAccountRepository);

    assertThat(StructuredLogEventSupport.lastEvent(logAppender, ILoggingEvent::getLevel))
      .isEqualTo(Level.WARN);
    assertThat(StructuredLogEventSupport.lastEventKeyValues(logAppender))
      .containsEntry(LogKey.EVENT_TYPE, BankAccountEventType.OPEN)
      .containsEntry(LogKey.OUTCOME, LogOutcome.FAILURE)
      .containsEntry(LogKey.REASON, "domain_error")
      .containsEntry(BankAccountLogKey.MASKED_IBAN, GENERATED_IBAN.masked());
    assertThat(StructuredLogEventSupport.eventTypes(logAppender))
      .allMatch(eventType -> !eventType.contains(".failed"));
  }

  @Test
  void shouldThrowUnexpectedExceptionAndLogMaskedIban_whenRepositoryThrowsUnexpectedException() {
    when(ibanGeneratorFacade.generate()).thenReturn(GENERATED_IBAN);

    var exception = new IllegalStateException("database unavailable");

    doThrow(exception)
      .when(bankAccountRepository)
      .save(any(BankAccount.class));

    var command = command(AccountType.SAVINGS, Currency.USD);

    assertThatThrownBy(() -> commandHandler.handle(command))
      .isSameAs(exception);

    verify(ibanGeneratorFacade).generate();
    verify(bankAccountRepository).save(any(BankAccount.class));
    verifyNoMoreInteractions(ibanGeneratorFacade, bankAccountRepository);

    assertThat(StructuredLogEventSupport.lastEvent(logAppender, ILoggingEvent::getLevel))
      .isEqualTo(Level.ERROR);
    assertThat(StructuredLogEventSupport.lastEventKeyValues(logAppender))
      .containsEntry(LogKey.EVENT_TYPE, BankAccountEventType.OPEN)
      .containsEntry(LogKey.OUTCOME, LogOutcome.FAILURE)
      .containsEntry(LogKey.REASON, "internal_error")
      .containsEntry(BankAccountLogKey.MASKED_IBAN, GENERATED_IBAN.masked());
    assertThat(StructuredLogEventSupport.eventTypes(logAppender))
      .allMatch(eventType -> !eventType.contains(".failed"));
  }

  @Test
  void shouldOmitMaskedIban_whenUnexpectedExceptionHappensBeforeIbanIsGenerated() {
    var exception = new IllegalStateException("iban generator unavailable");

    when(ibanGeneratorFacade.generate())
      .thenThrow(exception);

    var command = command(AccountType.SAVINGS, Currency.USD);

    assertThatThrownBy(() -> commandHandler.handle(command))
      .isSameAs(exception);

    verify(ibanGeneratorFacade).generate();
    verifyNoInteractions(bankAccountRepository);

    assertThat(StructuredLogEventSupport.lastEvent(logAppender, ILoggingEvent::getLevel))
      .isEqualTo(Level.ERROR);
    assertThat(StructuredLogEventSupport.lastEventKeyValues(logAppender))
      .containsEntry(LogKey.EVENT_TYPE, BankAccountEventType.OPEN)
      .containsEntry(LogKey.OUTCOME, LogOutcome.FAILURE)
      .containsEntry(LogKey.REASON, "internal_error")
      .doesNotContainKey(BankAccountLogKey.MASKED_IBAN);
    assertThat(StructuredLogEventSupport.eventTypes(logAppender))
      .allMatch(eventType -> !eventType.contains(".failed"));
  }

  private static OpenBankAccountCommand command(AccountType accountType, Currency currency) {
    return new OpenBankAccountCommand(
      AccountHolderFixtures.JEFFERSON.personalInfo(),
      AccountHolderFixtures.JEFFERSON.contactInfo(),
      AccountHolderFixtures.JEFFERSON.address(),
      accountType,
      currency
    );
  }

  private static final class TestDomainException extends DomainException {

    private TestDomainException() {
      super("domain error");
    }
  }
}
