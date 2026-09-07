package com.jcondotta.banking.infrastructure.adapters.output.messaging.outbox;

import com.jcondotta.banking.infrastructure.outbox.exceptions.OutboxPublishException;
import com.jcondotta.banking.infrastructure.outbox.properties.OutboxProperties;
import com.jcondotta.banking.infrastructure.outbox.record.OutboxRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class KafkaOutboxEventPublisher<T extends OutboxRecord> implements OutboxEventPublisher<T> {

  private final KafkaTemplate<String, byte[]> kafkaTemplate;
  private final OutboxProperties outboxProperties;

  @Override
  public void send(T event) {
    var topic = event.getDestination();
    var key = event.getMessageKey();
    var payload = event.getPayload().getBytes(StandardCharsets.UTF_8);
    var publishTimeout = outboxProperties.worker().processing().publishTimeout();

    try {
      kafkaTemplate.send(topic, key, payload)
        .get(publishTimeout.toMillis(), TimeUnit.MILLISECONDS);
    }
    catch (Exception ex) {
      throw new OutboxPublishException("Failed to publish event to Kafka. topic=%s, messageKey=%s".formatted(topic, key), ex);
    }
  }
}
