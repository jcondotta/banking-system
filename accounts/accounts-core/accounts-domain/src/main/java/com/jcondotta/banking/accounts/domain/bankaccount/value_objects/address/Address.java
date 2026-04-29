package com.jcondotta.banking.accounts.domain.bankaccount.value_objects.address;

import static com.jcondotta.domain.support.Preconditions.required;

public record Address(
  Street street,
  StreetNumber streetNumber,
  AddressComplement addressComplement,
  PostalCode postalCode,
  City city
) {

  public static final String STREET_MUST_BE_PROVIDED = "Street must be provided";
  public static final String NUMBER_MUST_BE_PROVIDED = "Street number must be provided";
  public static final String POSTAL_MUST_BE_PROVIDED = "Postal code must be provided";
  public static final String CITY_MUST_BE_PROVIDED = "City must be provided";

  public Address {
    required(street, STREET_MUST_BE_PROVIDED);
    required(streetNumber, NUMBER_MUST_BE_PROVIDED);
    required(postalCode, POSTAL_MUST_BE_PROVIDED);
    required(city, CITY_MUST_BE_PROVIDED);
  }

  public static Address of(String street, String number, String addressComplement, String postalCode, String city) {
    return new Address(
      Street.of(street),
      StreetNumber.of(number),
      AddressComplement.ofNullable(addressComplement),
      PostalCode.of(postalCode),
      City.of(city)
    );
  }
}