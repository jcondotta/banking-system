package com.jcondotta.banking.transfers.ledger.infrastructure.adapters.output.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Persistable;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "account_balance")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountBalanceEntity implements Persistable<UUID> {

    @Id
    @Column(name = "account_id", nullable = false, updatable = false)
    private UUID accountId;

    @Column(name = "booked_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal bookedAmount;

    @Column(name = "held_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal heldAmount;

    @Column(name = "version", nullable = false)
    private long version;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Override
    @Transient
    public UUID getId() {
        return accountId;
    }

    @Override
    @Transient
    public boolean isNew() {
        return true;
    }
}
