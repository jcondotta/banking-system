package com.jcondotta.banking.recipients.application.recipient.command.update;

import com.jcondotta.application.command.CommandHandler;
import com.jcondotta.application.logging.LogContext;
import com.jcondotta.application.logging.LogKey;
import com.jcondotta.banking.recipients.application.common.log.RecipientEventType;
import com.jcondotta.banking.recipients.application.common.log.RecipientLogKey;
import com.jcondotta.banking.recipients.application.common.log.RecipientFailureReason;
import com.jcondotta.banking.recipients.domain.recipient.exceptions.RecipientNotFoundException;
import com.jcondotta.banking.recipients.domain.recipient.repository.RecipientRepository;
import com.jcondotta.domain.exception.DomainException;
import io.micrometer.observation.annotation.Observed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.resilience.annotation.ConcurrencyLimit;
import org.springframework.stereotype.Component;

@Component
public class UpdateRecipientCommandHandler implements CommandHandler<UpdateRecipientCommand> {

  private static final Logger LOGGER = LoggerFactory.getLogger(UpdateRecipientCommandHandler.class);

  private final RecipientRepository recipientRepository;

  public UpdateRecipientCommandHandler(RecipientRepository recipientRepository) {
    this.recipientRepository = recipientRepository;
  }

  @Override
  @Observed(
      name = "recipients.update",
      contextualName = "updateRecipient",
      lowCardinalityKeyValues = {
          "aggregate", "recipient",
          "operation", "update"
      }
  )
  @ConcurrencyLimit(limitString = "${app.concurrency.recipients.update.limit}", policy = ConcurrencyLimit.ThrottlePolicy.REJECT)
  public void handle(UpdateRecipientCommand command) {
    var logContext = LogContext.timed(LOGGER, RecipientEventType.UPDATE)
      .with(RecipientLogKey.BANK_ACCOUNT_ID, command.bankAccountId().asString())
      .with(RecipientLogKey.RECIPIENT_ID, command.recipientId().asString());

    try {
      var recipient = recipientRepository.findByBankAccountIdAndId(command.bankAccountId(), command.recipientId())
        .orElseThrow(() -> new RecipientNotFoundException(command.recipientId(), command.bankAccountId()));

      recipient.update(command.recipientName(), command.iban());
      recipientRepository.save(recipient);

      logContext.info("Recipient updated")
        .success()
        .log();
    }
    catch (DomainException ex) {
      var reason = RecipientFailureReason.from(ex);

      logContext.warn("Recipient update failed")
        .failure()
        .with(LogKey.REASON, reason.normalize())
        .with(RecipientLogKey.MASKED_IBAN, command.iban().masked())
        .log();

      throw ex;
    }
    catch (Exception ex) {
      logContext.error("Unexpected error during recipient update", ex)
        .failure()
        .with(LogKey.REASON, RecipientFailureReason.INTERNAL_ERROR.normalize())
        .with(RecipientLogKey.MASKED_IBAN, command.iban().masked())
        .log();

      throw ex;
    }
  }
}
