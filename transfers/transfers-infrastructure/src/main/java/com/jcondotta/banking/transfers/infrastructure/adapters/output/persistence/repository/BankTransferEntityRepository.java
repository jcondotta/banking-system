package com.jcondotta.banking.transfers.infrastructure.adapters.output.persistence.repository;

import com.jcondotta.banking.transfers.infrastructure.adapters.output.persistence.entity.BankTransferEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BankTransferEntityRepository extends JpaRepository<BankTransferEntity, UUID> {
}
