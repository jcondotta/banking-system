package com.jcondotta.banking.transfers.ledger.infrastructure.adapters.output.persistence;

import com.jcondotta.banking.transfers.ledger.domain.ledger_account.aggregate.LedgerAccount;
import com.jcondotta.banking.transfers.ledger.domain.ledger_account.repository.LedgerAccountRepository;
import com.jcondotta.banking.transfers.ledger.domain.ledger_account.value_objects.AccountReference;
import com.jcondotta.banking.transfers.ledger.infrastructure.adapters.output.persistence.mapper.LedgerAccountEntityMapper;
import com.jcondotta.banking.transfers.ledger.infrastructure.adapters.output.persistence.repository.AccountBalanceEntityRepository;
import com.jcondotta.banking.transfers.ledger.infrastructure.adapters.output.persistence.repository.LedgerAccountEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class LedgerAccountPostgresRepository implements LedgerAccountRepository {

    private final LedgerAccountEntityRepository ledgerAccountEntityRepository;
    private final AccountBalanceEntityRepository accountBalanceEntityRepository;
    private final LedgerAccountEntityMapper mapper;

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
    @Transactional(readOnly = true)
    public Optional<LedgerAccount> findByAccountReference(AccountReference reference) {
        return ledgerAccountEntityRepository.findByAccountReference(reference.value())
                .flatMap(accountEntity ->
                        accountBalanceEntityRepository.findById(accountEntity.getId())
                                .map(balanceEntity -> mapper.toDomain(accountEntity, balanceEntity))
                );
    }
}
