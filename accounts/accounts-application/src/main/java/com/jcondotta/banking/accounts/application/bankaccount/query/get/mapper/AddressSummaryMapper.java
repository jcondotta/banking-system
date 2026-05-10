package com.jcondotta.banking.accounts.application.bankaccount.query.get.mapper;

import com.jcondotta.banking.accounts.application.bankaccount.query.get.model.AddressSummary;
import com.jcondotta.banking.accounts.domain.bankaccount.value_objects.address.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AddressSummaryMapper {

  @Mapping(target = "street", source = "street")
  @Mapping(target = "streetNumber", source = "streetNumber")
  @Mapping(target = "addressComplement", source = "addressComplement")
  @Mapping(target = "postalCode", source = "postalCode")
  @Mapping(target = "city", source = "city")
  AddressSummary toSummary(Address address);

  default String map(Street street) {
    return street != null ? street.value() : null;
  }

  default String map(StreetNumber streetNumber) {
    return streetNumber != null ? streetNumber.value() : null;
  }

  default String map(AddressComplement complement) {
    return complement != null ? complement.value() : null;
  }

  default String map(PostalCode postalCode) {
    return postalCode != null ? postalCode.value() : null;
  }

  default String map(City city) {
    return city != null ? city.value() : null;
  }
}