package com.jcondotta.bankaccounts.infrastructure.adapters.input.rest.lookupbankaccount.mapper;

import com.jcondotta.bankaccounts.infrastructure.adapters.input.rest.lookupbankaccount.*;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AccountHolderDetailsResponseMapper {

  AccountHolderDetailsResponse toResponse(AccountHolderDetails details);

  default PersonalInfoResponse map(PersonalInfoDetails details) {
    if (details == null) return null;

    return new PersonalInfoResponse(
      details.firstName(),
      details.lastName(),
      map(details.identityDocument()),
      details.dateOfBirth()
    );
  }

  default IdentityDocumentResponse map(IdentityDocumentDetails details) {
    if (details == null) return null;

    return new IdentityDocumentResponse(
      details.country(),
      details.type(),
      details.number()
    );
  }

  default AddressResponse map(AddressDetails address) {
    if (address == null) return null;

    return new AddressResponse(
      address.street(),
      address.streetNumber(),
      address.addressComplement(),
      address.postalCode(),
      address.city()
    );
  }

  default ContactInfoResponse map(ContactInfoDetails contactInfo) {
    if (contactInfo == null) return null;

    return new ContactInfoResponse(
      contactInfo.email(),
      contactInfo.phoneNumber()
    );
  }
}