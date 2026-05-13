package com.jcondotta.banking.accounts.outbox.infrastructure.adapters.output.outbox.processor;

import com.jcondotta.application.logging.LogContext;
import com.jcondotta.application.logging.LogKey;
import com.jcondotta.banking.accounts.outbox.infrastructure.adapters.output.outbox.OutboxEventCompleter;
import com.jcondotta.banking.accounts.outbox.infrastructure.adapters.output.outbox.concurrency.ShardExecutor;
import com.jcondotta.banking.accounts.outbox.infrastructure.adapters.output.outbox.concurrency.exceptions.ShardExecutionException;
import com.jcondotta.banking.accounts.outbox.infrastructure.adapters.output.outbox.concurrency.exceptions.ShardTimeoutException;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.outbox.entity.OutboxEntity;
import com.jcondotta.banking.accounts.outbox.infrastructure.adapters.output.outbox.log.OutboxEventType;
import com.jcondotta.banking.accounts.outbox.infrastructure.adapters.output.outbox.log.OutboxLogKey;
import com.jcondotta.banking.accounts.outbox.infrastructure.adapters.output.outbox.store.OutboxEventStore;
import com.jcondotta.banking.accounts.outbox.infrastructure.properties.OutboxProcessingProperties;
import io.micrometer.observation.annotation.Observed;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OutboxEventShardProcessor {

  private static final Logger LOGGER = LoggerFactory.getLogger(OutboxEventShardProcessor.class);

  private final ShardExecutor<Integer> shardExecutor;
  private final OutboxEventStore eventStore;
  private final OutboxEventCompleter eventPublisher;

  private final OutboxProcessingProperties properties;

  @Observed(
    name = "accounts.outbox.process",
    contextualName = "processOutboxEvent",
    lowCardinalityKeyValues = {
      "component", "outbox",
      "operation", "process"
    }
  )
  public void process(OutboxEntity event) {
    var logContext = LogContext.timed(LOGGER, OutboxEventType.PROCESS)
      .with(OutboxLogKey.SHARD, event.getShard())
      .with(OutboxLogKey.EVENT_ID, event.getEventId())
      .with(OutboxLogKey.AGGREGATE_ID, event.getAggregateId())
      .with(OutboxLogKey.OUTBOX_EVENT_TYPE, event.getEventType())
      .with(OutboxLogKey.RETRY_COUNT, event.getRetryCount());

    if (event.getRetryCount() >= properties.maxRetries()) {
      eventStore.deadLetterEvent(event);
      logContext.warn("Outbox event moved to dead letter")
        .failure()
        .with(LogKey.REASON, "max_retries_exceeded")
        .log();
      return;
    }

    try {
      shardExecutor.execute(event.getShard(), properties.acquireTimeout(), () -> {
        var claimed = eventStore.tryClaimEvent(event);
        if (claimed.isEmpty()) {
          logContext.debug("Outbox event claim skipped")
            .success()
            .with(LogKey.REASON, "already_claimed")
            .log();
          return;
        }

        eventPublisher.handle(claimed.get());
      });
    }
    catch (ShardTimeoutException e) {
      logContext.warn("Outbox event processing delayed")
        .failure()
        .with(LogKey.REASON, "shard_timeout")
        .log();
      throw e;
    }
    catch (ShardExecutionException e) {
      logContext.error("Outbox event processing failed", e)
        .failure()
        .with(LogKey.REASON, "shard_execution_error")
        .log();
      throw e;
    }
  }
}
