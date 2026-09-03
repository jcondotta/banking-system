package com.jcondotta.banking.accounts.domain.bankaccount.repository;

import com.jcondotta.banking.accounts.domain.bankaccount.aggregate.BankAccount;
import com.jcondotta.banking.accounts.domain.bankaccount.identity.BankAccountId;
import com.jcondotta.banking.accounts.domain.bankaccount.value_objects.Iban;
import com.jcondotta.domain.core.repository.AggregateRepository;

import java.util.Optional;

public interface BankAccountRepository extends AggregateRepository<BankAccount, BankAccountId> {

  Optional<BankAccount> findByIban(Iban iban);
}