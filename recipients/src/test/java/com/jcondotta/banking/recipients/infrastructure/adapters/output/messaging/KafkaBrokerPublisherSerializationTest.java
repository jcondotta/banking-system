package com.jcondotta.banking.recipients.infrastructure.adapters.output.messaging;

import com.jcondotta.banking.infrastructure.adapters.output.messaging.DefaultEventPublication;
import com.jcondotta.banking.infrastructure.adapters.output.messaging.EventPublicationContext;
import com.jcondotta.banking.recipients.domain.recipient.events.RecipientCreatedData;
import com.jcondotta.banking.recipients.domain.recipient.events.RecipientCreatedEvent;
import com.jcondotta.banking.recipients.domain.recipient.identity.BankAccountId;
import com.jcondotta.banking.recipients.domain.recipient.identity.RecipientId;
import com.jcondotta.domain.events.DomainEventMetadata;
import com.jcondotta.domain.identity.EventId;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.MockProducer;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.mock.MockProducerFactory;
import tools.jackson.databind.json.JsonMapper;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class KafkaBrokerPublisherSerializationTest {

  private static final String TOPIC_NAME = "recipients-created";
  private static final UUID CORRELATION_ID = UUID.fromString("ce75acbd-da91-4aca-ad03-e1fbb11429b6");
  private static final String EVENT_SOURCE = "recipients";
  private static final EventId EVENT_ID = EventId.of(UUID.fromString("90854175-5da4-4775-82a0-243a602a59df"));
  private static final RecipientId RECIPIENT_ID = RecipientId.of(UUID.fromString("10d723ea-fe73-4d58-9ed0-97c248955496"));
  private static final BankAccountId BANK_ACCOUNT_ID = BankAccountId.of(UUID.fromString("c328e5b7-0bf8-4acb-b09a-1dd0ed475c22"));
  private static final Instant OCCURRED_AT = Instant.parse("2026-01-01T00:00:00Z");

  @Test
  void shouldPublishEventPublicationEnvelope_whenPublicationIsValid() throws Exception {
    var mockProducer = new MockProducer<String, byte[]>(true, null, new StringSerializer(), new ByteArraySerializer());
    var producerFactory = new MockProducerFactory<String, byte[]>(() -> mockProducer);
    var kafkaTemplate = new KafkaTemplate<>(producerFactory);
    var publisher = new KafkaBrokerPublisher(
      kafkaTemplate,
      JsonMapper.builder().build(),
      new KafkaPublisherProperties(Duration.ofSeconds(1))
    );
    var event = new RecipientCreatedEvent(
      DomainEventMetadata.of(EVENT_ID, RECIPIENT_ID, OCCURRED_AT),
      new RecipientCreatedData(BANK_ACCOUNT_ID.value(), "Isabella Condotta", "BE68539007547034")
    );
    var publication = new RecipientCreatedPublicationFactory().create(event);

    publisher.publish(publication, publicationContext());

    var record = mockProducer.history().getFirst();

    assertThat(record.topic()).isEqualTo(TOPIC_NAME);
    assertThat(record.key()).isEqualTo(BANK_ACCOUNT_ID.asString());
    assertThat(JsonMapper.builder().build().readTree(record.value()).toString())
      .isEqualTo("{\"eventId\":\"90854175-5da4-4775-82a0-243a602a59df\",\"correlationId\":\"ce75acbd-da91-4aca-ad03-e1fbb11429b6\",\"aggregateId\":\"10d723ea-fe73-4d58-9ed0-97c248955496\",\"eventType\":\"recipient-created\",\"eventSource\":\"recipients\",\"occurredAt\":\"2026-01-01T00:00:00Z\",\"eventVersion\":1,\"data\":{\"bankAccountId\":\"c328e5b7-0bf8-4acb-b09a-1dd0ed475c22\",\"name\":\"Isabella Condotta\",\"iban\":\"BE68539007547034\"}}");
    assertThat(record.headers()).isEmpty();
  }

  @Test
  @SuppressWarnings("unchecked")
  void shouldPublishEachPublicationToItsOwnDestination() {
    var kafkaTemplate = (KafkaTemplate<String, byte[]>) mock(KafkaTemplate.class);
    var publishedRecords = new ArrayList<ProducerRecord<String, byte[]>>();
    when(kafkaTemplate.send(any(ProducerRecord.class))).thenAnswer(invocation -> {
      ProducerRecord<String, byte[]> record = invocation.getArgument(0);
      publishedRecords.add(record);
      return CompletableFuture.completedFuture(null);
    });
    var publisher = new KafkaBrokerPublisher(
      kafkaTemplate,
      JsonMapper.builder().build(),
      new KafkaPublisherProperties(Duration.ofSeconds(1))
    );
    var event = new RecipientCreatedEvent(
      DomainEventMetadata.of(EVENT_ID, RECIPIENT_ID, OCCURRED_AT),
      new RecipientCreatedData(BANK_ACCOUNT_ID.value(), "Isabella Condotta", "BE68539007547034")
    );

    publisher.publish(new DefaultEventPublication<>(event, "first-topic", "first-key"), publicationContext());
    publisher.publish(new DefaultEventPublication<>(event, "second-topic", "second-key"), publicationContext());

    assertThat(publishedRecords)
      .extracting(record -> record.topic() + ":" + record.key())
      .containsExactly("first-topic:first-key", "second-topic:second-key");
  }

  private static EventPublicationContext publicationContext() {
    return new EventPublicationContext(CORRELATION_ID, EVENT_SOURCE);
  }
}
