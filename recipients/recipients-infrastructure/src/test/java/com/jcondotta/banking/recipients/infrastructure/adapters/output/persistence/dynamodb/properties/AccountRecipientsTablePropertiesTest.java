package com.jcondotta.banking.recipients.infrastructure.adapters.output.persistence.dynamodb.properties;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AccountRecipientsTablePropertiesTest {

  @Test
  void shouldReturnTableName_whenPropertiesAreCreated() {
    var properties = new AccountRecipientsTableProperties("account-recipients");

    assertThat(properties.tableName())
      .isEqualTo("account-recipients");
  }
}
