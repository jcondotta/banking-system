package com.jcondotta.banking.recipients.infrastructure.adapters.output.messaging;

import com.jcondotta.banking.infrastructure.adapters.output.messaging.DefaultEventPublication;
import com.jcondotta.banking.infrastructure.adapters.output.messaging.EventPublication;
import com.jcondotta.banking.infrastructure.adapters.output.messaging.EventPublicationFactory;
import com.jcondotta.banking.recipients.domain.recipient.events.RecipientCreatedEvent;
import com.jcondotta.banking.recipients.infrastructure.adapters.output.messaging.properties.KafkaTopicsProperties;
import org.springframework.stereotype.Component;

@Component
public class RecipientCreatedPublicationFactory implements EventPublicationFactory<RecipientCreatedEvent> {

  private final String destination;

  public RecipientCreatedPublicationFactory(KafkaTopicsProperties topicsProperties) {
    this.destination = topicsProperties.recipientCreated().topicName();
  }

  @Override
  public Class<RecipientCreatedEvent> domainEventType() {
    return RecipientCreatedEvent.class;
  }

  @Override
  public EventPublication<RecipientCreatedEvent> create(RecipientCreatedEvent event) {
    return new DefaultEventPublication<>(event, destination, event.data().bankAccountId().toString());
  }
}
