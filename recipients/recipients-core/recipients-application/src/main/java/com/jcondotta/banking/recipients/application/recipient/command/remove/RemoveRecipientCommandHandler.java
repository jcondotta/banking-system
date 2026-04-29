package com.jcondotta.banking.recipients.application.recipient.command.remove;

import com.jcondotta.application.command.CommandHandler;
import com.jcondotta.banking.recipients.application.common.log.LogKey;
import com.jcondotta.banking.recipients.application.common.log.LogOutcome;
import com.jcondotta.banking.recipients.application.common.log.RecipientEventType;
import com.jcondotta.banking.recipients.domain.common.FailureReason;
import com.jcondotta.banking.recipients.domain.recipient.exceptions.RecipientNotFoundException;
import com.jcondotta.banking.recipients.domain.recipient.repository.RecipientRepository;
import com.jcondotta.domain.exception.DomainException;
import io.micrometer.observation.annotation.Observed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RemoveRecipientCommandHandler implements CommandHandler<RemoveRecipientCommand> {

  private static final Logger log = LoggerFactory.getLogger(RemoveRecipientCommandHandler.class);

  private final RecipientRepository recipientRepository;

  public RemoveRecipientCommandHandler(RecipientRepository recipientRepository) {
    this.recipientRepository = recipientRepository;
  }

  @Override
  @Observed(
    name = "recipient.remove",
    contextualName = "removeRecipient",
    lowCardinalityKeyValues = {
      "aggregate", "recipient",
      "operation", "remove"
    }
  )
  public void handle(RemoveRecipientCommand command) {
    var startNs = System.nanoTime();

    try {
      var recipient = recipientRepository.findById(command.recipientId())
        .orElseThrow(() -> new RecipientNotFoundException(command.recipientId(), command.bankAccountId()));

      recipient.assertBelongsTo(command.bankAccountId());

      recipientRepository.delete(recipient);

      log.atInfo()
        .setMessage("Recipient removed successfully")
        .addKeyValue(LogKey.EVENT_TYPE, RecipientEventType.REMOVE)
        .addKeyValue(LogKey.OUTCOME, LogOutcome.SUCCESS)
        .addKeyValue(LogKey.BANK_ACCOUNT_ID, command.bankAccountId().asString())
        .addKeyValue(LogKey.RECIPIENT_ID, command.recipientId().asString())
        .addKeyValue(LogKey.DURATION_MS, durationMs(startNs))
        .log();
    }
    catch (DomainException ex) {
      var reason = FailureReason.from(ex);
      log.atWarn()
        .setMessage("Recipient removal failed")
        .addKeyValue(LogKey.EVENT_TYPE, RecipientEventType.REMOVE)
        .addKeyValue(LogKey.OUTCOME, LogOutcome.FAILURE)
        .addKeyValue(LogKey.REASON, reason.normalize())
        .addKeyValue(LogKey.BANK_ACCOUNT_ID, command.bankAccountId().asString())
        .addKeyValue(LogKey.RECIPIENT_ID, command.recipientId().asString())
        .addKeyValue(LogKey.DURATION_MS, durationMs(startNs))
        .log();

      throw ex;
    }
    catch (Exception ex) {
      log.atError()
        .setMessage("Unexpected error during recipient removal")
        .addKeyValue(LogKey.EVENT_TYPE, RecipientEventType.REMOVE)
        .addKeyValue(LogKey.OUTCOME, LogOutcome.FAILURE)
        .addKeyValue(LogKey.REASON, FailureReason.INTERNAL_ERROR.normalize())
        .addKeyValue(LogKey.BANK_ACCOUNT_ID, command.bankAccountId().asString())
        .addKeyValue(LogKey.RECIPIENT_ID, command.recipientId().asString())
        .addKeyValue(LogKey.DURATION_MS, durationMs(startNs))
        .setCause(ex)
        .log();

      throw ex;
    }
  }

  private static long durationMs(long startNs) {
    return TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);
  }
}
