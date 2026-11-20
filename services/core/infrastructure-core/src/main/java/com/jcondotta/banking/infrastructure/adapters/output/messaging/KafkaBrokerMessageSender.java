package com.jcondotta.banking.infrastructure.adapters.output.messaging;

import com.jcondotta.banking.infrastructure.adapters.output.messaging.exceptions.BrokerMessageSendException;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class KafkaBrokerMessageSender implements BrokerMessageSender {

  private final KafkaTemplate<String, byte[]> kafkaTemplate;

  @Override
  public void send(BrokerMessage message, Duration timeout) {
    try {
      var record = new ProducerRecord<>(message.destination(), message.key(), message.payload());
      kafkaTemplate.send(record)
        .get(timeout.toMillis(), TimeUnit.MILLISECONDS);
    }
    catch (InterruptedException ex) {
      Thread.currentThread().interrupt();
      throw new BrokerMessageSendException(message.destination(), message.key(), ex);
    }
    catch (Exception ex) {
      throw new BrokerMessageSendException(message.destination(), message.key(), ex);
    }
  }
}
