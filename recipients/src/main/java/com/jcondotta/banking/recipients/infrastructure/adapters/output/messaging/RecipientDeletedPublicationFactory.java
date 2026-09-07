package com.jcondotta.banking.recipients.infrastructure.adapters.output.messaging;

import com.jcondotta.banking.infrastructure.adapters.output.messaging.DefaultEventPublication;
import com.jcondotta.banking.infrastructure.adapters.output.messaging.EventPublication;
import com.jcondotta.banking.infrastructure.adapters.output.messaging.EventPublicationFactory;
import com.jcondotta.banking.recipients.domain.recipient.events.RecipientDeletedEvent;
import com.jcondotta.banking.recipients.infrastructure.adapters.output.messaging.properties.KafkaTopicsProperties;
import org.springframework.stereotype.Component;

@Component
public class RecipientDeletedPublicationFactory implements EventPublicationFactory<RecipientDeletedEvent> {

  private final String destination;

  public RecipientDeletedPublicationFactory(KafkaTopicsProperties topicsProperties) {
    this.destination = topicsProperties.recipientDeleted().topicName();
  }

  @Override
  public Class<RecipientDeletedEvent> domainEventType() {
    return RecipientDeletedEvent.class;
  }

  @Override
  public EventPublication<RecipientDeletedEvent> create(RecipientDeletedEvent event) {
    return new DefaultEventPublication<>(event, destination, event.data().bankAccountId().toString());
  }
}
