package com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.lookupbankaccount.mapper;

import com.jcondotta.banking.accounts.application.bankaccount.query.get.model.*;
import com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.lookupbankaccount.*;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AccountHolderDetailsResponseMapper {

  AccountHolderDetailsResponse toResponse(AccountHolderSummary accountHolderSummary);

  default PersonalInfoResponse map(PersonalInfoSummary personalInfoSummary) {
    if (personalInfoSummary == null) return null;

    return new PersonalInfoResponse(
      personalInfoSummary.firstName(),
      personalInfoSummary.lastName(),
      map(personalInfoSummary.identityDocument()),
      personalInfoSummary.dateOfBirth()
    );
  }

  default IdentityDocumentResponse map(IdentityDocumentSummary identityDocumentSummary) {
    if (identityDocumentSummary == null) return null;

    return new IdentityDocumentResponse(
      identityDocumentSummary.country(),
      identityDocumentSummary.type(),
      identityDocumentSummary.number()
    );
  }

  default AddressResponse map(AddressSummary addressSummary) {
    if (addressSummary == null) return null;

    return new AddressResponse(
      addressSummary.street(),
      addressSummary.streetNumber(),
      addressSummary.addressComplement(),
      addressSummary.postalCode(),
      addressSummary.city()
    );
  }

  default ContactInfoResponse map(ContactInfoSummary contactInfoSummary) {
    if (contactInfoSummary == null) return null;

    return new ContactInfoResponse(
      contactInfoSummary.email(),
      contactInfoSummary.phoneNumber()
    );
  }
}