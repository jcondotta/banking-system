package com.jcondotta.banking.recipients.domain.testsupport;

public enum RecipientTestData {

  JEFFERSON("Jefferson Condotta", "ES3801283316232166447417"),
  PATRIZIO("Patrizio Condotta", "IT93Q0300203280175171887193"),
  VIRGINIO("Virginio Condotta", "GB82WEST12345698765432");

  private final String name;
  private final String iban;

  RecipientTestData(String name, String iban) {
    this.name = name;
    this.iban = iban;
  }

  public String getName() {
    return name;
  }

  public String getIban() {
    return iban;
  }
}
