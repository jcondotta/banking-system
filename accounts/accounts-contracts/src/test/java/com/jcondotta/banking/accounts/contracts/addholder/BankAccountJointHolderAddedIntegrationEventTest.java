package com.jcondotta.banking.accounts.contracts.addholder;

import com.jcondotta.application.events.IntegrationEventMetadata;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.JsonNode;

import java.util.UUID;

import static com.jcondotta.banking.accounts.contracts.support.IntegrationEventMetadataFixtures.*;
import static com.jcondotta.banking.accounts.contracts.support.ObjectMapperFactory.OBJECT_MAPPER;
import static com.jcondotta.banking.accounts.contracts.support.ObjectMapperFactory.fieldNamesOf;
import static org.assertj.core.api.Assertions.assertThat;

class BankAccountJointHolderAddedIntegrationEventTest {

  private static final UUID BANK_ACCOUNT_ID = UUID.fromString("a1b2c3d4-e5f6-7890-abcd-ef1234567890");
  private static final UUID ACCOUNT_HOLDER_ID = UUID.fromString("b2c3d4e5-f6a7-8901-bcde-f12345678901");

  private final IntegrationEventMetadata metadata = defaultMetadata();
  private final BankAccountJointHolderAddedIntegrationPayload payload =
    new BankAccountJointHolderAddedIntegrationPayload(BANK_ACCOUNT_ID, ACCOUNT_HOLDER_ID);

  @Test
  void shouldCreateEvent_whenAllFieldsAreValid() {
    var event = new BankAccountJointHolderAddedIntegrationEvent(metadata, payload);

    assertThat(event.metadata()).isEqualTo(metadata);
    assertThat(event.payload()).isEqualTo(payload);
    assertThat(event.eventType()).isEqualTo(BankAccountJointHolderAddedIntegrationEvent.EVENT_TYPE);
  }

  @Test
  void shouldHaveExpectedEventTypeValue() {
    assertThat(BankAccountJointHolderAddedIntegrationEvent.EVENT_TYPE).isEqualTo("bank-account-joint-holder-added");
  }

  @Test
  void shouldSerializeEvent_whenConvertedToJson() throws Exception {
    var event = new BankAccountJointHolderAddedIntegrationEvent(metadata, payload);

    JsonNode jsonNode = OBJECT_MAPPER.valueToTree(event);

    assertThat(fieldNamesOf(jsonNode)).containsExactlyInAnyOrder("metadata", "payload");

    JsonNode metadataNode = jsonNode.get("metadata");
    assertThat(metadataNode.get("eventId").asText()).isEqualTo(EVENT_ID.toString());
    assertThat(metadataNode.get("correlationId").asText()).isEqualTo(CORRELATION_ID.toString());
    assertThat(metadataNode.get("eventSource").asText()).isEqualTo(EVENT_SOURCE);
    assertThat(metadataNode.get("version").asInt()).isEqualTo(IntegrationEventMetadata.DEFAULT_EVENT_VERSION);
    assertThat(metadataNode.get("occurredAt").asText()).isEqualTo(OCCURRED_AT_ISO);

    JsonNode payloadNode = jsonNode.get("payload");
    assertThat(fieldNamesOf(payloadNode)).containsExactlyInAnyOrder("bankAccountId", "accountHolderId");
    assertThat(payloadNode.get("bankAccountId").asText()).isEqualTo(BANK_ACCOUNT_ID.toString());
    assertThat(payloadNode.get("accountHolderId").asText()).isEqualTo(ACCOUNT_HOLDER_ID.toString());
  }

  @Test
  void shouldDeserializeEvent_whenJsonIsValid() throws Exception {
    var event = new BankAccountJointHolderAddedIntegrationEvent(metadata, payload);

    String json = OBJECT_MAPPER.writeValueAsString(event);
    var deserialized = OBJECT_MAPPER.readValue(json, BankAccountJointHolderAddedIntegrationEvent.class);

    assertThat(deserialized).isEqualTo(event);
  }
}
