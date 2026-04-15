package com.jcondotta.banking.recipients.infrastructure.adapters.input.rest.properties;

import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AccountRecipientsURIPropertiesTest {

  private static final String ROOT_PATH = "/api/v1/bank-accounts/{bank-account-id}/recipients";
  private static final String RECIPIENT_ID_PATH = "/api/v1/bank-accounts/{bank-account-id}/recipients/{recipient-id}";

  private final AccountRecipientsURIProperties properties =
    new AccountRecipientsURIProperties(ROOT_PATH, RECIPIENT_ID_PATH);

  @Test
  void shouldExpandRecipientsUri_whenBankAccountIdIsProvided() {
    var bankAccountId = UUID.randomUUID();

    var uri = properties.recipientsURI(bankAccountId);

    assertThat(uri)
      .isEqualTo(URI.create("/api/v1/bank-accounts/" + bankAccountId + "/recipients"));
  }

  @Test
  void shouldExpandRecipientUri_whenBankAccountIdAndRecipientIdAreProvided() {
    var bankAccountId = UUID.randomUUID();
    var recipientId = UUID.randomUUID();

    var uri = properties.recipientURI(bankAccountId, recipientId);

    assertThat(uri)
      .isEqualTo(URI.create("/api/v1/bank-accounts/" + bankAccountId + "/recipients/" + recipientId));
  }
}
