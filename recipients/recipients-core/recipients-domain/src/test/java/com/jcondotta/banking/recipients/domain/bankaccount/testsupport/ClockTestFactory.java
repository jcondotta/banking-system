package com.jcondotta.banking.recipients.domain.bankaccount.testsupport;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

// ⚠️ This is intentionally duplicated to avoid test-module dependency.
// Keep in sync with the ClockTestFactory in recipients-test-support.
public class ClockTestFactory {

  public static final Clock FIXED_CLOCK = Clock.fixed(Instant.parse("2022-06-24T12:45:01Z"), ZoneOffset.UTC);
}