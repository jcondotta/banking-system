package com.jcondotta.banking.recipients.domain.recipient.repository;

import com.jcondotta.banking.recipients.domain.recipient.aggregate.BankAccount;
import com.jcondotta.banking.recipients.domain.recipient.identity.BankAccountId;

import java.util.Optional;

public interface BankAccountRepository {

  Optional<BankAccount> findById(BankAccountId id);
  void save(BankAccount bankAccount);

}