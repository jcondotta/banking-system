package com.jcondotta.banking.accounts.infrastructure.adapters.output.messaging.kafka;

import com.jcondotta.banking.accounts.domain.bankaccount.events.BankAccountActivatedEvent;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.messaging.mapper.BankAccountActivatedIntegrationEventMapper;
import com.jcondotta.banking.accounts.infrastructure.properties.BankAccountActivatedTopicProperties;
import com.jcondotta.domain.events.DomainEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaBankAccountActivatedEventPublisher implements com.jcondotta.banking.accounts.application.bankaccount.ports.output.messaging.BankAccountActivatedEventPublisher {

  private final KafkaTemplate<String, Object> kafkaTemplate;
  private final BankAccountActivatedIntegrationEventMapper messageMapper;
  private final BankAccountActivatedTopicProperties topicProperties;

  @Override
  public void publish(DomainEvent event) {
    if (event instanceof BankAccountActivatedEvent bankAccountActivatedEvent) {

      log.info(
        "Publishing BankAccountActivatedEvent to Kafka [topic={}, key={}]",
        topicProperties.topicName(),
        bankAccountActivatedEvent.aggregateId()
      );

//      var producerRecord = new ProducerRecord<String, Object>(
//        topicProperties.topicName(),
//        bankAccountActivatedEvent.id().value().toString(),
//        messageMapper.toMessage(bankAccountActivatedEvent)
//      );
//
//      kafkaTemplate.send(producerRecord);
    }
  }
}
