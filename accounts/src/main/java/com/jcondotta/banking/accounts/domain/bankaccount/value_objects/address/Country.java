package com.jcondotta.banking.accounts.domain.bankaccount.value_objects.address;

import java.util.Locale;
import java.util.Set;

import static com.jcondotta.domain.support.Preconditions.checkArgument;
import static com.jcondotta.domain.support.Preconditions.requiredNotBlank;

public record Country(String isoCode) {

  public static final String ISO_CODE_MUST_BE_PROVIDED = "Country ISO code must be provided";
  public static final String ISO_CODE_MUST_BE_VALID = "Country ISO code must be a valid ISO 3166-1 alpha-2 code";

  private static final Set<String> VALID_ISO_CODES = Set.of(Locale.getISOCountries());

  public Country {
    requiredNotBlank(isoCode, ISO_CODE_MUST_BE_PROVIDED);
    checkArgument(VALID_ISO_CODES.contains(isoCode), ISO_CODE_MUST_BE_VALID);
  }

  public static Country of(String isoCode) {
    return new Country(isoCode);
  }
}
