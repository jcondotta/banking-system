package com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.entity;

import com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.enums.EntityType;
import lombok.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@DynamoDbBean
public class BankingEntity {

  private String partitionKey;
  private String sortKey;
  private EntityType entityType;

  private UUID bankAccountId;
  private String accountType;
  private String currency;
  private String iban;
  private String accountStatus;

  private UUID accountHolderId;
  private String holderFirstName;
  private String holderLastName;
  private String documentType;
  private String documentCountry;
  private String documentNumber;
  private LocalDate dateOfBirth;
  private String email;
  private String phoneNumber;

  private String street;
  private String streetNumber;
  private String addressComplement;
  private String postalCode;
  private String city;

  private String holderType;
  private Instant createdAt;

  @DynamoDbPartitionKey
  public String getPartitionKey() {
    return partitionKey;
  }

  @DynamoDbSortKey
  public String getSortKey() {
    return sortKey;
  }

  public boolean isBankAccount() {
    return entityType == EntityType.BANK_ACCOUNT;
  }

  public boolean isAccountHolder() {
    return entityType == EntityType.ACCOUNT_HOLDER;
  }
}