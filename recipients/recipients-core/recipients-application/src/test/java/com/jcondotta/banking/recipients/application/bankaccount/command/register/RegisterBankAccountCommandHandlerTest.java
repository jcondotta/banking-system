package com.jcondotta.banking.recipients.application.bankaccount.command.register;

import com.jcondotta.banking.recipients.domain.recipient.aggregate.BankAccount;
import com.jcondotta.banking.recipients.domain.recipient.aggregate.Recipients;
import com.jcondotta.banking.recipients.domain.recipient.enums.AccountStatus;
import com.jcondotta.banking.recipients.domain.recipient.exceptions.BankAccountNotActiveException;
import com.jcondotta.banking.recipients.domain.recipient.identity.BankAccountId;
import com.jcondotta.banking.recipients.domain.recipient.repository.BankAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.params.provider.EnumSource.Mode.EXCLUDE;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegisterBankAccountCommandHandlerTest {

  private static final BankAccountId BANK_ACCOUNT_ID = BankAccountId.of(UUID.randomUUID());

  @Mock
  private BankAccountRepository bankAccountRepository;

  private RegisterBankAccountCommandHandler commandHandler;

  @BeforeEach
  void setUp() {
    commandHandler = new RegisterBankAccountCommandHandler(bankAccountRepository);
  }

  @Test
  void shouldRegisterBankAccount_whenCommandIsValid() {
    when(bankAccountRepository.findById(BANK_ACCOUNT_ID)).thenReturn(Optional.empty());

    var command = new RegisterBankAccountCommand(BANK_ACCOUNT_ID);
    commandHandler.handle(command);

    ArgumentCaptor<BankAccount> bankAccountCaptor = ArgumentCaptor.forClass(BankAccount.class);

    verify(bankAccountRepository).findById(BANK_ACCOUNT_ID);
    verify(bankAccountRepository).save(bankAccountCaptor.capture());
    verifyNoMoreInteractions(bankAccountRepository);

    BankAccount savedBankAccount = bankAccountCaptor.getValue();

    assertThat(savedBankAccount.getId()).isEqualTo(BANK_ACCOUNT_ID);
    assertThat(savedBankAccount.getAccountStatus().isActive()).isTrue();
    assertThat(savedBankAccount.getActiveRecipients()).isEmpty();
  }

  @Test
  void shouldBeIdempotent_whenBankAccountAlreadyExists() {
    when(bankAccountRepository.findById(BANK_ACCOUNT_ID))
      .thenReturn(Optional.empty())
      .thenReturn(Optional.of(BankAccount.register(BANK_ACCOUNT_ID)));

    var command = new RegisterBankAccountCommand(BANK_ACCOUNT_ID);
    commandHandler.handle(command);
    commandHandler.handle(command);

    verify(bankAccountRepository, times(2)).findById(BANK_ACCOUNT_ID);
    verify(bankAccountRepository).save(any(BankAccount.class));
    verifyNoMoreInteractions(bankAccountRepository);
  }

  @ParameterizedTest
  @EnumSource(value = AccountStatus.class, names = "ACTIVE", mode = EXCLUDE)
  void shouldThrowBankAccountNotActiveException_whenBankAccountAlreadyExistsAndIsNotActive(AccountStatus status) {
    var bankAccount = BankAccount.restore(BANK_ACCOUNT_ID, status, Recipients.empty());

    when(bankAccountRepository.findById(BANK_ACCOUNT_ID))
      .thenReturn(Optional.of(bankAccount));

    var command = new RegisterBankAccountCommand(BANK_ACCOUNT_ID);

    assertThatThrownBy(() -> commandHandler.handle(command))
      .isInstanceOf(BankAccountNotActiveException.class)
      .hasMessage(new BankAccountNotActiveException(status).getMessage());

    verify(bankAccountRepository).findById(BANK_ACCOUNT_ID);
    verifyNoMoreInteractions(bankAccountRepository);
  }
}
