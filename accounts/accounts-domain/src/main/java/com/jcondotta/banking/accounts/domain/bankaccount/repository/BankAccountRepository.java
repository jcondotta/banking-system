package com.jcondotta.banking.accounts.domain.bankaccount.repository;

import com.jcondotta.banking.accounts.domain.bankaccount.aggregate.BankAccount;
import com.jcondotta.banking.accounts.domain.bankaccount.identity.BankAccountId;

import java.util.Optional;

public interface BankAccountRepository {

  Optional<BankAccount> findById(BankAccountId id);
  void save(BankAccount bankAccount);

}