package com.jcondotta.banking.transfers.infrastructure.adapters.output.persistence.repository;

import com.jcondotta.banking.transfers.domain.bank_transfer.enums.TransferStatus;
import com.jcondotta.banking.transfers.infrastructure.adapters.output.persistence.entity.BankTransferEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

public interface BankTransferEntityRepository extends JpaRepository<BankTransferEntity, UUID> {

    @Modifying
    @Transactional
    @Query("UPDATE BankTransferEntity bt SET bt.transferStatus = :status WHERE bt.id = :id")
    int updateStatus(@Param("id") UUID id, @Param("status") TransferStatus status);
}
