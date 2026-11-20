package com.jcondotta.banking.ledger.domain.ledger_account.repository;

import com.jcondotta.banking.ledger.domain.ledger_account.aggregate.LedgerAccount;
import com.jcondotta.banking.ledger.domain.ledger_account.entries.LedgerEntry;
import com.jcondotta.banking.ledger.domain.ledger_account.value_objects.AccountReference;

import java.util.List;
import java.util.Optional;

public interface LedgerAccountRepository {

    void save(LedgerAccount account);

    void updateBalance(LedgerAccount account);

    void saveLedgerEntries(List<LedgerEntry> entries);

    Optional<LedgerAccount> findByAccountReference(AccountReference reference);
}
