package com.jcondotta.banking.accounts.contracts.addholder;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.JsonNode;

import java.util.UUID;

import static com.jcondotta.banking.accounts.contracts.support.ObjectMapperFactory.OBJECT_MAPPER;
import static com.jcondotta.banking.accounts.contracts.support.ObjectMapperFactory.fieldNamesOf;
import static org.assertj.core.api.Assertions.assertThat;

class BankAccountJointHolderAddedIntegrationPayloadTest {

  private static final UUID BANK_ACCOUNT_ID = UUID.fromString("a1b2c3d4-e5f6-7890-abcd-ef1234567890");
  private static final UUID ACCOUNT_HOLDER_ID = UUID.fromString("b2c3d4e5-f6a7-8901-bcde-f12345678901");

  @Test
  void shouldCreatePayload_whenAllFieldsAreValid() {
    var payload = new BankAccountJointHolderAddedIntegrationPayload(BANK_ACCOUNT_ID, ACCOUNT_HOLDER_ID);

    assertThat(payload.bankAccountId()).isEqualTo(BANK_ACCOUNT_ID);
    assertThat(payload.accountHolderId()).isEqualTo(ACCOUNT_HOLDER_ID);
  }

  @Test
  void shouldSerializePayload_whenConvertedToJson() throws Exception {
    var payload = new BankAccountJointHolderAddedIntegrationPayload(BANK_ACCOUNT_ID, ACCOUNT_HOLDER_ID);

    JsonNode jsonNode = OBJECT_MAPPER.valueToTree(payload);

    assertThat(fieldNamesOf(jsonNode)).containsExactlyInAnyOrder("bankAccountId", "accountHolderId");
    assertThat(jsonNode.get("bankAccountId").asText()).isEqualTo(BANK_ACCOUNT_ID.toString());
    assertThat(jsonNode.get("accountHolderId").asText()).isEqualTo(ACCOUNT_HOLDER_ID.toString());
  }

  @Test
  void shouldDeserializePayload_whenJsonIsValid() throws Exception {
    var payload = new BankAccountJointHolderAddedIntegrationPayload(BANK_ACCOUNT_ID, ACCOUNT_HOLDER_ID);

    String json = OBJECT_MAPPER.writeValueAsString(payload);
    var deserialized = OBJECT_MAPPER.readValue(json, BankAccountJointHolderAddedIntegrationPayload.class);

    assertThat(deserialized).isEqualTo(payload);
  }
}
