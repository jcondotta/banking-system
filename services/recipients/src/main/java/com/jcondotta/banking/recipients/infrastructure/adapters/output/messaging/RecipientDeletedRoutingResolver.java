package com.jcondotta.banking.recipients.infrastructure.adapters.output.messaging;

import com.jcondotta.banking.infrastructure.adapters.output.messaging.EventRoutingResolver;
import com.jcondotta.banking.infrastructure.adapters.output.messaging.EventRouting;
import com.jcondotta.banking.recipients.domain.recipient.events.RecipientDeletedEvent;
import com.jcondotta.banking.recipients.infrastructure.adapters.output.messaging.properties.KafkaTopicsProperties;
import org.springframework.stereotype.Component;

@Component
public class RecipientDeletedRoutingResolver implements EventRoutingResolver<RecipientDeletedEvent> {

  private final String destination;

  public RecipientDeletedRoutingResolver(KafkaTopicsProperties topicsProperties) {
    this.destination = topicsProperties.recipientDeleted().topicName();
  }

  @Override
  public Class<RecipientDeletedEvent> domainEventType() {
    return RecipientDeletedEvent.class;
  }

  @Override
  public EventRouting resolve(RecipientDeletedEvent event) {
    return new EventRouting(destination, event.data().bankAccountId().toString());
  }
}
