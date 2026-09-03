package com.jcondotta.banking.accounts.domain.testsupport;

import com.jcondotta.banking.accounts.domain.bankaccount.value_objects.address.Address;
import com.jcondotta.banking.accounts.domain.bankaccount.value_objects.contact.ContactInfo;
import com.jcondotta.banking.accounts.domain.bankaccount.value_objects.personal.PersonalInfo;

public enum AccountHolderFixtures {

  JEFFERSON(
    PersonalInfoFixtures.JEFFERSON,
    ContactInfoFixtures.JEFFERSON,
    AddressFixtures.BARCELONA_APT
  ),

  VIRGINIO(
    PersonalInfoFixtures.VIRGINIO,
    ContactInfoFixtures.VIRGINIO,
    AddressFixtures.MADRID_OFFICE
  ),

  PATRIZIO(
    PersonalInfoFixtures.PATRIZIO,
    ContactInfoFixtures.PATRIZIO,
    AddressFixtures.BERLIN_HOUSE
  );

  private final PersonalInfoFixtures personalInfoFixture;
  private final ContactInfoFixtures contactInfoFixture;
  private final AddressFixtures addressFixtures;

  AccountHolderFixtures(PersonalInfoFixtures personalInfoFixture, ContactInfoFixtures contactInfoFixture, AddressFixtures addressFixtures) {
    this.personalInfoFixture = personalInfoFixture;
    this.contactInfoFixture = contactInfoFixture;
    this.addressFixtures = addressFixtures;
  }

  public PersonalInfo personalInfo() {
    return personalInfoFixture.personalInfo();
  }

  public ContactInfo contactInfo() {
    return contactInfoFixture.contactInfo();
  }

  public Address address() {
    return addressFixtures.address();
  }
}