package com.jcondotta.banking.infrastructure.outbox.processor;

import com.jcondotta.application.command.CommandHandler;
import com.jcondotta.application.logging.LogContext;
import com.jcondotta.application.logging.LogKey;
import com.jcondotta.banking.infrastructure.adapters.output.messaging.outbox.OutboxEventPublisher;
import com.jcondotta.banking.infrastructure.outbox.exceptions.OutboxEventAlreadyProcessedException;
import com.jcondotta.banking.infrastructure.outbox.log.OutboxOperation;
import com.jcondotta.banking.infrastructure.outbox.log.OutboxFailureReason;
import com.jcondotta.banking.infrastructure.outbox.log.OutboxLogKey;
import com.jcondotta.banking.infrastructure.outbox.record.OutboxRecord;
import com.jcondotta.banking.infrastructure.outbox.store.OutboxEventStore;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RequiredArgsConstructor
public class OutboxEventCompleter<T extends OutboxRecord> implements CommandHandler<T> {

  private static final Logger LOGGER = LoggerFactory.getLogger(OutboxEventCompleter.class);

  private final OutboxEventPublisher<T> eventSender;
  private final OutboxEventStore<T> eventStore;

  @Override
  public void handle(T event) {
    var logContext = logContext(event);

    try {
      eventSender.send(event);
    }
    catch (Exception ex) {
      logContext.error("Outbox event publishing failed", ex)
        .failure()
        .with(LogKey.REASON, OutboxFailureReason.PUBLISH_FAILED.normalize())
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
        .with(LogKey.REASON, OutboxFailureReason.ALREADY_PROCESSED.normalize())
        .log();

      throw ex;
    }
    catch (Exception ex) {
      logContext.error("Outbox event cleanup failed", ex)
        .failure()
        .with(LogKey.REASON, OutboxFailureReason.DELETE_FAILED.normalize())
        .log();

      throw ex;
    }
  }

  private LogContext logContext(T event) {
    return LogContext.timed(LOGGER, OutboxOperation.PUBLISH)
      .with(OutboxLogKey.SHARD, event.getShard())
      .with(OutboxLogKey.EVENT_ID, event.getEventId())
      .with(LogKey.CORRELATION_ID, event.getCorrelationId())
      .with(OutboxLogKey.AGGREGATE_ID, event.getAggregateId())
      .with(LogKey.EVENT_TYPE, event.getEventType())
      .with(OutboxLogKey.ATTEMPT_COUNT, event.getAttemptCount());
  }
}
