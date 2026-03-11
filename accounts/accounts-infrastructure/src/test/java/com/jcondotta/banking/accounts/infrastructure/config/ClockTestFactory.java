package com.jcondotta.banking.accounts.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

@Configuration
public class ClockTestFactory {

  public static final Clock FIXED_CLOCK = Clock.fixed(Instant.parse("2022-06-24T12:45:01Z"), ZoneOffset.UTC);

  @Bean
  @Primary
  public Clock systemClock() {
    return FIXED_CLOCK;
  }
}
