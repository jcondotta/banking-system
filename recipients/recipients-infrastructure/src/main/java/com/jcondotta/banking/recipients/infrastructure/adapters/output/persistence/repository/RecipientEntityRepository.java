package com.jcondotta.banking.recipients.infrastructure.adapters.output.persistence.repository;

import com.jcondotta.banking.recipients.infrastructure.adapters.output.persistence.entity.RecipientEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface RecipientEntityRepository extends JpaRepository<RecipientEntity, UUID> {

    Page<RecipientEntity> findByBankAccountId(UUID bankAccountId, Pageable pageable);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("DELETE FROM RecipientEntity r WHERE r.id = :id AND r.version = :version")
    int deleteIfVersionMatches(@Param("id") UUID id, @Param("version") long version);
}
