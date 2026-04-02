package com.jcondotta.banking.accounts.infrastructure.adapters.output.outbox.processor;

import com.jcondotta.banking.accounts.infrastructure.adapters.output.outbox.store.OutboxQuery;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.outbox.store.OutboxEventStore;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.outbox.entity.OutboxStatus;
import com.jcondotta.banking.accounts.infrastructure.properties.OutboxProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Component
@RequiredArgsConstructor
public class VirtualThreadOutboxProcessor {

  private final OutboxEventClaimer eventClaimer;
  private final OutboxEventStore repository;
  private final OutboxProperties properties;

  private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

  public void process() {
    properties.shards().shardIds().forEach(this::processShard);
  }

  private void processShard(int shard) {
    var batchSize = properties.shards().batchSizePerShard();
    var events = repository.findPendingEvents(new OutboxQuery(shard, batchSize, OutboxStatus.PENDING));

    log.info("[shard={}] fetched {} pending events", shard, events.size());

    for (var event : events) {
      executor.submit(() -> eventClaimer.claim(shard, event));
    }
  }
}