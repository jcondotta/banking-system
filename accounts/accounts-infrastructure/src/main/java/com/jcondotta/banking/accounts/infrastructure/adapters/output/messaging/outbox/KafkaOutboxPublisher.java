package com.jcondotta.banking.accounts.infrastructure.adapters.output.messaging.outbox;

import com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.entity.OutboxEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaOutboxPublisher implements OutboxPublisher {

  private final KafkaTemplate<String, byte[]> kafkaTemplate;

  @Override
  public void publish(OutboxEntity outboxEntity) {
    var topic = outboxEntity.getEventType();
    var aggregateId = outboxEntity.getAggregateId();

    byte[] payload = outboxEntity.getPayload().getBytes(StandardCharsets.UTF_8);

    kafkaTemplate.send(topic, aggregateId, payload);
  }
}
