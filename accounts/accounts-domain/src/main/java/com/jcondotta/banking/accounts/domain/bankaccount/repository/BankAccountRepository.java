package com.jcondotta.banking.accounts.domain.bankaccount.repository;

import com.jcondotta.banking.accounts.domain.bankaccount.aggregate.BankAccount;
import com.jcondotta.banking.accounts.domain.bankaccount.identity.BankAccountId;
import com.jcondotta.domain.repository.AggregateRepository;

public interface BankAccountRepository extends AggregateRepository<BankAccount, BankAccountId> {

}