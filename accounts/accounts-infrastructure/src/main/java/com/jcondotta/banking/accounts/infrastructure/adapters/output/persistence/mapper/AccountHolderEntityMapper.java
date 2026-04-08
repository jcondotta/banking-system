package com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.mapper;

import com.jcondotta.banking.accounts.domain.bankaccount.aggregate.AccountHolder;
import com.jcondotta.banking.accounts.domain.bankaccount.enums.DocumentCountry;
import com.jcondotta.banking.accounts.domain.bankaccount.enums.DocumentType;
import com.jcondotta.banking.accounts.domain.bankaccount.enums.HolderType;
import com.jcondotta.banking.accounts.domain.bankaccount.identity.AccountHolderId;
import com.jcondotta.banking.accounts.domain.bankaccount.identity.BankAccountId;
import com.jcondotta.banking.accounts.domain.bankaccount.value_objects.address.*;
import com.jcondotta.banking.accounts.domain.bankaccount.value_objects.contact.ContactInfo;
import com.jcondotta.banking.accounts.domain.bankaccount.value_objects.contact.Email;
import com.jcondotta.banking.accounts.domain.bankaccount.value_objects.contact.PhoneNumber;
import com.jcondotta.banking.accounts.domain.bankaccount.value_objects.personal.*;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.entity.AccountHolderEntityKey;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.entity.BankingEntity;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.enums.EntityType;
import org.mapstruct.Mapper;

import java.util.Optional;

@Mapper(componentModel = "spring")
public interface AccountHolderEntityMapper {

  default BankingEntity toAccountHolderEntity(BankAccountId bankAccountId, AccountHolder accountHolder) {
    return BankingEntity.builder()
      .partitionKey(AccountHolderEntityKey.partitionKey(bankAccountId))
      .sortKey(AccountHolderEntityKey.sortKey(accountHolder.getId()))
      .entityType(EntityType.ACCOUNT_HOLDER)
      .bankAccountId(bankAccountId.value())
      .accountHolderId(accountHolder.getId().value())
      .holderFirstName(accountHolder.getPersonalInfo().holderName().firstName())
      .holderLastName(accountHolder.getPersonalInfo().holderName().lastName())
      .documentType(accountHolder.getPersonalInfo().identityDocument().type().toString())
      .documentCountry(accountHolder.getPersonalInfo().identityDocument().country().toString())
      .documentNumber(accountHolder.getPersonalInfo().identityDocument().number().value())
      .dateOfBirth(accountHolder.getPersonalInfo().dateOfBirth().value())
      .email(accountHolder.getContactInfo().email().value())
      .phoneNumber(accountHolder.getContactInfo().phoneNumber().value())
      .street(accountHolder.getAddress().street().value())
      .streetNumber(accountHolder.getAddress().streetNumber().value())
      .addressComplement(Optional.ofNullable(accountHolder.getAddress().addressComplement())
        .map(AddressComplement::value)
        .orElse(null))
      .postalCode(accountHolder.getAddress().postalCode().value())
      .city(accountHolder.getAddress().city().value())
      .holderType(accountHolder.getAccountHolderType().name())
      .createdAt(accountHolder.getCreatedAt())
      .build();
  }

  default AccountHolder toDomain(BankingEntity entity) {
    PersonalInfo personalInfo = PersonalInfo.of(
      AccountHolderName.of(entity.getHolderFirstName(), entity.getHolderLastName()),
      IdentityDocument.of(
        DocumentCountry.SPAIN,
        DocumentType.valueOf(entity.getDocumentType()),
        DocumentNumber.of(entity.getDocumentNumber())
      ),
      DateOfBirth.of(entity.getDateOfBirth())
    );

    ContactInfo contactInfo = ContactInfo.of(
      Email.of(entity.getEmail()),
      PhoneNumber.of(entity.getPhoneNumber())
    );

    Address address = new Address(
      Street.of(entity.getStreet()),
      StreetNumber.of(entity.getStreetNumber()),
      entity.getAddressComplement() != null ? AddressComplement.ofNullable(entity.getAddressComplement()) : null,
      PostalCode.of(entity.getPostalCode()),
      City.of(entity.getCity())
    );

    return AccountHolder.restore(
      AccountHolderId.of(entity.getAccountHolderId()),
      personalInfo,
      contactInfo,
      address,
      HolderType.valueOf(entity.getHolderType()),
      entity.getCreatedAt()
    );
  }
}