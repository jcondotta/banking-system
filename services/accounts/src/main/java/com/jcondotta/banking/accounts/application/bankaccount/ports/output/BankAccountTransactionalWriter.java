package com.jcondotta.banking.accounts.application.bankaccount.ports.output;

import com.jcondotta.application.idempotency.IdempotentSummary;
import com.jcondotta.banking.accounts.domain.bankaccount.aggregate.BankAccount;
import com.jcondotta.banking.accounts.domain.bankaccount.identity.BankAccountId;

public interface BankAccountTransactionalWriter {

    void save(BankAccount bankAccount, IdempotentSummary<BankAccountId> summary);
}
