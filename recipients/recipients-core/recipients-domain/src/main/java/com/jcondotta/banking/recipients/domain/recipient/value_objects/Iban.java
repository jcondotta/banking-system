package com.jcondotta.banking.recipients.domain.recipient.value_objects;

import org.apache.commons.lang3.StringUtils;

import java.util.Locale;
import java.util.Objects;

public record Iban(String value) {

  public static final String IBAN_NOT_PROVIDED = "IBAN must be provided.";
  public static final String IBAN_INVALID_FORMAT_MESSAGE = "IBAN format is invalid.";

  public Iban {
    Objects.requireNonNull(value, IBAN_NOT_PROVIDED);

    var sanitizedValue = StringUtils.deleteWhitespace(value).toUpperCase(Locale.ROOT);

    if (!isValid(sanitizedValue)) {
      throw new IllegalArgumentException(IBAN_INVALID_FORMAT_MESSAGE);
    }

    value = sanitizedValue;
  }

  public static Iban of(String value) {
    return new Iban(value);
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
