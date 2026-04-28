package com.jcondotta.banking.accounts.application.bankaccount.command.add_joint_holder;

import com.jcondotta.application.command.CommandHandler;
import com.jcondotta.banking.accounts.application.bankaccount.command.add_joint_holder.model.AddJointHolderCommand;
import com.jcondotta.banking.accounts.domain.bankaccount.aggregate.BankAccount;
import com.jcondotta.banking.accounts.domain.bankaccount.exceptions.BankAccountNotFoundException;
import com.jcondotta.banking.accounts.domain.bankaccount.identity.BankAccountId;
import com.jcondotta.banking.accounts.domain.bankaccount.repository.BankAccountRepository;
import com.jcondotta.banking.recipients.domain.testsupport.AccountHolderFixtures;
import com.jcondotta.banking.recipients.domain.testsupport.BankAccountFixture;
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

  private CommandHandler<AddJointHolderCommand> commandHandler;

  @BeforeEach
  void setUp() {
    commandHandler = new AddJointHolderCommandHandler(bankAccountRepository);
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
  }
}