package com.jcondotta.banking.accounts.outbox.infrastructure.adapters.output.outbox.processor;

import com.jcondotta.banking.accounts.outbox.infrastructure.adapters.output.outbox.OutboxEventCompleter;
import com.jcondotta.banking.accounts.outbox.infrastructure.adapters.output.outbox.concurrency.ShardExecutor;
import com.jcondotta.banking.accounts.outbox.infrastructure.adapters.output.outbox.concurrency.exceptions.ShardExecutionException;
import com.jcondotta.banking.accounts.outbox.infrastructure.adapters.output.outbox.concurrency.exceptions.ShardTimeoutException;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.outbox.entity.OutboxEntity;
import com.jcondotta.banking.accounts.outbox.infrastructure.adapters.output.outbox.metrics.OutboxMetrics;
import com.jcondotta.banking.accounts.outbox.infrastructure.adapters.output.outbox.store.OutboxEventStore;
import com.jcondotta.banking.accounts.outbox.infrastructure.properties.OutboxProcessingProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxEventShardProcessor {

  private final ShardExecutor<Integer> shardExecutor;
  private final OutboxEventStore eventStore;
  private final OutboxEventCompleter eventPublisher;

  private final OutboxProcessingProperties properties;
  private final OutboxMetrics outboxMetrics;

  public void process(OutboxEntity event) {
    if (event.getRetryCount() >= properties.maxRetries()) {
      log.error("[shard={}] event {} exceeded max retries ({}), moving to dead letter",
        event.getShard(), event.getEventId(), properties.maxRetries());
      eventStore.deadLetterEvent(event);
      outboxMetrics.incrementDeadLetterEvent(event.getShard());
      return;
    }

    var waitSample = outboxMetrics.startTimer();
    try {
      shardExecutor.execute(event.getShard(), properties.acquireTimeout(), () -> {
        outboxMetrics.stopShardWaitTimer(waitSample, event.getShard());

        var claimed = eventStore.tryClaimEvent(event);
        if (claimed.isEmpty()) {
          outboxMetrics.incrementClaimSkipped(event.getShard());
          log.info("[shard={}] event {} already claimed by another node, skipping", event.getShard(), event.getEventId());
          return;
        }

        eventPublisher.handle(claimed.get());
      });
    }
    catch (ShardTimeoutException | ShardExecutionException e) {
      outboxMetrics.stopShardWaitTimer(waitSample, event.getShard());
      outboxMetrics.incrementSemaphoreTimeout(event.getShard());
      throw e;
    }
  }
}
