package com.jcondotta.banking.accounts.domain.bankaccount.aggregate;

import com.jcondotta.banking.accounts.domain.bankaccount.enums.HolderType;
import com.jcondotta.banking.accounts.domain.bankaccount.identity.AccountHolderId;
import com.jcondotta.banking.accounts.domain.bankaccount.validation.AccountHolderErrors;
import com.jcondotta.banking.accounts.domain.bankaccount.value_objects.address.Address;
import com.jcondotta.banking.accounts.domain.bankaccount.value_objects.contact.ContactInfo;
import com.jcondotta.banking.accounts.domain.bankaccount.value_objects.personal.PersonalInfo;
import com.jcondotta.domain.core.Entity;

import java.time.Instant;

import static com.jcondotta.domain.support.Preconditions.required;

public final class AccountHolder extends Entity<AccountHolderId> {

  private final PersonalInfo personalInfo;
  private final ContactInfo contactInfo;
  private final Address address;
  private final HolderType holderType;
  private final Instant createdAt;

  private Instant deactivatedAt;

  private AccountHolder(
    AccountHolderId id,
    PersonalInfo personalInfo,
    ContactInfo contactInfo,
    Address address,
    HolderType holderType,
    Instant createdAt
  ) {
    super(required(id, AccountHolderErrors.ID_MUST_BE_PROVIDED));
    this.personalInfo = required(personalInfo, AccountHolderErrors.PERSONAL_INFO_MUST_BE_PROVIDED);
    this.contactInfo = required(contactInfo, AccountHolderErrors.CONTACT_INFO_MUST_BE_PROVIDED);
    this.address = required(address, AccountHolderErrors.ADDRESS_MUST_BE_PROVIDED);
    this.holderType = required(holderType, AccountHolderErrors.ACCOUNT_HOLDER_TYPE_MUST_BE_PROVIDED);
    this.createdAt = required(createdAt, AccountHolderErrors.CREATED_AT_MUST_BE_PROVIDED);
  }

  static AccountHolder createPrimary(PersonalInfo personalInfo, ContactInfo contactInfo, Address address, Instant createdAt) {
    return create(personalInfo, contactInfo, address, HolderType.PRIMARY, createdAt);
  }

  static AccountHolder createJoint(PersonalInfo personalInfo, ContactInfo contactInfo, Address address, Instant createdAt) {
    return create(personalInfo, contactInfo, address, HolderType.JOINT, createdAt);
  }

  static AccountHolder create(PersonalInfo personalInfo, ContactInfo contactInfo, Address address, HolderType holderType, Instant createdAt) {
    return new AccountHolder(AccountHolderId.newId(), personalInfo, contactInfo, address, holderType, createdAt);
  }

  public static AccountHolder restore(
    AccountHolderId accountHolderId,
    PersonalInfo personalInfo,
    ContactInfo contactInfo,
    Address address,
    HolderType holderType,
    Instant createdAt) {

    return new AccountHolder(accountHolderId, personalInfo, contactInfo, address, holderType, createdAt);
  }

  void deactivate() {
    if (this.deactivatedAt != null) {
      return;
    }

    this.deactivatedAt = Instant.now();
  }

  public boolean isActive() {
    return deactivatedAt == null;
  }

  public PersonalInfo getPersonalInfo() {
    return personalInfo;
  }

  public ContactInfo getContactInfo() {
    return contactInfo;
  }

  public Address getAddress() {
    return address;
  }

  public HolderType getAccountHolderType() {
    return holderType;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public Instant getDeactivatedAt() {
    return deactivatedAt;
  }

  public boolean isPrimary() {
    return holderType.isPrimary();
  }

  public boolean isJoint() {
    return holderType.isJoint();
  }
}
