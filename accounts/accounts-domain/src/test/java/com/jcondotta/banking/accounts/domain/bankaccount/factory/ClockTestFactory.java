package com.jcondotta.banking.accounts.domain.bankaccount.factory;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

public class ClockTestFactory {

  public static final Clock FIXED_CLOCK = Clock.fixed(Instant.parse("2022-06-24T12:45:01Z"), ZoneOffset.UTC);
}