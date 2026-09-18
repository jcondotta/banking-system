package com.jcondotta.banking.recipients.application.bank_account.command.update;

import com.jcondotta.banking.recipients.domain.bank_account.BankAccount;
import com.jcondotta.banking.recipients.domain.bank_account.enums.BankAccountStatus;
import com.jcondotta.banking.recipients.domain.bank_account.repository.BankAccountRepository;
import com.jcondotta.banking.recipients.domain.recipient.identity.BankAccountId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UpdateBankAccountStatusCommandHandlerTest {

  @Mock
  private BankAccountRepository repository;

  @Test
  void shouldSaveCurrentBankAccountStatus() {
    var bankAccountId = BankAccountId.of(UUID.randomUUID());
    var handler = new UpdateBankAccountStatusCommandHandler(repository);

    handler.handle(new UpdateBankAccountStatusCommand(bankAccountId, BankAccountStatus.BLOCKED));

    verify(repository).save(new BankAccount(bankAccountId, BankAccountStatus.BLOCKED));
  }
}
