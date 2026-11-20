package com.jcondotta.banking.accounts.application.bankaccount.services;

import com.jcondotta.banking.accounts.domain.bankaccount.value_objects.Iban;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadLocalRandom;

@Component
public class IbanGenerator {

  private static final String COUNTRY_CODE = "DE";
  private static final String BANK_CODE = "50010517";

  public Iban generate() {
    var accountNumber = String.format("%010d", ThreadLocalRandom.current().nextLong(0, 10_000_000_000L));
    var bban = BANK_CODE + accountNumber;
    var checkDigits = computeCheckDigits(COUNTRY_CODE, bban);
    return Iban.of(COUNTRY_CODE + checkDigits + bban);
  }

  private static String computeCheckDigits(String countryCode, String bban) {
    var rearranged = bban + countryCode + "00";
    var numeric = toNumeric(rearranged);
    var checkDigits = 98 - mod97(numeric);
    return String.format("%02d", checkDigits);
  }

  private static String toNumeric(String value) {
    var sb = new StringBuilder();
    for (char c : value.toCharArray()) {
      if (Character.isLetter(c)) {
        sb.append(c - 'A' + 10);
      } else {
        sb.append(c);
      }
    }
    return sb.toString();
  }

  private static int mod97(String input) {
    int remainder = 0;
    for (char c : input.toCharArray()) {
      remainder = (remainder * 10 + (c - '0')) % 97;
    }
    return remainder;
  }
}
