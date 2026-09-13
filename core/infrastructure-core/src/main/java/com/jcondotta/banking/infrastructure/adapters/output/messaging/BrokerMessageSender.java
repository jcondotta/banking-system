package com.jcondotta.banking.infrastructure.adapters.output.messaging;

import java.time.Duration;

public interface BrokerMessageSender {

  void send(BrokerMessage message, Duration timeout);
}
