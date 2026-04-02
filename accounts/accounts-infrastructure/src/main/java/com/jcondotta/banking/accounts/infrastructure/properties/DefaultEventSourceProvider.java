package com.jcondotta.banking.accounts.infrastructure.properties;

import com.jcondotta.application.events.EventSourceProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DefaultEventSourceProvider implements EventSourceProvider {

  private final String applicationName;

  public DefaultEventSourceProvider(@Value("${spring.application.name}") String applicationName) {
    this.applicationName = applicationName;
  }

  public String get() {
    return applicationName;
  }
}