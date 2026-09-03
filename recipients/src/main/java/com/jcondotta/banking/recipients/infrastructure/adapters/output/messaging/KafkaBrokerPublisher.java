package com.jcondotta.banking.recipients.infrastructure.adapters.output.messaging;

import com.jcondotta.banking.infrastructure.adapters.output.messaging.EventPublication;
import com.jcondotta.banking.infrastructure.adapters.output.messaging.EventEnvelope;
import com.jcondotta.banking.infrastructure.adapters.output.messaging.EventPublicationContext;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.util.concurrent.TimeUnit;

@Component
public class KafkaBrokerPublisher implements BrokerPublisher {

  private final KafkaTemplate<String, byte[]> kafkaTemplate;
  private final ObjectMapper objectMapper;
  private final KafkaPublisherProperties publisherProperties;

  public KafkaBrokerPublisher(
    KafkaTemplate<String, byte[]> kafkaTemplate,
    ObjectMapper objectMapper,
    KafkaPublisherProperties publisherProperties
  ) {
    this.kafkaTemplate = kafkaTemplate;
    this.objectMapper = objectMapper;
    this.publisherProperties = publisherProperties;
  }

  @Override
  public void publish(EventPublication<?> publication, EventPublicationContext context) {
    try {
      var payload = objectMapper.writeValueAsBytes(EventEnvelope.from(publication, context));
      var record = new ProducerRecord<>(publication.destination(), publication.key(), payload);

      kafkaTemplate.send(record)
        .get(publisherProperties.publishTimeout().toMillis(), TimeUnit.MILLISECONDS);
    }
    catch (JacksonException ex) {
      throw new RecipientEventPublishException(publication.event().eventType(), ex);
    }
    catch (InterruptedException ex) {
      Thread.currentThread().interrupt();
      throw new RecipientEventPublishException(publication.event().eventType(), ex);
    }
    catch (Exception ex) {
      throw new RecipientEventPublishException(publication.event().eventType(), ex);
    }
  }
}
