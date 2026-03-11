package com.jcondotta.banking.accounts.infrastructure.adapters.output.messaging.kafka;

import com.jcondotta.banking.accounts.domain.bankaccount.events.BankAccountJointHolderAddedEvent;
import com.jcondotta.banking.accounts.infrastructure.properties.JointAccountHolderAddedTopicProperties;
import com.jcondotta.domain.events.DomainEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaJointAccountHolderAddedEventPublisher implements com.jcondotta.banking.accounts.application.bankaccount.ports.output.messaging.JointAccountHolderAddedEventPublisher {

  private final KafkaTemplate<String, Object> kafkaTemplate;
//  private final JointAccountHolderAddedMessageMapper messageMapper;
  private final JointAccountHolderAddedTopicProperties topicProperties;

  @Override
  public void publish(DomainEvent event) {
    if (event instanceof BankAccountJointHolderAddedEvent bankAccountJointHolderAddedEvent) {

      log.info(
        "Publishing JointAccountHolderAddedEvent to Kafka [topic={}, key={}]",
        topicProperties.topicName(),
        bankAccountJointHolderAddedEvent.aggregateId()
      );

//      var producerRecord = new ProducerRecord<String, Object>(
//        topicProperties.topicName(),
//        jointAccountHolderAddedEvent.id().value().toString(),
//        messageMapper.toMessage(jointAccountHolderAddedEvent)
//      );
//
//      kafkaTemplate.send(producerRecord);
    }
  }
}
