package com.jcondotta.banking.accounts.infrastructure.adapters.output.messaging.outbox;

import com.jcondotta.banking.accounts.infrastructure.adapters.output.messaging.outbox.news.ShardExecutor;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.messaging.outbox.query.OutboxQuery;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.messaging.outbox.query.OutboxRepository;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.entity.OutboxEntity;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.enums.OutboxStatus;
import com.jcondotta.banking.accounts.infrastructure.properties.OutboxProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Component
@RequiredArgsConstructor
public class VirtualThreadOutboxProcessor {

  private final ShardExecutor<Integer> shardExecutor;
  private final OutboxRepository repository;
  private final OutboxPublisher publisher;
  private final OutboxProperties properties;

  private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

  public void process() {
    properties.shards()
      .shardIds()
      .forEach(this::processShard);
  }

  private void processShard(int shard) {
    var batchSize = properties.shards().batchSizePerShard();
    var events = fetchPendingEvents(shard, batchSize);

    log.info("[shard={}] fetched {} pending events", shard, events.size());

    for (var event : events) {
      executor.submit(() -> submitEvent(shard, event));
    }
  }

  private List<OutboxEntity> fetchPendingEvents(int shard, int batchSize) {
    return repository.findEvents(new OutboxQuery(shard, batchSize, OutboxStatus.PENDING));
  }

  private void submitEvent(int shard, OutboxEntity event) {
    shardExecutor.execute(
      shard,
      properties.processing().timeout(),
      () -> processEvent(shard, event)
    );
  }

  private Void processEvent(int shard, OutboxEntity event) {
    if (!repository.tryMarkAsProcessing(event)) {
      log.info("[shard={}] skipping event {} — already claimed by another node", shard, event.getEventId());
      return null;
    }

    try {
      log.info("[shard={}] processing event {}", shard, event.getEventId());
      publisher.publish(event);
      repository.markAsPublished(event);
    }
    catch (Exception ex) {
      log.error("[shard={}] error processing event {}", shard, event.getEventId(), ex);
    }

    return null;
  }
}
