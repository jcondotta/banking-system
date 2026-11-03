package com.jcondotta.banking.accounts.integration.outbox;

import com.jcondotta.banking.accounts.domain.bankaccount.enums.AccountType;
import com.jcondotta.banking.accounts.domain.bankaccount.enums.Currency;
import com.jcondotta.banking.accounts.domain.bankaccount.events.BankAccountActivatedEvent;
import com.jcondotta.banking.accounts.domain.bankaccount.identity.BankAccountId;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.messaging.properties.KafkaTopicsProperties;
import com.jcondotta.banking.accounts.integration.bankaccount.BankAccountIntegrationSupport;
import com.jcondotta.banking.accounts.integration.testsupport.annotation.IntegrationTest;
import com.jcondotta.banking.accounts.integration.testsupport.container.KafkaContainerSupport;
import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.TopicExistsException;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertAll;

@IntegrationTest
@TestPropertySource(properties = {
  "app.outbox.worker.enabled=true",
  "app.outbox.worker.polling.interval=100ms",
  "app.kafka.topics.bank-account-activated.topic-name=bank-account-activated"
})
class BankAccountActivatedOutboxIT extends BankAccountIntegrationSupport {

  private static final Duration KAFKA_POLL_TIMEOUT = Duration.ofSeconds(10);

  @Autowired
  private KafkaTopicsProperties topicsProperties;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  void shouldPublishActivatedEventAndDeleteOutboxRecords_whenBankAccountIsActivated() throws Exception {
    clearOutboxEvents();
    var topic = topicsProperties.bankAccountActivated().topicName();
    ensureTopicExists(topic);

    try (var consumer = createConsumer()) {
      assignAtEnd(consumer, topic);

      var id = openBankAccount(AccountType.CHECKING, Currency.EUR);
      var correlationId = UUID.randomUUID();

      activateBankAccount(id, correlationId);

      var bankAccount = bankAccountRepository.findById(id).orElseThrow();
      var iban = bankAccount.getIban().orElseThrow().value();
      var record = pollForActivatedEvent(consumer, id);
      var envelope = objectMapper.readTree(record.value());

      assertAll(
        () -> assertThat(record.topic()).isEqualTo(topic),
        () -> assertThat(record.key()).isEqualTo(id.asString()),
        () -> assertThat(envelope.get("eventId").asText()).isNotBlank(),
        () -> assertThat(envelope.get("correlationId").asText()).isEqualTo(correlationId.toString()),
        () -> assertThat(envelope.get("aggregateId").asText()).isEqualTo(id.asString()),
        () -> assertThat(envelope.get("eventType").asText()).isEqualTo(BankAccountActivatedEvent.EVENT_TYPE),
        () -> assertThat(envelope.get("eventSource").asText()).isEqualTo("accounts"),
        () -> assertThat(envelope.get("occurredAt").asText()).isNotBlank(),
        () -> assertThat(envelope.get("eventVersion").asInt()).isEqualTo(1),
        () -> assertThat(envelope.get("data").get("iban").asText()).isEqualTo(iban),
        () -> assertThat(envelope.get("data").get("currency").asText()).isEqualTo(Currency.EUR.name())
      );

      await()
        .atMost(Duration.ofSeconds(10))
        .pollInterval(Duration.ofMillis(100))
        .untilAsserted(() -> assertThat(findOutboxEvents(id)).isEmpty());
    }
  }

  private static KafkaConsumer<String, byte[]> createConsumer() {
    return new KafkaConsumer<>(Map.of(
      ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, KafkaContainerSupport.bootstrapServers(),
      ConsumerConfig.GROUP_ID_CONFIG, "accounts-outbox-it-" + UUID.randomUUID(),
      ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
      ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ByteArrayDeserializer.class,
      ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest",
      ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false
    ));
  }

  private static void assignAtEnd(KafkaConsumer<String, byte[]> consumer, String topic) {
    var partition = new TopicPartition(topic, 0);
    consumer.assign(List.of(partition));
    consumer.seekToEnd(List.of(partition));
  }

  private static ConsumerRecord<String, byte[]> pollForActivatedEvent(
    KafkaConsumer<String, byte[]> consumer,
    BankAccountId id
  ) {
    var deadline = Instant.now().plus(KAFKA_POLL_TIMEOUT);

    while (Instant.now().isBefore(deadline)) {
      for (var record : consumer.poll(Duration.ofMillis(250))) {
        if (id.asString().equals(record.key())) {
          return record;
        }
      }
    }

    throw new AssertionError("BankAccountActivated event was not received for account " + id.asString());
  }

  private static void ensureTopicExists(String topic) throws Exception {
    try (var admin = Admin.create(Map.of(
      AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG,
      KafkaContainerSupport.bootstrapServers()
    ))) {
      try {
        admin.createTopics(List.of(new NewTopic(topic, 1, (short) 1)))
          .all()
          .get(10, TimeUnit.SECONDS);
      }
      catch (ExecutionException exception) {
        if (!(exception.getCause() instanceof TopicExistsException)) {
          throw exception;
        }
      }
    }
  }
}
