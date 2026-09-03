package com.jcondotta.banking.accounts.application.bankaccount.query.get.model;

import static java.util.Objects.requireNonNull;

public record AddressSummary(
  String street,
  String streetNumber,
  String addressComplement,
  String postalCode,
  String city
) {

  static final String STREET_REQUIRED = "street must be provided";
  static final String STREET_NUMBER_REQUIRED = "streetNumber must be provided";
  static final String POSTAL_CODE_REQUIRED = "postalCode must be provided";
  static final String CITY_REQUIRED = "city must be provided";

  public AddressSummary {
    requireNonNull(street, STREET_REQUIRED);
    requireNonNull(streetNumber, STREET_NUMBER_REQUIRED);
    requireNonNull(postalCode, POSTAL_CODE_REQUIRED);
    requireNonNull(city, CITY_REQUIRED);
  }
}