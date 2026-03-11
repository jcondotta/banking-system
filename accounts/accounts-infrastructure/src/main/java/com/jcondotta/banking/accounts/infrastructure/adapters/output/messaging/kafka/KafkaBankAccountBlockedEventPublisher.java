package com.jcondotta.banking.accounts.infrastructure.adapters.output.messaging.kafka;

import com.jcondotta.banking.accounts.domain.bankaccount.events.BankAccountBlockedEvent;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.messaging.mapper.BankAccountBlockedIntegrationEventMapper;
import com.jcondotta.banking.accounts.infrastructure.properties.BankAccountBlockedTopicProperties;
import com.jcondotta.domain.events.DomainEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaBankAccountBlockedEventPublisher implements com.jcondotta.banking.accounts.application.bankaccount.ports.output.messaging.BankAccountBlockedEventPublisher {

  private final KafkaTemplate<String, Object> kafkaTemplate;
  private final BankAccountBlockedIntegrationEventMapper messageMapper;
  private final BankAccountBlockedTopicProperties topicProperties;

  @Override
  public void publish(DomainEvent event) {
    if (event instanceof BankAccountBlockedEvent bankAccountBlockedEvent) {

      log.info(
        "Publishing BankAccountBlockedEvent to Kafka [topic={}, key={}]",
        topicProperties.topicName(),
        bankAccountBlockedEvent.aggregateId()
      );

//      var producerRecord = new ProducerRecord<String, Object>(
//        topicProperties.topicName(),
//        bankAccountBlockedEvent.id().value().toString(),
//        messageMapper.toMessage(bankAccountBlockedEvent)
//      );
//
//      kafkaTemplate.send(producerRecord);
    }
  }
}
