package com.jcondotta.banking.recipients.domain.bank_account.repository;

import com.jcondotta.banking.recipients.domain.bank_account.BankAccount;
import com.jcondotta.banking.recipients.domain.recipient.identity.BankAccountId;

import java.util.Optional;

public interface BankAccountRepository {

  Optional<BankAccount> findById(BankAccountId bankAccountId);

  void registerIfAbsent(BankAccount bankAccount);

  void save(BankAccount bankAccount);
}
