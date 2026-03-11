package com.jcondotta.banking.accounts.infrastructure.adapters.output.messaging.kafka;

import com.jcondotta.banking.accounts.domain.bankaccount.events.BankAccountUnblockedEvent;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.messaging.mapper.BankAccountUnblockedIntegrationEventMapper;
import com.jcondotta.banking.accounts.infrastructure.properties.BankAccountUnblockedTopicProperties;
import com.jcondotta.domain.events.DomainEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaBankAccountUnblockedEventPublisher implements com.jcondotta.banking.accounts.application.bankaccount.ports.output.messaging.BankAccountUnblockedEventPublisher {

  private final KafkaTemplate<String, Object> kafkaTemplate;
  private final BankAccountUnblockedIntegrationEventMapper messageMapper;
  private final BankAccountUnblockedTopicProperties topicProperties;

  @Override
  public void publish(DomainEvent event) {
    if (event instanceof BankAccountUnblockedEvent bankAccountUnblockedEvent) {

      log.info(
        "Publishing BankAccountUnblockedEvent to Kafka [topic={}, key={}]",
        topicProperties.topicName(),
        bankAccountUnblockedEvent.aggregateId()
      );

//      var producerRecord = new ProducerRecord<String, Object>(
//        topicProperties.topicName(),
//        bankAccountUnblockedEvent.id().value().toString(),
//        messageMapper.toMessage(bankAccountUnblockedEvent)
//      );
//
//      kafkaTemplate.send(producerRecord);
    }
  }
}