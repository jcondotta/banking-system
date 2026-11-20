package com.jcondotta.banking.recipients.application.recipient.command.remove;

import com.jcondotta.application.command.CommandHandler;
import com.jcondotta.scopedlogging.application.logscope.LogContext;
import com.jcondotta.scopedlogging.LogScope;
import com.jcondotta.scopedlogging.domain.logging.LogKey;
import com.jcondotta.banking.recipients.application.common.log.RecipientOperation;
import com.jcondotta.banking.recipients.application.common.log.RecipientFailureReason;
import com.jcondotta.banking.recipients.application.common.log.RecipientLogKey;
import com.jcondotta.banking.recipients.application.recipient.ports.output.RecipientEventPublisher;
import com.jcondotta.banking.recipients.domain.recipient.exceptions.RecipientNotFoundException;
import com.jcondotta.banking.recipients.domain.recipient.repository.RecipientRepository;
import com.jcondotta.domain.exception.DomainException;
import io.micrometer.observation.annotation.Observed;
import org.springframework.resilience.annotation.ConcurrencyLimit;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Instant;

@Component
public class RemoveRecipientCommandHandler implements CommandHandler<RemoveRecipientCommand> {

  private final LogContext logContext;

  private final RecipientRepository recipientRepository;
  private final RecipientEventPublisher recipientEventPublisher;
  private final Clock clock;

  public RemoveRecipientCommandHandler(
    RecipientRepository recipientRepository,
    RecipientEventPublisher recipientEventPublisher,
    Clock clock,
    LogContext logContext
  ) {
    this.logContext = logContext;
    this.recipientRepository = recipientRepository;
    this.recipientEventPublisher = recipientEventPublisher;
    this.clock = clock;
  }

  @Override
  @LogScope(RecipientOperation.REMOVE)
  @Observed(
      name = "recipients.remove",
      contextualName = "removeRecipient",
      lowCardinalityKeyValues = {
          "aggregate", "recipient",
          "operation", "remove"
      }
  )
  @ConcurrencyLimit(limitString = "${app.concurrency.recipients.remove.limit}", policy = ConcurrencyLimit.ThrottlePolicy.REJECT)
  public void handle(RemoveRecipientCommand command) {
    logContext.with(RecipientLogKey.BANK_ACCOUNT_ID, command.bankAccountId().asString())
      .with(RecipientLogKey.RECIPIENT_ID, command.recipientId().asString());

    try {
      var recipient = recipientRepository.findByBankAccountIdAndId(command.bankAccountId(), command.recipientId())
        .orElseThrow(() -> new RecipientNotFoundException(command.recipientId(), command.bankAccountId()));

      recipient.delete(Instant.now(clock));
      recipientRepository.delete(recipient);
      recipientEventPublisher.publish(recipient.pullEvents());

      logContext.info("Recipient removed")
        .success();
    }
    catch (DomainException ex) {
      var reason = RecipientFailureReason.from(ex);

      logContext.warn("Recipient removal failed")
        .with(LogKey.REASON, reason.normalize())
        .failure();

      throw ex;
    }
    catch (Exception ex) {
      logContext.error("Unexpected error during recipient removal", ex)
        .with(LogKey.REASON, RecipientFailureReason.INTERNAL_ERROR.normalize())
        .failure();

      throw ex;
    }
  }
}
