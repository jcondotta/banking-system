package com.jcondotta.banking.recipients.infrastructure.adapters.output.messaging;

import com.jcondotta.banking.infrastructure.adapters.output.messaging.DefaultEventPublication;
import com.jcondotta.banking.infrastructure.adapters.output.messaging.EventPublication;
import com.jcondotta.banking.infrastructure.adapters.output.messaging.EventPublicationFactory;
import com.jcondotta.banking.recipients.domain.recipient.events.RecipientDeletedEvent;
import org.springframework.stereotype.Component;

@Component
public class RecipientDeletedPublicationFactory implements EventPublicationFactory<RecipientDeletedEvent> {

  static final String DESTINATION = "recipients-deleted";

  @Override
  public Class<RecipientDeletedEvent> domainEventType() {
    return RecipientDeletedEvent.class;
  }

  @Override
  public EventPublication<RecipientDeletedEvent> create(RecipientDeletedEvent event) {
    return new DefaultEventPublication<>(event, DESTINATION, event.data().bankAccountId().toString());
  }
}
