package com.jcondotta.banking.accounts.domain.bankaccount.value_objects.personal;

import com.jcondotta.domain.exception.DomainValidationException;
import com.jcondotta.domain.support.DomainPreconditions;

import java.time.Clock;
import java.time.LocalDate;

public record DateOfBirth(LocalDate value) {

  public static final String DATE_OF_BIRTH_NOT_PROVIDED = "Date of birth must be provided.";
  public static final String DATE_OF_BIRTH_NOT_IN_PAST = "Date of birth must be in the past.";

  public DateOfBirth {
    DomainPreconditions.required(value, DATE_OF_BIRTH_NOT_PROVIDED);

    if (value.isAfter(LocalDate.now(Clock.systemDefaultZone()))) {
      throw new DomainValidationException(DATE_OF_BIRTH_NOT_IN_PAST);
    }
  }

  public static DateOfBirth of(LocalDate value) {
    return new DateOfBirth(value);
  }
}
