package com.jcondotta.banking.accounts.infrastructure.adapters.output.outbox.scheduler;

import com.jcondotta.banking.accounts.infrastructure.adapters.output.outbox.processor.VirtualThreadOutboxProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxScheduler {

  private final VirtualThreadOutboxProcessor processor;

  @Scheduled(fixedDelayString = "${app.outbox.polling.interval}")
  public void processOutbox() {
    log.debug("Starting outbox processing");

    processor.process();

    log.debug("Finished outbox processing");
  }
}