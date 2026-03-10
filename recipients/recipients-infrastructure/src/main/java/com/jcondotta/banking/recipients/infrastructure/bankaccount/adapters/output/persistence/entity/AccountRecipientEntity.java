package com.jcondotta.banking.recipients.infrastructure.bankaccount.adapters.output.persistence.entity;

import com.jcondotta.banking.recipients.infrastructure.bankaccount.adapters.output.persistence.enums.EntityType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

import java.time.Instant;
import java.util.UUID;

@Setter
@DynamoDbBean
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class AccountRecipientEntity {

  private String partitionKey;
  private String sortKey;
  private EntityType entityType;

  private UUID bankAccountId;
  private String accountStatus;
  private UUID recipientId;
  private String recipientName;
  private String iban;
  private String recipientStatus;
  private Instant createdAt;

  @DynamoDbPartitionKey
  @DynamoDbAttribute("partitionKey")
  public String getPartitionKey() {
    return partitionKey;
  }

  @DynamoDbSortKey
  @DynamoDbAttribute("sortKey")
  public String getSortKey() {
    return sortKey;
  }

  @DynamoDbAttribute("entityType")
  public EntityType getEntityType() {
    return entityType;
  }

  @DynamoDbAttribute("bankAccountId")
  public UUID getBankAccountId() {
    return bankAccountId;
  }

  @DynamoDbAttribute("accountStatus")
  public String getAccountStatus() {
    return accountStatus;
  }

  @DynamoDbAttribute("recipientId")
  public UUID getRecipientId() {
    return recipientId;
  }

  @DynamoDbAttribute("recipientName")
  public String getRecipientName() {
    return recipientName;
  }

  @DynamoDbAttribute("iban")
  public String getIban() {
    return iban;
  }

  @DynamoDbAttribute("recipientStatus")
  public String getRecipientStatus() {
    return recipientStatus;
  }

  @DynamoDbAttribute("createdAt")
  public Instant getCreatedAt() {
    return createdAt;
  }

  public boolean isBankAccount() {
    return entityType == EntityType.BANK_ACCOUNT;
  }

  public boolean isRecipient() {
    return entityType == EntityType.RECIPIENT;
  }
}
