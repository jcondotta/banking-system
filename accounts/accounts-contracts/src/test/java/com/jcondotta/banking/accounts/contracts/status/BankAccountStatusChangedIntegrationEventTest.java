package com.jcondotta.banking.accounts.contracts.status;

import tools.jackson.databind.JsonNode;
import com.jcondotta.application.events.IntegrationEventMetadata;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static com.jcondotta.banking.accounts.contracts.support.IntegrationEventMetadataFixtures.*;
import static com.jcondotta.banking.accounts.contracts.support.ObjectMapperFactory.OBJECT_MAPPER;
import static com.jcondotta.banking.accounts.contracts.support.ObjectMapperFactory.fieldNamesOf;
import static org.assertj.core.api.Assertions.assertThat;

class BankAccountStatusChangedIntegrationEventTest {

  private static final UUID BANK_ACCOUNT_ID = UUID.fromString("a1b2c3d4-e5f6-7890-abcd-ef1234567890");
  private static final BankAccountStatus PREVIOUS_STATUS = BankAccountStatus.PENDING;
  private static final BankAccountStatus CURRENT_STATUS = BankAccountStatus.ACTIVE;

  private final IntegrationEventMetadata metadata = defaultMetadata();
  private final BankAccountStatusChangedIntegrationPayload payload =
    new BankAccountStatusChangedIntegrationPayload(BANK_ACCOUNT_ID, PREVIOUS_STATUS, CURRENT_STATUS);

  @Test
  void shouldCreateEvent_whenAllFieldsAreValid() {
    var event = new BankAccountStatusChangedIntegrationEvent(metadata, payload);

    assertThat(event.metadata()).isEqualTo(metadata);
    assertThat(event.payload()).isEqualTo(payload);
    assertThat(event.eventType()).isEqualTo(BankAccountStatusChangedIntegrationEvent.EVENT_TYPE);
  }

  @Test
  void shouldHaveExpectedEventTypeValue() {
    assertThat(BankAccountStatusChangedIntegrationEvent.EVENT_TYPE).isEqualTo("bank-account-status-changed");
  }

  @Test
  void shouldSerializeEvent_whenConvertedToJson() {
    var event = new BankAccountStatusChangedIntegrationEvent(metadata, payload);

    JsonNode jsonNode = OBJECT_MAPPER.valueToTree(event);

    assertThat(fieldNamesOf(jsonNode)).containsExactlyInAnyOrder("metadata", "payload");

    JsonNode metadataNode = jsonNode.get("metadata");
    assertThat(metadataNode.get("eventId").asText()).isEqualTo(EVENT_ID.toString());
    assertThat(metadataNode.get("correlationId").asText()).isEqualTo(CORRELATION_ID.toString());
    assertThat(metadataNode.get("eventSource").asText()).isEqualTo(EVENT_SOURCE);
    assertThat(metadataNode.get("version").asInt()).isEqualTo(IntegrationEventMetadata.DEFAULT_EVENT_VERSION);
    assertThat(metadataNode.get("occurredAt").asText()).isEqualTo(OCCURRED_AT_ISO);

    JsonNode payloadNode = jsonNode.get("payload");
    assertThat(fieldNamesOf(payloadNode)).containsExactlyInAnyOrder("bankAccountId", "previousStatus", "currentStatus");
    assertThat(payloadNode.get("bankAccountId").asText()).isEqualTo(BANK_ACCOUNT_ID.toString());
    assertThat(payloadNode.get("previousStatus").asText()).isEqualTo(PREVIOUS_STATUS.name());
    assertThat(payloadNode.get("currentStatus").asText()).isEqualTo(CURRENT_STATUS.name());
  }

  @Test
  void shouldDeserializeEvent_whenJsonIsValid() throws Exception {
    var event = new BankAccountStatusChangedIntegrationEvent(metadata, payload);

    String json = OBJECT_MAPPER.writeValueAsString(event);
    var deserialized = OBJECT_MAPPER.readValue(json, BankAccountStatusChangedIntegrationEvent.class);

    assertThat(deserialized).isEqualTo(event);
  }
}
