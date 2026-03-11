package com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.entity;

import com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.enums.EntityType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Setter
@DynamoDbBean
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class BankingEntity {

  private String partitionKey;
  private String sortKey;
  private EntityType entityType;

  private UUID bankAccountId;
  private String accountType;
  private String currency;
  private String iban;
  private String status;

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

  @DynamoDbAttribute("id")
  public UUID getBankAccountId() {
    return bankAccountId;
  }

  @DynamoDbAttribute("accountType")
  public String getAccountType() {
    return accountType;
  }

  @DynamoDbAttribute("currency")
  public String getCurrency() {
    return currency;
  }

  @DynamoDbAttribute("iban")
  public String getIban() {
    return iban;
  }

  @DynamoDbAttribute("accountStatus")
  public String getStatus() {
    return status;
  }

  @DynamoDbAttribute("accountHolderId")
  public UUID getAccountHolderId() {
    return accountHolderId;
  }

  @DynamoDbAttribute("holderFirstName")
  public String getHolderFirstName() {
    return holderFirstName;
  }

  @DynamoDbAttribute("holderLastName")
  public String getHolderLastName() {
    return holderLastName;
  }

  @DynamoDbAttribute("documentType")
  public String getDocumentType() {
    return documentType;
  }

  @DynamoDbAttribute("documentCountry")
  public String getDocumentCountry() {
    return documentCountry;
  }

  @DynamoDbAttribute("documentNumber")
  public String getDocumentNumber() {
    return documentNumber;
  }

  @DynamoDbAttribute("dateOfBirth")
  public LocalDate getDateOfBirth() {
    return dateOfBirth;
  }

  @DynamoDbAttribute("email")
  public String getEmail() {
    return email;
  }

  @DynamoDbAttribute("phoneNumber")
  public String getPhoneNumber() {
    return phoneNumber;
  }

  @DynamoDbAttribute("street")
  public String getStreet() {
    return street;
  }

  @DynamoDbAttribute("streetNumber")
  public String getStreetNumber() {
    return streetNumber;
  }

  @DynamoDbAttribute("addressComplement")
  public String getAddressComplement() {
    return addressComplement;
  }

  @DynamoDbAttribute("postalCode")
  public String getPostalCode() {
    return postalCode;
  }

  @DynamoDbAttribute("city")
  public String getCity() {
    return city;
  }

  @DynamoDbAttribute("holderType")
  public String getHolderType() {
    return holderType;
  }


  @DynamoDbAttribute("createdAt")
  public Instant getCreatedAt() {
    return createdAt;
  }

  public boolean isBankAccount() {
    return entityType == EntityType.BANK_ACCOUNT;
  }

  public boolean isAccountHolder() {
    return entityType == EntityType.ACCOUNT_HOLDER;
  }
}