package com.jcondotta.banking.accounts.infrastructure.adapters.output.outbox.processor;

import com.jcondotta.banking.accounts.infrastructure.adapters.output.outbox.concurrency.ShardExecutor;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.outbox.publisher.OutboxPublisher;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.outbox.store.OutboxEventStore;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.outbox.entity.OutboxEntity;
import com.jcondotta.banking.accounts.infrastructure.properties.OutboxProcessingProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxEventProcessor {

  private final ShardExecutor<Integer> shardExecutor;
  private final OutboxEventStore repository;
  private final OutboxPublisher publisher;
  private final OutboxProcessingProperties outboxProcessingProperties;

  public void process(int shard, OutboxEntity event) {
    shardExecutor.execute(shard, outboxProcessingProperties.timeout(), () -> {
      try {
        log.info("[shard={}] processing event {}", shard, event.getEventId());
        publisher.publish(event);
        repository.markEventAsPublished(event);
      }
      catch (Exception ex) {
        log.error("[shard={}] error processing event {}", shard, event.getEventId(), ex);
      }
    });
  }
}