package com.jcondotta.banking.accounts.infrastructure.adapters.output.outbox.scheduler;

import com.jcondotta.banking.accounts.infrastructure.adapters.output.outbox.dispatcher.OutboxDispatcher;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OutboxScheduler {

  private final OutboxDispatcher dispatcher;

  @Scheduled(fixedDelayString = "${app.outbox.polling.interval}")
  public void run() {
    dispatcher.dispatch();
  }
}