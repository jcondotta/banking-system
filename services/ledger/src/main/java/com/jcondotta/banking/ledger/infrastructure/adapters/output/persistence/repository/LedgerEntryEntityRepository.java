package com.jcondotta.banking.ledger.infrastructure.adapters.output.persistence.repository;

import com.jcondotta.banking.ledger.infrastructure.adapters.output.persistence.entity.LedgerEntryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LedgerEntryEntityRepository extends JpaRepository<LedgerEntryEntity, UUID> {
}
