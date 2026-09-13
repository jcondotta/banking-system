package com.jcondotta.banking.recipients.infrastructure.adapters.output.messaging;

import com.jcondotta.banking.infrastructure.adapters.output.messaging.EventPublication;

public interface BrokerPublisher {

  void publish(EventPublication publication);
}
