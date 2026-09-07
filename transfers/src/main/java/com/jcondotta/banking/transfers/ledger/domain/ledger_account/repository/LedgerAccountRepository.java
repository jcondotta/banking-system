package com.jcondotta.banking.transfers.ledger.domain.ledger_account.repository;

import com.jcondotta.banking.transfers.ledger.domain.ledger_account.aggregate.LedgerAccount;
import com.jcondotta.banking.transfers.ledger.domain.ledger_account.value_objects.AccountReference;

import java.util.Optional;

public interface LedgerAccountRepository {

    void save(LedgerAccount account);

    void updateBalance(LedgerAccount account);

    Optional<LedgerAccount> findByAccountReference(AccountReference reference);
}
