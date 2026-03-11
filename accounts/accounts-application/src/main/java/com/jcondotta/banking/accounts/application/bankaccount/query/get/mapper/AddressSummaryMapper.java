package com.jcondotta.banking.accounts.application.bankaccount.query.get.mapper;

import com.jcondotta.banking.accounts.application.bankaccount.query.get.model.AddressSummary;
import com.jcondotta.banking.accounts.domain.bankaccount.value_objects.address.Address;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AddressSummaryMapper {

  default AddressSummary toSummary(Address address) {
    if (address == null) {
      return null;
    }

    return new AddressSummary(
      address.street().value(),
      address.streetNumber().value(),
      address.complement() != null ? address.complement().value() : null,
      address.postalCode().value(),
      address.city().value()
    );
  }
}