package com.jcondotta.banking.recipients.infrastructure.adapters.input.messaging.properties;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BankAccountActivatedTopicPropertiesTest {

  @Test
  void shouldReturnTopicName_whenPropertiesAreCreated() {
    var properties = new BankAccountActivatedTopicProperties("bank-account-activated");

    assertThat(properties.topicName())
      .isEqualTo("bank-account-activated");
  }
}
