package com.jcondotta.banking.accounts.domain.bankaccount.value_objects;

import com.jcondotta.domain.exception.InvalidDomainDataException;

import java.util.Locale;

public record Iban(String value) {

  public static final String IBAN_NOT_PROVIDED = "IBAN must be provided.";
  public static final String IBAN_INVALID_FORMAT = "IBAN format is invalid.";

  public Iban {
    if (value == null) {
      throw new InvalidDomainDataException(IBAN_NOT_PROVIDED);
    }

    var sanitized = value.replaceAll("\\s+", "").toUpperCase(Locale.ROOT);

    if (sanitized.isEmpty()) {
      throw new InvalidDomainDataException(IBAN_NOT_PROVIDED);
    }

    if (!isValid(sanitized)) {
      throw new InvalidDomainDataException(IBAN_INVALID_FORMAT);
    }

    value = sanitized;
  }

  public static Iban of(String value) {
    return new Iban(value);
  }

  public String masked() {
    return value.substring(0, 4) + "****" + value.substring(value.length() - 4);
  }

  private static boolean isValid(String iban) {
    if (iban.length() < 15 || iban.length() > 34) {
      return false;
    }

    String rearranged = iban.substring(4) + iban.substring(0, 4);

    StringBuilder numeric = new StringBuilder();
    for (char c : rearranged.toCharArray()) {
      if (Character.isLetter(c)) {
        numeric.append(c - 'A' + 10);
      } else if (Character.isDigit(c)) {
        numeric.append(c);
      } else {
        return false;
      }
    }

    return mod97(numeric.toString()) == 1;
  }

  private static int mod97(String input) {
    int remainder = 0;

    for (int i = 0; i < input.length(); i++) {
      int digit = input.charAt(i) - '0';
      remainder = (remainder * 10 + digit) % 97;
    }

    return remainder;
  }
}
