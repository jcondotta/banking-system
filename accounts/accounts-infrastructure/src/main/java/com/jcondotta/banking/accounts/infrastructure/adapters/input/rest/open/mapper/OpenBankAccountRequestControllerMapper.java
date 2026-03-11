package com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.open.mapper;

import com.jcondotta.banking.accounts.application.bankaccount.command.open.model.OpenBankAccountCommand;
import com.jcondotta.banking.accounts.domain.bankaccount.enums.DocumentCountry;
import com.jcondotta.banking.accounts.domain.bankaccount.enums.DocumentType;
import com.jcondotta.banking.accounts.domain.bankaccount.value_objects.address.*;
import com.jcondotta.banking.accounts.domain.bankaccount.value_objects.contact.ContactInfo;
import com.jcondotta.banking.accounts.domain.bankaccount.value_objects.contact.Email;
import com.jcondotta.banking.accounts.domain.bankaccount.value_objects.contact.PhoneNumber;
import com.jcondotta.banking.accounts.domain.bankaccount.value_objects.personal.*;
import com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.open.model.OpenBankAccountRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface OpenBankAccountRequestControllerMapper {

    @Mapping(
      target = "personalInfo",
      expression = "java(toPersonalInfo(request))"
    )
    @Mapping(
      target = "contactInfo",
      expression = "java(toContactInfo(request))"
    )
    @Mapping(
      target = "address",
      expression = "java(toAddress(request))"
    )
    @Mapping(target = "accountType", source = "accountType")
    @Mapping(target = "currency", source = "currency")
    OpenBankAccountCommand toCommand(OpenBankAccountRequest request);

    default PersonalInfo toPersonalInfo(OpenBankAccountRequest request) {
        return PersonalInfo.of(
          AccountHolderName.of(request.accountHolder().personalInfo().firstName(), request.accountHolder().personalInfo().lastName()),
          IdentityDocument.of(
            DocumentCountry.valueOf(request.accountHolder().personalInfo().identityDocument().country()),
            DocumentType.valueOf(request.accountHolder().personalInfo().identityDocument().type()),
            DocumentNumber.of(request.accountHolder().personalInfo().identityDocument().number())
          ),
          DateOfBirth.of(request.accountHolder().personalInfo().dateOfBirth())
        );
    }

    default ContactInfo toContactInfo(OpenBankAccountRequest request) {
        return ContactInfo.of(
          Email.of(request.accountHolder().contactInfo().email()),
          PhoneNumber.of(request.accountHolder().contactInfo().phoneNumber())
        );
    }

    default Address toAddress(OpenBankAccountRequest request) {
        return new Address(
          Street.of(request.accountHolder().address().street()),
          StreetNumber.of(request.accountHolder().address().streetNumber()),
          request.accountHolder().address().complement() != null ? AddressComplement.of(request.accountHolder().address().complement()) : null,
          PostalCode.of(request.accountHolder().address().postalCode()),
          City.of(request.accountHolder().address().city())
        );
    }
}
