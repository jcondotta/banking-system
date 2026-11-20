package com.jcondotta.banking.ledger.infrastructure.adapters.output.persistence;

import com.jcondotta.banking.ledger.domain.ledger_account.aggregate.LedgerAccount;
import com.jcondotta.banking.ledger.domain.ledger_account.entries.LedgerEntry;
import com.jcondotta.banking.ledger.domain.ledger_account.repository.LedgerAccountRepository;
import com.jcondotta.banking.ledger.domain.ledger_account.value_objects.AccountReference;
import com.jcondotta.banking.ledger.infrastructure.adapters.output.persistence.mapper.LedgerAccountEntityMapper;
import com.jcondotta.banking.ledger.infrastructure.adapters.output.persistence.mapper.LedgerEntryEntityMapper;
import com.jcondotta.banking.ledger.infrastructure.adapters.output.persistence.repository.AccountBalanceEntityRepository;
import com.jcondotta.banking.ledger.infrastructure.adapters.output.persistence.repository.LedgerAccountEntityRepository;
import com.jcondotta.banking.ledger.infrastructure.adapters.output.persistence.repository.LedgerEntryEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class LedgerAccountPostgresRepository implements LedgerAccountRepository {

    private final LedgerAccountEntityRepository ledgerAccountEntityRepository;
    private final AccountBalanceEntityRepository accountBalanceEntityRepository;
    private final LedgerEntryEntityRepository ledgerEntryEntityRepository;
    private final LedgerAccountEntityMapper mapper;
    private final LedgerEntryEntityMapper ledgerEntryEntityMapper;

    @Override
    @Transactional
    public void save(LedgerAccount account) {
        ledgerAccountEntityRepository.saveAndFlush(mapper.toEntity(account));
        accountBalanceEntityRepository.saveAndFlush(mapper.toBalanceEntity(account));
    }

    @Override
    @Transactional
    public void updateBalance(LedgerAccount account) {
        accountBalanceEntityRepository.updateBalance(
                account.getId().value(),
                account.getBalance().booked().amount(),
                account.getBalance().held().amount(),
                Instant.now()
        );
    }

    @Override
    @Transactional
    public void saveLedgerEntries(List<LedgerEntry> entries) {
        var entities = entries.stream()
                .map(ledgerEntryEntityMapper::toEntity)
                .toList();
        ledgerEntryEntityRepository.saveAllAndFlush(entities);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<LedgerAccount> findByAccountReference(AccountReference reference) {
        return ledgerAccountEntityRepository.findByAccountReference(reference.value())
                .flatMap(accountEntity ->
                        accountBalanceEntityRepository.findById(accountEntity.getId())
                                .map(balanceEntity -> mapper.toDomain(accountEntity, balanceEntity))
                );
    }
}
