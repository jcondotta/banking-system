package com.jcondotta.banking.accounts.infrastructure.adapters.output.eventsource;

import com.jcondotta.application.events.EventSourceProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ApplicationNameEventSourceProvider implements EventSourceProvider {

  private final String applicationName;

  public ApplicationNameEventSourceProvider(@Value("${spring.application.name}") String applicationName) {
    this.applicationName = applicationName;
  }

  public String get() {
    return applicationName;
  }
}
