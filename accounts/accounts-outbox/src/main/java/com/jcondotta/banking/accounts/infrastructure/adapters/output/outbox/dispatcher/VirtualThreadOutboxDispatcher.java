package com.jcondotta.banking.accounts.infrastructure.adapters.output.outbox.dispatcher;

import com.jcondotta.banking.accounts.infrastructure.adapters.output.outbox.processor.OutboxEventShardProcessor;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.outbox.store.OutboxEventStore;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.outbox.store.OutboxQuery;
import com.jcondotta.banking.accounts.infrastructure.properties.OutboxShardsProperties;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
@RequiredArgsConstructor
public class VirtualThreadOutboxDispatcher implements OutboxDispatcher {

  private final OutboxEventStore eventStore;
  private final OutboxEventShardProcessor eventShardProcessor;
  private final OutboxShardsProperties shardsProperties;

  private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

  private final AtomicBoolean dispatching = new AtomicBoolean(false);
  private final AtomicInteger inFlightTasks = new AtomicInteger(0);

  @Override
  public void dispatch() {
    if (!dispatching.compareAndSet(false, true)) {
      log.warn("Previous dispatch cycle still in progress ({} tasks in flight), skipping", inFlightTasks.get());
      return;
    }

    // Initialize to 1 to represent "submission loop in progress".
    // Prevents a fast-completing task from flipping dispatching=false before submission ends.
    inFlightTasks.set(1);

    try {
      var batchSize = shardsProperties.batchSizePerShard();

      for (var shard : shardsProperties.shardIds()) {
        var outboxQuery = OutboxQuery.of(shard, batchSize);
        var pendingEvents = eventStore.findPendingEvents(outboxQuery);

        for (var event : pendingEvents) {
          inFlightTasks.incrementAndGet();
          executor.submit(() -> {
            try {
              eventShardProcessor.process(event);
            }
            catch (Exception ex) {
              log.error("[shard={}] unhandled exception processing event {}", event.getShard(), event.getEventId(), ex);
            }
            finally {
              releaseIfDone();
            }
          });
        }
      }
    }
    catch (Exception ex) {
      log.error("Unexpected error during outbox dispatch", ex);
    }
    finally {
      // Release the "submission loop" hold — allows the last in-flight task to close the cycle
      releaseIfDone();
    }
  }

  private void releaseIfDone() {
    if (inFlightTasks.decrementAndGet() == 0) {
      dispatching.set(false);
      log.debug("Dispatch cycle complete");
    }
  }

  @PreDestroy
  public void shutdown() {
    log.info("Shutting down outbox dispatcher executor");
    executor.shutdown();
    try {
      if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
        log.warn("Outbox executor did not terminate within 30s, forcing shutdown");
        executor.shutdownNow();
      }
    }
    catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      executor.shutdownNow();
    }
  }
}
