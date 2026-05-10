package com.jcondotta.banking.accounts.contracts.status;

import tools.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static com.jcondotta.banking.accounts.contracts.support.ObjectMapperFactory.OBJECT_MAPPER;
import static com.jcondotta.banking.accounts.contracts.support.ObjectMapperFactory.fieldNamesOf;
import static org.assertj.core.api.Assertions.assertThat;

class BankAccountStatusChangedIntegrationPayloadTest {

  private static final UUID BANK_ACCOUNT_ID = UUID.fromString("a1b2c3d4-e5f6-7890-abcd-ef1234567890");
  private static final BankAccountStatus PREVIOUS_STATUS = BankAccountStatus.PENDING;
  private static final BankAccountStatus CURRENT_STATUS = BankAccountStatus.ACTIVE;

  @Test
  void shouldCreatePayload_whenAllFieldsAreValid() {
    var payload = new BankAccountStatusChangedIntegrationPayload(BANK_ACCOUNT_ID, PREVIOUS_STATUS, CURRENT_STATUS);

    assertThat(payload.bankAccountId()).isEqualTo(BANK_ACCOUNT_ID);
    assertThat(payload.previousStatus()).isEqualTo(PREVIOUS_STATUS);
    assertThat(payload.currentStatus()).isEqualTo(CURRENT_STATUS);
  }

  @Test
  void shouldSerializePayload_whenConvertedToJson() {
    var payload = new BankAccountStatusChangedIntegrationPayload(BANK_ACCOUNT_ID, PREVIOUS_STATUS, CURRENT_STATUS);

    JsonNode jsonNode = OBJECT_MAPPER.valueToTree(payload);

    assertThat(fieldNamesOf(jsonNode)).containsExactlyInAnyOrder("bankAccountId", "previousStatus", "currentStatus");
    assertThat(jsonNode.get("bankAccountId").asText()).isEqualTo(BANK_ACCOUNT_ID.toString());
    assertThat(jsonNode.get("previousStatus").asText()).isEqualTo(PREVIOUS_STATUS.name());
    assertThat(jsonNode.get("currentStatus").asText()).isEqualTo(CURRENT_STATUS.name());
  }

  @Test
  void shouldDeserializePayload_whenJsonIsValid() throws Exception {
    var payload = new BankAccountStatusChangedIntegrationPayload(BANK_ACCOUNT_ID, PREVIOUS_STATUS, CURRENT_STATUS);

    String json = OBJECT_MAPPER.writeValueAsString(payload);
    var deserialized = OBJECT_MAPPER.readValue(json, BankAccountStatusChangedIntegrationPayload.class);

    assertThat(deserialized).isEqualTo(payload);
  }
}
