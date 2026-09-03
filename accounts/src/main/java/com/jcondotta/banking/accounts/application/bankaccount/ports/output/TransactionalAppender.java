package com.jcondotta.banking.accounts.application.bankaccount.ports.output;

import com.jcondotta.application.TransactionContext;
import com.jcondotta.domain.core.AggregateRoot;

public interface TransactionalAppender {

  void append(AggregateRoot<?> aggregate, TransactionContext transactionContext);
}
