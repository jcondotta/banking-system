package com.jcondotta.banking.recipients.application.recipient.command.remove;

import com.jcondotta.application.command.CommandHandler;
import com.jcondotta.application.logging.LogContext;
import com.jcondotta.application.logging.LogKey;
import com.jcondotta.banking.recipients.application.common.log.RecipientLogKey;
import com.jcondotta.banking.recipients.application.common.log.RecipientEventType;
import com.jcondotta.banking.recipients.domain.common.FailureReason;
import com.jcondotta.banking.recipients.domain.recipient.exceptions.RecipientNotFoundException;
import com.jcondotta.banking.recipients.domain.recipient.repository.RecipientRepository;
import com.jcondotta.domain.exception.DomainException;
import io.micrometer.observation.annotation.Observed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.resilience.annotation.ConcurrencyLimit;
import org.springframework.stereotype.Component;

@Component
public class RemoveRecipientCommandHandler implements CommandHandler<RemoveRecipientCommand> {

  private static final Logger LOGGER = LoggerFactory.getLogger(RemoveRecipientCommandHandler.class);

  private final RecipientRepository recipientRepository;

  public RemoveRecipientCommandHandler(RecipientRepository recipientRepository) {
    this.recipientRepository = recipientRepository;
  }

  @Override
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
    var logContext = LogContext.timed(LOGGER, RecipientEventType.REMOVE)
      .with(RecipientLogKey.BANK_ACCOUNT_ID, command.bankAccountId().asString())
      .with(RecipientLogKey.RECIPIENT_ID, command.recipientId().asString());

    try {
      var recipient = recipientRepository.findById(command.recipientId())
        .orElseThrow(() -> new RecipientNotFoundException(command.recipientId(), command.bankAccountId()));

      recipient.assertBelongsTo(command.bankAccountId());

      recipientRepository.delete(recipient);

      logContext.info("Recipient removed")
        .success()
        .log();
    }
    catch (DomainException ex) {
      var reason = FailureReason.from(ex);

      logContext.warn("Recipient removal failed")
        .failure()
        .with(LogKey.REASON, reason.normalize())
        .log();

      throw ex;
    }
    catch (Exception ex) {
      logContext.error("Unexpected error during recipient removal", ex)
        .failure()
        .with(LogKey.REASON, FailureReason.INTERNAL_ERROR.normalize())
        .log();

      throw ex;
    }
  }
}
