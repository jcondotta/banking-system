package com.jcondotta.banking.infrastructure.adapters.input.scheduling.outbox;

import com.jcondotta.banking.infrastructure.outbox.dispatcher.OutboxDispatcher;
import jakarta.annotation.PreDestroy;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class OutboxWorkerRunner implements ApplicationRunner {

  private final OutboxDispatcher dispatcher;
  private final Duration interval;

  public OutboxWorkerRunner(OutboxDispatcher dispatcher, Duration interval) {
    this.dispatcher = dispatcher;
    this.interval = interval;
  }

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
