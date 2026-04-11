package com.jcondotta.banking.accounts.outbox.infrastructure.adapters.output.outbox.worker;

import com.jcondotta.banking.accounts.outbox.infrastructure.adapters.output.outbox.dispatcher.OutboxDispatcher;
import com.jcondotta.banking.accounts.outbox.infrastructure.properties.OutboxPollingProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxWorkerRunner implements ApplicationRunner {

  private final OutboxDispatcher dispatcher;
  private final OutboxPollingProperties pollingProperties;

  @Override
  public void run(ApplicationArguments args) {
    Thread.ofPlatform() // 👈 FIX PRINCIPAL (non-daemon)
      .name("outbox-worker")
      .start(this::workerLoop);

    log.info("Outbox worker started with polling interval: {}", pollingProperties.interval());
  }

  private void workerLoop() {
    var interval = pollingProperties.interval();

    while (!Thread.currentThread().isInterrupted()) {
      try {
        dispatcher.dispatch();
      } catch (Exception e) {
        log.error("Outbox dispatch failed, will retry after interval", e);
      }

      try {
        Thread.sleep(interval.toMillis()); // 👈 FIX (Duration → millis)
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        log.info("Outbox worker interrupted, shutting down");
        break;
      }
    }
  }
}