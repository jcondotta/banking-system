package com.jcondotta.banking.recipients.application.bankaccount.command.register;

import com.jcondotta.banking.recipients.domain.recipient.aggregate.BankAccount;
import com.jcondotta.banking.recipients.domain.recipient.identity.BankAccountId;
import com.jcondotta.banking.recipients.domain.recipient.repository.BankAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

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
    var command = new RegisterBankAccountCommand(BANK_ACCOUNT_ID);

    commandHandler.handle(command);

    ArgumentCaptor<BankAccount> bankAccountCaptor = ArgumentCaptor.forClass(BankAccount.class);

    verify(bankAccountRepository).save(bankAccountCaptor.capture());
    verifyNoMoreInteractions(bankAccountRepository);

    BankAccount savedBankAccount = bankAccountCaptor.getValue();

    assertThat(savedBankAccount.getId()).isEqualTo(BANK_ACCOUNT_ID);
    assertThat(savedBankAccount.getAccountStatus().isActive()).isTrue();
    assertThat(savedBankAccount.getRecipients()).isEmpty();
  }
}