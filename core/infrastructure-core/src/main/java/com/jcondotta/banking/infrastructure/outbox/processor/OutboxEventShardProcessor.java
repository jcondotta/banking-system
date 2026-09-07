package com.jcondotta.banking.infrastructure.outbox.processor;

import com.jcondotta.application.logging.LogContext;
import com.jcondotta.application.logging.LogKey;
import com.jcondotta.banking.infrastructure.outbox.concurrency.ShardExecutor;
import com.jcondotta.banking.infrastructure.outbox.concurrency.exceptions.ShardExecutionException;
import com.jcondotta.banking.infrastructure.outbox.concurrency.exceptions.ShardTimeoutException;
import com.jcondotta.banking.infrastructure.outbox.log.OutboxOperation;
import com.jcondotta.banking.infrastructure.outbox.log.OutboxFailureReason;
import com.jcondotta.banking.infrastructure.outbox.log.OutboxLogKey;
import com.jcondotta.banking.infrastructure.outbox.properties.OutboxProperties;
import com.jcondotta.banking.infrastructure.outbox.record.OutboxRecord;
import com.jcondotta.banking.infrastructure.outbox.store.OutboxEventStore;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RequiredArgsConstructor
public class OutboxEventShardProcessor<T extends OutboxRecord> {

  private static final Logger LOGGER = LoggerFactory.getLogger(OutboxEventShardProcessor.class);

  private final ShardExecutor<Integer> shardExecutor;
  private final OutboxEventStore<T> eventStore;
  private final OutboxEventCompleter<T> eventCompleter;
  private final OutboxProperties outboxProperties;

  public void process(T event) {
    var processing = outboxProperties.worker().processing();
    var retry = outboxProperties.worker().retry();

    var logContext = LogContext.timed(LOGGER, OutboxOperation.PROCESS)
      .with(OutboxLogKey.SHARD, event.getShard())
      .with(OutboxLogKey.EVENT_ID, event.getEventId())
      .with(LogKey.CORRELATION_ID, event.getCorrelationId())
      .with(OutboxLogKey.AGGREGATE_ID, event.getAggregateId())
      .with(LogKey.EVENT_TYPE, event.getEventType())
      .with(OutboxLogKey.ATTEMPT_COUNT, event.getAttemptCount());

    if (event.getAttemptCount() >= retry.maxRetries()) {
      eventStore.deadLetterEvent(event);
      logContext.warn("Outbox event moved to dead letter")
        .failure()
        .with(LogKey.REASON, OutboxFailureReason.MAX_RETRIES_EXCEEDED.normalize())
        .log();
      return;
    }

    try {
      shardExecutor.execute(event.getShard(), processing.acquireTimeout(), () -> {
        var claimed = eventStore.tryClaimEvent(event);
        if (claimed.isEmpty()) {
          logContext.debug("Outbox event claim skipped")
            .success()
            .with(LogKey.REASON, OutboxFailureReason.ALREADY_CLAIMED.normalize())
            .log();
          return;
        }

        eventCompleter.handle(claimed.get());
      });
    }
    catch (ShardTimeoutException e) {
      logContext.warn("Outbox event processing delayed")
        .failure()
        .with(LogKey.REASON, OutboxFailureReason.SHARD_TIMEOUT.normalize())
        .log();
      throw e;
    }
    catch (ShardExecutionException e) {
      logContext.error("Outbox event processing failed", e)
        .failure()
        .with(LogKey.REASON, OutboxFailureReason.SHARD_EXECUTION_ERROR.normalize())
        .log();
      throw e;
    }
  }
}
