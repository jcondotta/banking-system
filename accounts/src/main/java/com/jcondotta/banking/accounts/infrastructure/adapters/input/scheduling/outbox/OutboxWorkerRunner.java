package com.jcondotta.banking.accounts.infrastructure.adapters.input.scheduling.outbox;

import com.jcondotta.banking.accounts.infrastructure.outbox.dispatcher.OutboxDispatcher;
import com.jcondotta.banking.accounts.infrastructure.outbox.properties.OutboxPollingProperties;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Component
@ConditionalOnProperty(prefix = "app.outbox.worker", name = "enabled", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
public class OutboxWorkerRunner implements ApplicationRunner {

  private final OutboxDispatcher dispatcher;
  private final OutboxPollingProperties pollingProperties;

  private final ExecutorService executor = Executors.newSingleThreadExecutor(
    Thread.ofPlatform().name("outbox-worker").factory()
  );

  @Override
  public void run(ApplicationArguments args) {
    executor.submit(this::workerLoop);
  }

  @PreDestroy
  public void shutdown() {
    executor.shutdownNow();
    try {
      if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
        executor.shutdownNow();
      }
    }
    catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      executor.shutdownNow();
    }
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
