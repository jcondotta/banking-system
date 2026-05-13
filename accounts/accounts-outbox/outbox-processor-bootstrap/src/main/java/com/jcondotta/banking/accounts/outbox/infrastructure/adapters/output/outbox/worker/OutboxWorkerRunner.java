package com.jcondotta.banking.accounts.outbox.infrastructure.adapters.output.outbox.worker;

import com.jcondotta.banking.accounts.outbox.infrastructure.adapters.output.outbox.dispatcher.OutboxDispatcher;
import com.jcondotta.banking.accounts.outbox.infrastructure.properties.OutboxPollingProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "app.outbox.worker", name = "enabled", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
public class OutboxWorkerRunner implements ApplicationRunner {

  private final OutboxDispatcher dispatcher;
  private final OutboxPollingProperties pollingProperties;

  @Override
  public void run(ApplicationArguments args) {
    Thread.ofPlatform()
      .name("outbox-worker")
      .start(this::workerLoop);
  }

  private void workerLoop() {
    var interval = pollingProperties.interval();

    while (!Thread.currentThread().isInterrupted()) {
      try {
        dispatcher.dispatch();
      }
      catch (Exception ignored) {
        // The dispatcher logs failures with structured context.
      }

      try {
        Thread.sleep(interval.toMillis());
      }
      catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        break;
      }
    }
  }
}
