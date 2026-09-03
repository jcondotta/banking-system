package com.jcondotta.banking.recipients.infrastructure.adapters.output.messaging;

import com.jcondotta.banking.infrastructure.adapters.output.messaging.DefaultEventPublication;
import com.jcondotta.banking.infrastructure.adapters.output.messaging.EventPublication;
import com.jcondotta.banking.infrastructure.adapters.output.messaging.EventPublicationFactory;
import com.jcondotta.banking.recipients.domain.recipient.events.RecipientCreatedEvent;
import org.springframework.stereotype.Component;

@Component
public class RecipientCreatedPublicationFactory implements EventPublicationFactory<RecipientCreatedEvent> {

  static final String DESTINATION = "recipients-created";

  @Override
  public Class<RecipientCreatedEvent> domainEventType() {
    return RecipientCreatedEvent.class;
  }

  @Override
  public EventPublication<RecipientCreatedEvent> create(RecipientCreatedEvent event) {
    return new DefaultEventPublication<>(event, DESTINATION, event.data().bankAccountId().toString());
  }
}
