package com.jcondotta.banking.ledger.infrastructure.adapters.output.persistence.mapper;

import com.jcondotta.banking.ledger.domain.ledger_account.entries.LedgerEntry;
import com.jcondotta.banking.ledger.infrastructure.adapters.output.persistence.entity.LedgerEntryEntity;
import org.springframework.stereotype.Component;

@Component
public class LedgerEntryEntityMapper {

    public LedgerEntryEntity toEntity(LedgerEntry entry) {
        return LedgerEntryEntity.builder()
                .id(entry.id())
                .accountId(entry.accountId().value())
                .transferId(entry.transferId())
                .movementType(entry.movementType())
                .amount(entry.amount())
                .currency(entry.currency())
                .createdAt(entry.createdAt())
                .build();
    }
}
