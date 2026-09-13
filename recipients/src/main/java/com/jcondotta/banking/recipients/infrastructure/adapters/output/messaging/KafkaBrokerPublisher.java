package com.jcondotta.banking.recipients.infrastructure.adapters.output.messaging;

import com.jcondotta.banking.infrastructure.adapters.output.messaging.BrokerMessage;
import com.jcondotta.banking.infrastructure.adapters.output.messaging.BrokerMessageSender;
import com.jcondotta.banking.infrastructure.adapters.output.messaging.EventPublication;
import com.jcondotta.banking.infrastructure.adapters.output.messaging.exceptions.BrokerMessageSendException;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@Component
public class KafkaBrokerPublisher implements BrokerPublisher {

  private final BrokerMessageSender messageSender;
  private final ObjectMapper objectMapper;
  private final KafkaPublisherProperties publisherProperties;

  public KafkaBrokerPublisher(
    BrokerMessageSender messageSender,
    ObjectMapper objectMapper,
    KafkaPublisherProperties publisherProperties
  ) {
    this.messageSender = messageSender;
    this.objectMapper = objectMapper;
    this.publisherProperties = publisherProperties;
  }

  @Override
  public void publish(EventPublication publication) {
    var envelope = publication.envelope();
    var routing = publication.routing();

    try {
      var payload = objectMapper.writeValueAsBytes(envelope);
      var message = new BrokerMessage(routing.destination(), routing.key(), payload);
      messageSender.send(message, publisherProperties.publishTimeout());
    }
    catch (JacksonException ex) {
      throw new RecipientEventPublishException(envelope.eventType(), ex);
    }
    catch (BrokerMessageSendException ex) {
      throw new RecipientEventPublishException(envelope.eventType(), ex.getCause());
    }
    catch (Exception ex) {
      throw new RecipientEventPublishException(envelope.eventType(), ex);
    }
  }
}
