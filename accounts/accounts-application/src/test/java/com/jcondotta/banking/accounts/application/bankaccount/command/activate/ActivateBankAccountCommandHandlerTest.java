package com.jcondotta.banking.accounts.application.bankaccount.command.activate;

import com.jcondotta.application.core.CommandHandler;
import com.jcondotta.banking.accounts.application.bankaccount.command.activate.model.ActivateBankAccountCommand;
import com.jcondotta.banking.accounts.domain.bankaccount.enums.AccountStatus;
import com.jcondotta.banking.accounts.domain.bankaccount.exceptions.BankAccountNotFoundException;
import com.jcondotta.banking.accounts.domain.bankaccount.identity.BankAccountId;
import com.jcondotta.banking.accounts.domain.bankaccount.repository.BankAccountRepository;
import com.jcondotta.banking.accounts.domain.bankaccount.testsupport.AccountHolderFixtures;
import com.jcondotta.banking.accounts.domain.bankaccount.testsupport.BankAccountFixture;
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
class ActivateBankAccountCommandHandlerTest {

  @Mock
  private BankAccountRepository bankAccountRepository;

  private CommandHandler<ActivateBankAccountCommand> commandHandler;

  @BeforeEach
  void setUp() {
    commandHandler = new ActivateBankAccountCommandHandler(bankAccountRepository);
  }

  @Test
  void shouldActivateBankAccount_whenCommandIsValid() {
    var bankAccount = BankAccountFixture.openPendingAccount(AccountHolderFixtures.JEFFERSON);
    bankAccount.pullEvents();

    when(bankAccountRepository.findById(bankAccount.getId()))
      .thenReturn(Optional.of(bankAccount));

    var command = new ActivateBankAccountCommand(bankAccount.getId());

    commandHandler.handle(command);

    assertThat(bankAccount.getAccountStatus()).isEqualTo(AccountStatus.ACTIVE);

    verify(bankAccountRepository).findById(bankAccount.getId());
    verify(bankAccountRepository).save(bankAccount);
    verifyNoMoreInteractions(bankAccountRepository);
  }

  @Test
  void shouldThrowBankAccountNotFoundException_whenAccountDoesNotExist() {
    var bankAccountId = BankAccountId.newId();
    when(bankAccountRepository.findById(bankAccountId))
      .thenReturn(Optional.empty());

    var command = new ActivateBankAccountCommand(bankAccountId);

    assertThatThrownBy(() -> commandHandler.handle(command))
      .isInstanceOf(BankAccountNotFoundException.class);

    verify(bankAccountRepository).findById(bankAccountId);
    verifyNoMoreInteractions(bankAccountRepository);
  }
}