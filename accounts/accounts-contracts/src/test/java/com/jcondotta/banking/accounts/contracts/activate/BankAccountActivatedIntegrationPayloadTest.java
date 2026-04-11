package com.jcondotta.banking.accounts.contracts.activate;

import tools.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static com.jcondotta.banking.accounts.contracts.support.ObjectMapperFactory.OBJECT_MAPPER;
import static com.jcondotta.banking.accounts.contracts.support.ObjectMapperFactory.fieldNamesOf;
import static org.assertj.core.api.Assertions.assertThat;

class BankAccountActivatedIntegrationPayloadTest {

  private static final UUID BANK_ACCOUNT_ID = UUID.fromString("a1b2c3d4-e5f6-7890-abcd-ef1234567890");

  @Test
  void shouldCreatePayload_whenAllFieldsAreValid() {
    var payload = new BankAccountActivatedIntegrationPayload(BANK_ACCOUNT_ID);

    assertThat(payload.bankAccountId()).isEqualTo(BANK_ACCOUNT_ID);
  }

  @Test
  void shouldSerializePayload_whenConvertedToJson() throws Exception {
    var payload = new BankAccountActivatedIntegrationPayload(BANK_ACCOUNT_ID);

    JsonNode jsonNode = OBJECT_MAPPER.valueToTree(payload);

    assertThat(fieldNamesOf(jsonNode)).containsExactlyInAnyOrder("bankAccountId");
    assertThat(jsonNode.get("bankAccountId").asText()).isEqualTo(BANK_ACCOUNT_ID.toString());
  }

  @Test
  void shouldDeserializePayload_whenJsonIsValid() throws Exception {
    var payload = new BankAccountActivatedIntegrationPayload(BANK_ACCOUNT_ID);

    String json = OBJECT_MAPPER.writeValueAsString(payload);
    var deserialized = OBJECT_MAPPER.readValue(json, BankAccountActivatedIntegrationPayload.class);

    assertThat(deserialized).isEqualTo(payload);
  }
}
