package com.jcondotta.banking.transfers.infrastructure.adapters.output.persistence.entity;

import com.jcondotta.banking.transfers.domain.bank_transfer.enums.TransferStatus;
import com.jcondotta.banking.transfers.domain.bank_transfer.enums.TransferType;
import com.jcondotta.banking.transfers.domain.shared.value_objects.Currency;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.Version;
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
@Table(name = "bank_transfers")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BankTransferEntity implements Persistable<UUID> {

  @Id
  @Column(name = "id", nullable = false, updatable = false)
  private UUID id;

  @Column(name = "sender_account_id", nullable = false, updatable = false)
  private UUID senderAccountId;

  @Column(name = "recipient_account_id", nullable = false, updatable = false)
  private UUID recipientAccountId;

  @Column(name = "amount", nullable = false, precision = 19, scale = 2)
  private BigDecimal amount;

  @Enumerated(EnumType.STRING)
  @Column(name = "currency", nullable = false, length = 3)
  private Currency currency;

  @Enumerated(EnumType.STRING)
  @Column(name = "transfer_type", nullable = false, length = 50)
  private TransferType transferType;

  @Enumerated(EnumType.STRING)
  @Column(name = "transfer_status", nullable = false, length = 50)
  private TransferStatus transferStatus;

  @Column(name = "reference", length = 255)
  private String reference;

  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  @Version
  @Column(name = "version", nullable = false)
  private Long version;

  @Override
  @Transient
  public boolean isNew() {
    return version == null;
  }
}
