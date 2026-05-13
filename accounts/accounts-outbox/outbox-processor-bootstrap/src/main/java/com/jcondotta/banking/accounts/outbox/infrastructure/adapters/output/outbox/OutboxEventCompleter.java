package com.jcondotta.banking.accounts.outbox.infrastructure.adapters.output.outbox;

import com.jcondotta.application.command.CommandHandler;
import com.jcondotta.application.logging.LogContext;
import com.jcondotta.application.logging.LogKey;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.outbox.entity.OutboxEntity;
import com.jcondotta.banking.accounts.outbox.infrastructure.adapters.output.outbox.log.OutboxEventType;
import com.jcondotta.banking.accounts.outbox.infrastructure.adapters.output.outbox.log.OutboxLogKey;
import com.jcondotta.banking.accounts.outbox.infrastructure.adapters.output.outbox.publisher.OutboxEventPublisher;
import com.jcondotta.banking.accounts.outbox.infrastructure.adapters.output.outbox.store.OutboxEventStore;
import com.jcondotta.banking.accounts.outbox.infrastructure.adapters.output.outbox.store.exceptions.OutboxEventAlreadyProcessedException;
import io.micrometer.observation.annotation.Observed;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OutboxEventCompleter implements CommandHandler<OutboxEntity> {

  private static final Logger LOGGER = LoggerFactory.getLogger(OutboxEventCompleter.class);

  private final OutboxEventPublisher eventSender;
  private final OutboxEventStore eventStore;

  @Override
  @Observed(
    name = "accounts.outbox.publish",
    contextualName = "publishOutboxEvent",
    lowCardinalityKeyValues = {
      "component", "outbox",
      "operation", "publish"
    }
  )
  public void handle(OutboxEntity event) {
    var logContext = logContext(event);

    try {
      eventSender.send(event);
    }
    catch (Exception ex) {
      logContext.error("Outbox event publishing failed", ex)
        .failure()
        .with(LogKey.REASON, "publish_failed")
        .log();

      throw ex;
    }

    try {
      eventStore.deletePublishedEvent(event);

      logContext.info("Outbox event published")
        .success()
        .log();
    }
    catch (OutboxEventAlreadyProcessedException ex) {
      logContext.warn("Outbox event cleanup skipped")
        .failure()
        .with(LogKey.REASON, "already_processed")
        .log();

      throw ex;
    }
    catch (Exception ex) {
      logContext.error("Outbox event cleanup failed", ex)
        .failure()
        .with(LogKey.REASON, "delete_failed")
        .log();

      throw ex;
    }
  }

  private LogContext logContext(OutboxEntity event) {
    return LogContext.timed(LOGGER, OutboxEventType.PUBLISH)
      .with(OutboxLogKey.SHARD, event.getShard())
      .with(OutboxLogKey.EVENT_ID, event.getEventId())
      .with(OutboxLogKey.AGGREGATE_ID, event.getAggregateId())
      .with(OutboxLogKey.OUTBOX_EVENT_TYPE, event.getEventType())
      .with(OutboxLogKey.RETRY_COUNT, event.getRetryCount());
  }
}
