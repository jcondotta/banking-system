package com.jcondotta.banking.accounts.contracts.open;

import tools.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static com.jcondotta.banking.accounts.contracts.support.ObjectMapperFactory.OBJECT_MAPPER;
import static com.jcondotta.banking.accounts.contracts.support.ObjectMapperFactory.fieldNamesOf;
import static org.assertj.core.api.Assertions.assertThat;

class BankAccountOpenedIntegrationPayloadTest {

  private static final UUID BANK_ACCOUNT_ID = UUID.fromString("a1b2c3d4-e5f6-7890-abcd-ef1234567890");
  private static final String ACCOUNT_TYPE = "CHECKING";
  private static final String CURRENCY = "USD";
  private static final UUID ACCOUNT_HOLDER_ID = UUID.fromString("b2c3d4e5-f6a7-8901-bcde-f12345678901");

  @Test
  void shouldCreatePayload_whenAllFieldsAreValid() {
    var payload = new BankAccountOpenedIntegrationPayload(BANK_ACCOUNT_ID, ACCOUNT_TYPE, CURRENCY, ACCOUNT_HOLDER_ID);

    assertThat(payload.bankAccountId()).isEqualTo(BANK_ACCOUNT_ID);
    assertThat(payload.accountType()).isEqualTo(ACCOUNT_TYPE);
    assertThat(payload.currency()).isEqualTo(CURRENCY);
    assertThat(payload.accountHolderId()).isEqualTo(ACCOUNT_HOLDER_ID);
  }

  @Test
  void shouldSerializePayload_whenConvertedToJson() throws Exception {
    var payload = new BankAccountOpenedIntegrationPayload(BANK_ACCOUNT_ID, ACCOUNT_TYPE, CURRENCY, ACCOUNT_HOLDER_ID);

    JsonNode jsonNode = OBJECT_MAPPER.valueToTree(payload);

    assertThat(fieldNamesOf(jsonNode)).containsExactlyInAnyOrder("bankAccountId", "accountType", "currency", "accountHolderId");
    assertThat(jsonNode.get("bankAccountId").asText()).isEqualTo(BANK_ACCOUNT_ID.toString());
    assertThat(jsonNode.get("accountType").asText()).isEqualTo(ACCOUNT_TYPE);
    assertThat(jsonNode.get("currency").asText()).isEqualTo(CURRENCY);
    assertThat(jsonNode.get("accountHolderId").asText()).isEqualTo(ACCOUNT_HOLDER_ID.toString());
  }

  @Test
  void shouldDeserializePayload_whenJsonIsValid() throws Exception {
    var payload = new BankAccountOpenedIntegrationPayload(BANK_ACCOUNT_ID, ACCOUNT_TYPE, CURRENCY, ACCOUNT_HOLDER_ID);

    String json = OBJECT_MAPPER.writeValueAsString(payload);
    var deserialized = OBJECT_MAPPER.readValue(json, BankAccountOpenedIntegrationPayload.class);

    assertThat(deserialized).isEqualTo(payload);
  }
}
