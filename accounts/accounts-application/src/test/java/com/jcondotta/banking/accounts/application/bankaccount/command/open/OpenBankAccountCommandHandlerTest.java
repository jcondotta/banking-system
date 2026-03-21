package com.jcondotta.banking.accounts.application.bankaccount.command.open;

import com.jcondotta.application.core.command.CommandHandlerWithResult;
import com.jcondotta.banking.accounts.application.bankaccount.argument_provider.AccountTypeAndCurrencyArgumentsProvider;
import com.jcondotta.banking.accounts.application.bankaccount.command.open.model.OpenBankAccountCommand;
import com.jcondotta.banking.accounts.application.bankaccount.ports.output.facade.IbanGeneratorFacade;
import com.jcondotta.banking.accounts.domain.bankaccount.aggregate.BankAccount;
import com.jcondotta.banking.accounts.domain.bankaccount.enums.AccountType;
import com.jcondotta.banking.accounts.domain.bankaccount.enums.Currency;
import com.jcondotta.banking.accounts.domain.bankaccount.identity.BankAccountId;
import com.jcondotta.banking.accounts.domain.bankaccount.repository.BankAccountRepository;
import com.jcondotta.banking.accounts.domain.bankaccount.testsupport.AccountHolderFixtures;
import com.jcondotta.banking.accounts.domain.bankaccount.testsupport.BankAccountFixture;
import com.jcondotta.banking.accounts.domain.bankaccount.value_objects.Iban;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
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

  private CommandHandlerWithResult<OpenBankAccountCommand, BankAccountId> commandHandler;

  @BeforeEach
  void setUp() {
    commandHandler = new OpenBankAccountCommandHandler(
      bankAccountRepository,
      ibanGeneratorFacade
    );
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

    commandHandler.handle(command);

    verify(ibanGeneratorFacade).generate();
    verify(bankAccountRepository).save(bankAccountCaptor.capture());
    verifyNoMoreInteractions(ibanGeneratorFacade, bankAccountRepository);

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
  }
}
