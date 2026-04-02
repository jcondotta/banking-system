package com.jcondotta.banking.accounts.infrastructure.adapters.output.outbox.processor;

import com.jcondotta.banking.accounts.infrastructure.adapters.output.outbox.store.OutboxEventStore;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.outbox.entity.OutboxEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxEventClaimer {

  private final OutboxEventStore repository;
  private final OutboxEventProcessor eventProcessor;

  public void claim(int shard, OutboxEntity event) {
    if (!repository.tryClaimEvent(event)) {
      log.info("[shard={}] skipping event {} — already claimed by another node", shard, event.getEventId());
      return;
    }

    eventProcessor.process(shard, event);
  }
}