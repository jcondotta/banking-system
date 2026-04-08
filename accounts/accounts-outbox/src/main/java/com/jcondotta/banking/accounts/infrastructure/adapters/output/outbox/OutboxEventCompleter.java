package com.jcondotta.banking.accounts.infrastructure.adapters.output.outbox;

import com.jcondotta.application.command.CommandHandler;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.outbox.entity.OutboxEntity;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.outbox.metrics.OutboxMetrics;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.outbox.publisher.OutboxEventPublisher;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.outbox.store.OutboxEventStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxEventCompleter implements CommandHandler<OutboxEntity> {

  private final OutboxEventPublisher eventSender;
  private final OutboxEventStore eventStore;
  private final OutboxMetrics outboxMetrics;

  @Override
  public void handle(OutboxEntity event) {
    var publishSample = outboxMetrics.startTimer();
    try {
      log.info("[shard={}] publishing event {}", event.getShard(), event.getEventId());
      eventSender.send(event);
      outboxMetrics.incrementPublishSuccess(event.getShard());
    }
    catch (Exception ex) {
      outboxMetrics.incrementPublishFailure(event.getShard());
      log.error("[shard={}] error publishing event {}", event.getShard(), event.getEventId(), ex);
      throw ex;
    }
    finally {
      outboxMetrics.stopPublishTimer(publishSample, event.getShard());
    }

    try {
      eventStore.deletePublishedEvent(event);
    }
    catch (Exception ex) {
      outboxMetrics.incrementDeleteFailure(event.getShard());
      log.error("[shard={}] event published but outbox delete failed for {}", event.getShard(), event.getEventId(), ex);
      throw ex;
    }
  }
}
