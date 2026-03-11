package com.jcondotta.banking.accounts.application.bankaccount.query.get.mapper;

import com.jcondotta.banking.accounts.application.bankaccount.query.get.model.ContactInfoSummary;
import com.jcondotta.banking.accounts.domain.bankaccount.value_objects.contact.ContactInfo;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ContactInfoSummaryMapper {

  default ContactInfoSummary toSummary(ContactInfo contactInfo) {
    if (contactInfo == null) {
      return null;
    }

    return new ContactInfoSummary(
      contactInfo.email().value() ,
      contactInfo.phoneNumber().value()
    );
  }
}