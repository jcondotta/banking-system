package com.jcondotta.banking.accounts.infrastructure.adapters.output.eventsource;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ApplicationNameEventSourceProviderTest {

  @Test
  void shouldReturnApplicationName_whenGetIsCalled() {
    var provider = new ApplicationNameEventSourceProvider("accounts-service");

    assertThat(provider.get()).isEqualTo("accounts-service");
  }
}
