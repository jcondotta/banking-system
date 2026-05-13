package com.jcondotta.banking.accounts.outbox.infrastructure.adapters.output.outbox.dispatcher;

import com.jcondotta.application.logging.LogContext;
import com.jcondotta.application.logging.LogKey;
import com.jcondotta.banking.accounts.outbox.infrastructure.adapters.output.outbox.OutboxPublishException;
import com.jcondotta.banking.accounts.outbox.infrastructure.adapters.output.outbox.log.OutboxEventType;
import com.jcondotta.banking.accounts.outbox.infrastructure.adapters.output.outbox.log.OutboxLogKey;
import com.jcondotta.banking.accounts.outbox.infrastructure.adapters.output.outbox.concurrency.exceptions.ShardExecutionException;
import com.jcondotta.banking.accounts.outbox.infrastructure.adapters.output.outbox.concurrency.exceptions.ShardTimeoutException;
import com.jcondotta.banking.accounts.outbox.infrastructure.adapters.output.outbox.processor.OutboxEventShardProcessor;
import com.jcondotta.banking.accounts.outbox.infrastructure.adapters.output.outbox.store.OutboxEventStore;
import com.jcondotta.banking.accounts.outbox.infrastructure.adapters.output.outbox.store.exceptions.OutboxEventAlreadyProcessedException;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.outbox.store.OutboxQuery;
import com.jcondotta.banking.accounts.outbox.infrastructure.properties.OutboxShardsProperties;
import io.micrometer.observation.annotation.Observed;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class VirtualThreadOutboxDispatcher implements OutboxDispatcher {

  private static final Logger LOGGER = LoggerFactory.getLogger(VirtualThreadOutboxDispatcher.class);

  private final OutboxEventStore eventStore;
  private final OutboxEventShardProcessor eventShardProcessor;
  private final OutboxShardsProperties shardsProperties;

  private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

  private final AtomicBoolean dispatching = new AtomicBoolean(false);
  private final AtomicInteger inFlightTasks = new AtomicInteger(0);

  public VirtualThreadOutboxDispatcher(
    OutboxEventStore eventStore,
    OutboxEventShardProcessor eventShardProcessor,
    OutboxShardsProperties shardsProperties
  ) {
    this.eventStore = eventStore;
    this.eventShardProcessor = eventShardProcessor;
    this.shardsProperties = shardsProperties;
  }

  @Override
  @Observed(
    name = "accounts.outbox.dispatch",
    contextualName = "dispatchOutboxEvents",
    lowCardinalityKeyValues = {
      "component", "outbox",
      "operation", "dispatch"
    }
  )
  public void dispatch() {
    var logContext = LogContext.timed(LOGGER, OutboxEventType.DISPATCH);

    if (!dispatching.compareAndSet(false, true)) {
      logContext.warn("Outbox dispatch skipped")
        .failure()
        .with(LogKey.REASON, "dispatch_in_progress")
        .with(OutboxLogKey.IN_FLIGHT_TASKS, inFlightTasks.get())
        .log();
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
            catch (ShardTimeoutException | ShardExecutionException | OutboxPublishException | OutboxEventAlreadyProcessedException ex) {
              // The processor/completer already logs expected outbox failures with event context.
            }
            catch (Exception ex) {
              LogContext.timed(LOGGER, OutboxEventType.PROCESS)
                .with(OutboxLogKey.SHARD, event.getShard())
                .with(OutboxLogKey.EVENT_ID, event.getEventId())
                .with(OutboxLogKey.AGGREGATE_ID, event.getAggregateId())
                .with(OutboxLogKey.OUTBOX_EVENT_TYPE, event.getEventType())
                .error("Unexpected error processing outbox event", ex)
                .failure()
                .with(LogKey.REASON, "unhandled_error")
                .log();
            }
            finally {
              releaseIfDone();
            }
          });
        }
      }
    }
    catch (Exception ex) {
      logContext.error("Unexpected error during outbox dispatch", ex)
        .failure()
        .with(LogKey.REASON, "internal_error")
        .log();
    }
    finally {
      // Release the "submission loop" hold — allows the last in-flight task to close the cycle
      releaseIfDone();
    }
  }

  private void releaseIfDone() {
    if (inFlightTasks.decrementAndGet() == 0) {
      dispatching.set(false);
    }
  }

  @PreDestroy
  public void shutdown() {
    executor.shutdown();
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
}
