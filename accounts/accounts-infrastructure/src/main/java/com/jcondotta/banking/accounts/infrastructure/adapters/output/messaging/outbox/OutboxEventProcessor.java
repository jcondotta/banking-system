package com.jcondotta.banking.accounts.infrastructure.adapters.output.messaging.outbox;

import com.jcondotta.banking.accounts.infrastructure.adapters.output.messaging.outbox.query.OutboxRepository;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.entity.OutboxEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxEventProcessor {

  private final OutboxPublisher publisher;
  private final OutboxRepository repository;

  public void process(int shard, OutboxEntity event) {
    try {
      log.info("[shard={}] processing event {}", shard, event.getEventId());

      publisher.publish(event);
      repository.markAsPublished(event);
    }
    catch (Exception ex) {
      log.error("[shard={}] error processing event {}", shard, event.getEventId(), ex);
    }
  }
}