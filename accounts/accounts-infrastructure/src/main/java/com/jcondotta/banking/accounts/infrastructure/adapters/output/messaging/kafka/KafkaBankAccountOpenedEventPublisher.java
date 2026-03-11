package com.jcondotta.banking.accounts.infrastructure.adapters.output.messaging.kafka;

import com.jcondotta.banking.accounts.application.bankaccount.ports.output.messaging.BankAccountOpenedEventPublisher;
import com.jcondotta.banking.accounts.domain.bankaccount.events.BankAccountOpenedEvent;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.messaging.mapper.BankAccountOpenedIntegrationEventMapper;
import com.jcondotta.banking.accounts.infrastructure.properties.BankAccountOpenedTopicProperties;
import com.jcondotta.domain.events.DomainEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaBankAccountOpenedEventPublisher implements BankAccountOpenedEventPublisher {

  private final KafkaTemplate<String, Object> kafkaTemplate;
  private final BankAccountOpenedIntegrationEventMapper messageMapper;
  private final BankAccountOpenedTopicProperties topicProperties;

  @Override
  public void publish(DomainEvent event) {
    if (event instanceof BankAccountOpenedEvent bankAccountOpenedEvent) {

      var bankAccountId = bankAccountOpenedEvent.aggregateId().value();
      log.info(
        "Publishing BankAccountOpenedEvent to Kafka [topic={}, key={}]", topicProperties.topicName(), bankAccountId
      );

//      var producerRecord = new ProducerRecord<String, Object>(
//        topicProperties.topicName(),
//        id.toString(),
//        messageMapper.toIntegrationEvent(bankAccountOpenedEvent, UUID.randomUUID().toString())
//      );
//
//      kafkaTemplate.send(producerRecord);
    }
  }
}
