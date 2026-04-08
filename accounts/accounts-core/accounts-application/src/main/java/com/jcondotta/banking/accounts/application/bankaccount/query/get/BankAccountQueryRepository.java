package com.jcondotta.banking.accounts.application.bankaccount.query.get;

import com.jcondotta.banking.accounts.application.bankaccount.query.get.model.BankAccountSummary;
import com.jcondotta.banking.accounts.domain.bankaccount.identity.BankAccountId;

import java.util.Optional;

public interface BankAccountQueryRepository {

  Optional<BankAccountSummary> findById(BankAccountId bankAccountId);

}