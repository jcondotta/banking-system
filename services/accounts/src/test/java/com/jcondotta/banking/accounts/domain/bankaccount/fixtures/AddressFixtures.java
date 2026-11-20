package com.jcondotta.banking.accounts.domain.bankaccount.fixtures;

import com.jcondotta.banking.accounts.domain.bankaccount.value_objects.address.Address;

public enum AddressFixtures {

  BARCELONA_APT(
    "Carrer de Mallorca",
    "01",
    "3º - 1ª",
    "08013",
    "Barcelona",
    "ES"
  ),

  BERLIN_HOUSE(
    "Alexanderplatz",
    "1",
    null,
    "10178",
    "Berlin",
    "DE"
  ),

  MADRID_OFFICE(
    "Gran Via",
    "28B",
    "Floor 2",
    "28013",
    "Madrid",
    "ES"
  );

  private final String street;
  private final String number;
  private final String complement;
  private final String postalCode;
  private final String city;
  private final String country;

  AddressFixtures(
    String street,
    String number,
    String complement,
    String postalCode,
    String city,
    String country
  ) {
    this.street = street;
    this.number = number;
    this.complement = complement;
    this.postalCode = postalCode;
    this.city = city;
    this.country = country;
  }

  public Address address() {
    return Address.of(street, number, complement, postalCode, city, country);
  }
}
