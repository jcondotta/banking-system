package com.jcondotta.banking.ledger.infrastructure.adapters.output.persistence.repository;

import com.jcondotta.banking.ledger.infrastructure.adapters.output.persistence.entity.LedgerAccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface LedgerAccountEntityRepository extends JpaRepository<LedgerAccountEntity, UUID> {

    Optional<LedgerAccountEntity> findByAccountReference(UUID accountReference);
}
