package com.jcondotta.banking.accounts.application.bankaccount.query.get.mapper;

import com.jcondotta.banking.accounts.application.bankaccount.query.get.model.ContactInfoSummary;
import com.jcondotta.banking.accounts.domain.bankaccount.value_objects.contact.ContactInfo;
import com.jcondotta.banking.accounts.domain.bankaccount.value_objects.contact.Email;
import com.jcondotta.banking.accounts.domain.bankaccount.value_objects.contact.PhoneNumber;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ContactInfoSummaryMapper {

  @Mapping(target = "email", source = "email")
  @Mapping(target = "phoneNumber", source = "phoneNumber")
  ContactInfoSummary toSummary(ContactInfo contactInfo);

  default String map(Email email) {
    return email != null ? email.value() : null;
  }

  default String map(PhoneNumber phoneNumber) {
    return phoneNumber != null ? phoneNumber.value() : null;
  }
}