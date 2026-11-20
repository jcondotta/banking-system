package com.jcondotta.banking.recipients.application.bank_account.command.register;

import com.jcondotta.banking.recipients.domain.bank_account.BankAccount;
import com.jcondotta.banking.recipients.domain.bank_account.repository.BankAccountRepository;
import com.jcondotta.banking.recipients.domain.recipient.identity.BankAccountId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RegisterActivatedBankAccountCommandHandlerTest {

  @Mock
  private BankAccountRepository repository;

  @Captor
  private ArgumentCaptor<BankAccount> bankAccountCaptor;

  @Test
  void shouldRegisterActiveBankAccount() {
    var bankAccountId = BankAccountId.of(UUID.randomUUID());
    var handler = new RegisterActivatedBankAccountCommandHandler(repository);

    handler.handle(new RegisterActivatedBankAccountCommand(bankAccountId));

    verify(repository).registerIfAbsent(bankAccountCaptor.capture());
    assertThat(bankAccountCaptor.getValue()).isEqualTo(BankAccount.active(bankAccountId));
  }
}
