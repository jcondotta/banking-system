package com.jcondotta.banking.accounts.infrastructure.adapters.output.outbox.publisher;

import com.jcondotta.banking.accounts.infrastructure.adapters.output.outbox.OutboxPublishException;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.outbox.entity.OutboxEntity;
import com.jcondotta.banking.accounts.infrastructure.properties.OutboxProcessingProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

@Slf4j
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
      var result = kafkaTemplate.send(topic, aggregateId, payload)
        .get(properties.publishTimeout().toMillis(), TimeUnit.MILLISECONDS);

      log.info("Event sent to Kafka. topic={}, aggregateId={}, offset={}",
        topic, aggregateId, result.getRecordMetadata().offset());
    }
    catch (Exception ex) {
      log.error("Failed to send event to Kafka. topic={}, aggregateId={}", topic, aggregateId, ex);
      throw new OutboxPublishException("Failed to publish event to Kafka. topic=%s, aggregateId=%s".formatted(topic, aggregateId), ex);
    }
  }
}
