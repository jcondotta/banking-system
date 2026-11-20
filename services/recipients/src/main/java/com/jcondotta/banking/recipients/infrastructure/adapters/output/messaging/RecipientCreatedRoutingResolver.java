package com.jcondotta.banking.recipients.infrastructure.adapters.output.messaging;

import com.jcondotta.banking.infrastructure.adapters.output.messaging.EventRoutingResolver;
import com.jcondotta.banking.infrastructure.adapters.output.messaging.EventRouting;
import com.jcondotta.banking.recipients.domain.recipient.events.RecipientCreatedEvent;
import com.jcondotta.banking.recipients.infrastructure.adapters.output.messaging.properties.KafkaTopicsProperties;
import org.springframework.stereotype.Component;

@Component
public class RecipientCreatedRoutingResolver implements EventRoutingResolver<RecipientCreatedEvent> {

  private final String destination;

  public RecipientCreatedRoutingResolver(KafkaTopicsProperties topicsProperties) {
    this.destination = topicsProperties.recipientCreated().topicName();
  }

  @Override
  public Class<RecipientCreatedEvent> domainEventType() {
    return RecipientCreatedEvent.class;
  }

  @Override
  public EventRouting resolve(RecipientCreatedEvent event) {
    return new EventRouting(destination, event.data().bankAccountId().toString());
  }
}
