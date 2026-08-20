package com.jcondotta.banking.recipients.integration.testsupport.configuration;

import com.jcondotta.banking.recipients.domain.testsupport.TimeFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.time.Clock;
import java.time.ZoneOffset;

@Configuration
public class TimeTestFactory {

  public static final Clock FIXED_CLOCK = Clock.fixed(TimeFactory.FIXED_INSTANT, ZoneOffset.UTC);

  @Bean
  @Primary
  public Clock systemClock() {
    return FIXED_CLOCK;
  }
}
