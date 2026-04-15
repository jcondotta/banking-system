package com.jcondotta.banking.recipients.integration.bankaccount;

import com.jcondotta.application.events.IntegrationEventMetadata;
import com.jcondotta.banking.accounts.contracts.activate.BankAccountActivatedIntegrationEvent;
import com.jcondotta.banking.accounts.contracts.activate.BankAccountActivatedIntegrationPayload;
import com.jcondotta.banking.recipients.domain.recipient.identity.BankAccountId;
import com.jcondotta.banking.recipients.domain.recipient.repository.BankAccountRepository;
import com.jcondotta.banking.recipients.infrastructure.adapters.input.messaging.properties.BankAccountActivatedTopicProperties;
import com.jcondotta.banking.recipients.integration.testsupport.annotation.IntegrationTest;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
class BankAccountActivatedConsumerIT {

  private static final Instant OCCURRED_AT = Instant.parse("2026-01-01T00:00:00Z");

  @Autowired
  private KafkaTemplate<String, byte[]> kafkaTemplate;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private BankAccountActivatedTopicProperties topicProperties;

  @Autowired
  private BankAccountRepository repository;

  private final UUID bankAccountId = UUID.randomUUID();
  private final UUID eventId = UUID.randomUUID();
  private final UUID correlationId = UUID.randomUUID();
  private final String source = "bank-account-service";

  @Test
  void shouldRegisterBankAccount_whenBankAccountActivatedEventIsPublished() {
    var eventMetadata = IntegrationEventMetadata.of(
      eventId,
      correlationId,
      source,
      1,
      OCCURRED_AT
    );

    var payload = new BankAccountActivatedIntegrationPayload(bankAccountId);
    var event = new BankAccountActivatedIntegrationEvent(eventMetadata, payload);

    var bytes = objectMapper.writeValueAsBytes(event);

    kafkaTemplate.send(topicProperties.topicName(), bankAccountId.toString(), bytes);

    Awaitility.await()
      .atMost(Duration.ofSeconds(5))
      .untilAsserted(() -> {
        var account = repository.findById(new BankAccountId(bankAccountId));
        assertThat(account).isPresent();
      });
  }

  @Test
  void shouldNotCreateDuplicateBankAccount_whenEventIsPublishedTwice() {
    var eventMetadata = IntegrationEventMetadata.of(
      eventId,
      correlationId,
      source,
      1,
      OCCURRED_AT
    );

    var payload = new BankAccountActivatedIntegrationPayload(bankAccountId);
    var event = new BankAccountActivatedIntegrationEvent(eventMetadata, payload);

    var bytes = objectMapper.writeValueAsBytes(event);

    kafkaTemplate.send(topicProperties.topicName(), bankAccountId.toString(), bytes);
    kafkaTemplate.send(topicProperties.topicName(), bankAccountId.toString(), bytes);

    Awaitility.await()
      .atMost(Duration.ofSeconds(5))
      .untilAsserted(() -> {
        var account = repository.findById(new BankAccountId(bankAccountId));
        assertThat(account).isPresent();
      });
  }
}
