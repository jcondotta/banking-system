package com.jcondotta.banking.accounts.outbox.infrastructure.adapters.output.outbox.publisher;

import com.jcondotta.banking.accounts.outbox.infrastructure.adapters.output.outbox.OutboxPublishException;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.outbox.entity.OutboxEntity;
import com.jcondotta.banking.accounts.outbox.infrastructure.properties.OutboxProcessingProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class KafkaOutboxEventPublisher implements OutboxEventPublisher {

  private final KafkaTemplate<String, byte[]> kafkaTemplate;
  private final OutboxProcessingProperties properties;

  @Override
  public void send(OutboxEntity event) {
    var topic = event.getEventType();
    var aggregateId = event.getAggregateId();
    var payload = event.getPayload().getBytes(StandardCharsets.UTF_8);

    try {
      kafkaTemplate.send(topic, aggregateId, payload)
        .get(properties.publishTimeout().toMillis(), TimeUnit.MILLISECONDS);
    }
    catch (Exception ex) {
      throw new OutboxPublishException("Failed to publish event to Kafka. topic=%s, aggregateId=%s".formatted(topic, aggregateId), ex);
    }
  }
}
