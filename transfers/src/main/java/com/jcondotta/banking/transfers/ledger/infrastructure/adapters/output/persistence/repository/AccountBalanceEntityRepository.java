package com.jcondotta.banking.transfers.ledger.infrastructure.adapters.output.persistence.repository;

import com.jcondotta.banking.transfers.ledger.infrastructure.adapters.output.persistence.entity.AccountBalanceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public interface AccountBalanceEntityRepository extends JpaRepository<AccountBalanceEntity, UUID> {

    @Modifying
    @Transactional
    @Query("UPDATE AccountBalanceEntity ab SET ab.bookedAmount = :bookedAmount, ab.heldAmount = :heldAmount, ab.version = ab.version + 1, ab.updatedAt = :updatedAt WHERE ab.accountId = :accountId")
    void updateBalance(
            @Param("accountId") UUID accountId,
            @Param("bookedAmount") BigDecimal bookedAmount,
            @Param("heldAmount") BigDecimal heldAmount,
            @Param("updatedAt") Instant updatedAt
    );
}
