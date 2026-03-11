package com.jcondotta.banking.accounts.infrastructure.adapters.output.messaging.kafka;

import com.jcondotta.banking.accounts.domain.bankaccount.events.BankAccountClosedEvent;
import com.jcondotta.banking.accounts.infrastructure.properties.BankAccountClosedTopicProperties;
import com.jcondotta.domain.events.DomainEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaBankAccountClosedEventPublisher implements com.jcondotta.banking.accounts.application.bankaccount.ports.output.messaging.BankAccountClosedEventPublisher {

  private final KafkaTemplate<String, Object> kafkaTemplate;
  private final BankAccountClosedTopicProperties topicProperties;

  @Override
  public void publish(DomainEvent event) {
    if (event instanceof BankAccountClosedEvent bankAccountClosedEvent) {

      log.info(
        "Publishing BankAccountClosedEvent to Kafka [topic={}, key={}]",
        topicProperties.topicName(),
        bankAccountClosedEvent.aggregateId()
      );

//      var producerRecord = new ProducerRecord<String, Object>(
//        topicProperties.topicName(),
//        bankAccountClosedEvent.id().value().toString(),
//        messageMapper.toMessage(bankAccountClosedEvent)
//      );
//
//      kafkaTemplate.send(producerRecord);
    }
  }
}