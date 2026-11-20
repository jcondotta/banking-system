package com.jcondotta.banking.accounts.domain.bankaccount.fixtures;

import com.jcondotta.banking.accounts.domain.bankaccount.value_objects.contact.ContactInfo;
import com.jcondotta.banking.accounts.domain.bankaccount.value_objects.contact.Email;
import com.jcondotta.banking.accounts.domain.bankaccount.value_objects.contact.PhoneNumber;

public enum ContactInfoFixtures {

  JEFFERSON(
    "jefferson.condotta@email.com",
    "+49123456789"
  ),

  VIRGINIO(
    "virginio.condotta@email.com",
    "+49111111111"
  ),

  PATRIZIO(
    "patrizio.condotta@email.com",
    "+49222222222"
  );

  private final String email;
  private final String phoneNumber;

  ContactInfoFixtures(String email, String phoneNumber) {
    this.email = email;
    this.phoneNumber = phoneNumber;
  }

  public ContactInfo contactInfo() {
    return ContactInfo.of(
      Email.of(email),
      PhoneNumber.of(phoneNumber)
    );
  }

  public Email email() {
    return Email.of(email);
  }

  public PhoneNumber phoneNumber() {
    return PhoneNumber.of(phoneNumber);
  }
}