package com.jcondotta.banking.recipients.infrastructure.adapters.output.persistence.entity;

import com.jcondotta.banking.recipients.domain.bank_account.enums.BankAccountStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "bank_accounts")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BankAccountEntity {

  @Id
  @Column(name = "id", nullable = false, updatable = false)
  private UUID id;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  private BankAccountStatus status;
}
