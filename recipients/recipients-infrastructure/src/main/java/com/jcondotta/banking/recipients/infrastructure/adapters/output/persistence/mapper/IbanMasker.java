package com.jcondotta.banking.recipients.infrastructure.adapters.output.persistence.mapper;

public final class IbanMasker {

  private static final int PREFIX_LENGTH = 4;
  private static final int SUFFIX_LENGTH = 4;
  private static final String MASK = "****";

  private IbanMasker() {}

  public static String mask(String iban) {
    if (iban == null || iban.length() <= PREFIX_LENGTH + SUFFIX_LENGTH) {
      return iban;
    }

    var prefix = iban.substring(0, PREFIX_LENGTH);
    var suffix = iban.substring(iban.length() - SUFFIX_LENGTH);

    return prefix + MASK + suffix;
  }
}