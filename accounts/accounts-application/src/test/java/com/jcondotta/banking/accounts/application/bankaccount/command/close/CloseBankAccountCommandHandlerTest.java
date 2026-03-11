package com.jcondotta.banking.accounts.application.bankaccount.command.close;

import com.jcondotta.application.core.CommandHandler;
import com.jcondotta.banking.accounts.application.bankaccount.command.close.model.CloseBankAccountCommand;
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
class CloseBankAccountCommandHandlerTest {

  @Mock
  private BankAccountRepository bankAccountRepository;

  private CommandHandler<CloseBankAccountCommand> useCase;

  @BeforeEach
  void setUp() {
    useCase = new CloseBankAccountCommandHandler(bankAccountRepository);
  }

  @Test
  void shouldCloseBankAccount_whenCommandIsValid() {
    var bankAccount = BankAccountFixture.openActiveAccount(AccountHolderFixtures.JEFFERSON);
    bankAccount.pullEvents();

    when(bankAccountRepository.findById(bankAccount.getId()))
      .thenReturn(Optional.of(bankAccount));

    var command = new CloseBankAccountCommand(bankAccount.getId());

    useCase.handle(command);

    assertThat(bankAccount.getAccountStatus())
      .isEqualTo(AccountStatus.CLOSED);

    verify(bankAccountRepository).findById(bankAccount.getId());
    verify(bankAccountRepository).save(bankAccount);
    verifyNoMoreInteractions(bankAccountRepository);
  }

  @Test
  void shouldThrowBankAccountNotFoundException_whenBankAccountDoesNotExist() {
    var bankAccountId = BankAccountId.newId();

    when(bankAccountRepository.findById(bankAccountId))
      .thenReturn(Optional.empty());

    var command = new CloseBankAccountCommand(bankAccountId);

    assertThatThrownBy(() -> useCase.handle(command))
      .isInstanceOf(BankAccountNotFoundException.class);

    verify(bankAccountRepository).findById(bankAccountId);
    verifyNoMoreInteractions(bankAccountRepository);
  }
}