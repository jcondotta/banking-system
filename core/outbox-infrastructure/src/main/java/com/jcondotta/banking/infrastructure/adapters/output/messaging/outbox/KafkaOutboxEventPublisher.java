package com.jcondotta.banking.infrastructure.adapters.output.messaging.outbox;

import com.jcondotta.banking.infrastructure.adapters.output.messaging.BrokerMessage;
import com.jcondotta.banking.infrastructure.adapters.output.messaging.BrokerMessageSender;
import com.jcondotta.banking.infrastructure.adapters.output.messaging.exceptions.BrokerMessageSendException;
import com.jcondotta.banking.infrastructure.outbox.exceptions.OutboxPublishException;
import com.jcondotta.banking.infrastructure.outbox.properties.OutboxProperties;
import com.jcondotta.banking.infrastructure.outbox.record.OutboxRecord;
import lombok.RequiredArgsConstructor;

import java.nio.charset.StandardCharsets;

@RequiredArgsConstructor
public class KafkaOutboxEventPublisher<T extends OutboxRecord> implements OutboxEventPublisher<T> {

  private final BrokerMessageSender messageSender;
  private final OutboxProperties outboxProperties;

  @Override
  public void send(T event) {
    var topic = event.getDestination();
    var key = event.getMessageKey();
    var payload = event.getPayload().getBytes(StandardCharsets.UTF_8);
    var publishTimeout = outboxProperties.worker().processing().publishTimeout();

    try {
      messageSender.send(new BrokerMessage(topic, key, payload), publishTimeout);
    }
    catch (BrokerMessageSendException ex) {
      throw new OutboxPublishException("Failed to publish event to Kafka. topic=%s, messageKey=%s".formatted(topic, key), ex.getCause());
    }
    catch (Exception ex) {
      throw new OutboxPublishException("Failed to publish event to Kafka. topic=%s, messageKey=%s".formatted(topic, key), ex);
    }
  }
}
