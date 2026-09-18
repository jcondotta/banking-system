package com.jcondotta.banking.recipients.integration.bank_account;

import com.jcondotta.banking.recipients.domain.bank_account.BankAccount;
import com.jcondotta.banking.recipients.domain.bank_account.enums.BankAccountStatus;
import com.jcondotta.banking.recipients.domain.bank_account.repository.BankAccountRepository;
import com.jcondotta.banking.recipients.domain.recipient.identity.BankAccountId;
import com.jcondotta.banking.recipients.integration.testsupport.annotation.IntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
class BankAccountProjectionIT {

  @Autowired
  private BankAccountRepository bankAccountRepository;

  @Test
  void shouldNotOverwriteCurrentStatus_whenActivatedAccountIsRegisteredAgain() {
    var bankAccountId = BankAccountId.of(UUID.randomUUID());
    bankAccountRepository.save(new BankAccount(bankAccountId, BankAccountStatus.BLOCKED));

    bankAccountRepository.registerIfAbsent(BankAccount.active(bankAccountId));

    assertThat(bankAccountRepository.findById(bankAccountId))
      .contains(new BankAccount(bankAccountId, BankAccountStatus.BLOCKED));
  }

  @Test
  void shouldInsertOrUpdateStatus_whenStatusChanges() {
    var bankAccountId = BankAccountId.of(UUID.randomUUID());

    bankAccountRepository.save(BankAccount.active(bankAccountId));
    assertThat(bankAccountRepository.findById(bankAccountId))
      .contains(BankAccount.active(bankAccountId));

    bankAccountRepository.save(new BankAccount(bankAccountId, BankAccountStatus.CLOSED));
    assertThat(bankAccountRepository.findById(bankAccountId))
      .contains(new BankAccount(bankAccountId, BankAccountStatus.CLOSED));
  }
}
