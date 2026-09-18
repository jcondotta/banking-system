package com.jcondotta.banking.recipients.infrastructure.adapters.output.persistence.repository;

import com.jcondotta.banking.recipients.infrastructure.adapters.output.persistence.entity.BankAccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface BankAccountEntityRepository extends JpaRepository<BankAccountEntity, UUID> {

  @Modifying
  @Query(value = """
    INSERT INTO bank_accounts (id, status)
    VALUES (:id, :status)
    ON CONFLICT (id) DO NOTHING
    """, nativeQuery = true)
  void insertIfAbsent(@Param("id") UUID id, @Param("status") String status);

  @Modifying
  @Query(value = """
    INSERT INTO bank_accounts (id, status)
    VALUES (:id, :status)
    ON CONFLICT (id) DO UPDATE SET status = EXCLUDED.status
    """, nativeQuery = true)
  void upsert(@Param("id") UUID id, @Param("status") String status);
}
