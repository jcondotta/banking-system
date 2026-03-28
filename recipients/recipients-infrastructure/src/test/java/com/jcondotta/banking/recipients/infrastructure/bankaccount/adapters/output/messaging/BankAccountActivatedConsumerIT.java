package com.jcondotta.banking.recipients.infrastructure.bankaccount.adapters.output.messaging;

import com.jcondotta.application.core.events.IntegrationEventMetadata;
import com.jcondotta.banking.contracts.activate.BankAccountActivatedIntegrationEvent;
import com.jcondotta.banking.contracts.activate.BankAccountActivatedIntegrationPayload;
import com.jcondotta.banking.recipients.domain.recipient.identity.BankAccountId;
import com.jcondotta.banking.recipients.domain.recipient.repository.BankAccountRepository;
import com.jcondotta.banking.recipients.infrastructure.bankaccount.properties.BankAccountActivatedTopicProperties;
import com.jcondotta.banking.recipients.infrastructure.bankaccount.testsupport.container.KafkaTestContainer;
import com.jcondotta.banking.recipients.infrastructure.bankaccount.testsupport.container.LocalStackTestContainer;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@ContextConfiguration(initializers = { LocalStackTestContainer.class, KafkaTestContainer.class })
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BankAccountActivatedConsumerIT {

  @Autowired
  private KafkaTemplate<String, Object> kafkaTemplate;

  @Autowired
  private BankAccountActivatedTopicProperties topicProperties;

  @Autowired
  private BankAccountRepository repository;

  private final UUID bankAccountId = UUID.randomUUID();
  private final UUID eventId = UUID.randomUUID();
  private final UUID correlationId = UUID.randomUUID();
  private final String source = "bank-account-service";

  @Test
  void shouldCreateBankAccount_whenBankAccountActivatedEventIsPublished() {
    var eventMetadata = IntegrationEventMetadata.of(
      eventId,
      correlationId,
      source,
      1,
      Instant.now()
    );

    var payload = new BankAccountActivatedIntegrationPayload(bankAccountId);
    var event = new BankAccountActivatedIntegrationEvent(eventMetadata, payload);

    kafkaTemplate.send(topicProperties.topicName(), event);

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
      Instant.now()
    );

    var payload = new BankAccountActivatedIntegrationPayload(bankAccountId);
    var event = new BankAccountActivatedIntegrationEvent(eventMetadata, payload);

    kafkaTemplate.send(topicProperties.topicName(), event);
    kafkaTemplate.send(topicProperties.topicName(), event);

    Awaitility.await()
      .atMost(Duration.ofSeconds(5))
      .untilAsserted(() -> {
        var account = repository.findById(new BankAccountId(bankAccountId));
        assertThat(account).isPresent();
      });
  }
}