package com.jcondotta.banking.recipients.application.recipient.command.update;

import com.jcondotta.application.command.CommandHandler;
import com.jcondotta.scopedlogging.application.logscope.LogContext;
import com.jcondotta.scopedlogging.LogScope;
import com.jcondotta.scopedlogging.domain.logging.LogKey;
import com.jcondotta.banking.recipients.application.common.log.RecipientOperation;
import com.jcondotta.banking.recipients.application.common.log.RecipientLogKey;
import com.jcondotta.banking.recipients.application.common.log.RecipientFailureReason;
import com.jcondotta.banking.recipients.domain.recipient.exceptions.RecipientNotFoundException;
import com.jcondotta.banking.recipients.domain.recipient.repository.RecipientRepository;
import com.jcondotta.domain.exception.DomainException;
import io.micrometer.observation.annotation.Observed;
import org.springframework.resilience.annotation.ConcurrencyLimit;
import org.springframework.stereotype.Component;

@Component
public class UpdateRecipientCommandHandler implements CommandHandler<UpdateRecipientCommand> {

  private final LogContext logContext;

  private final RecipientRepository recipientRepository;

  public UpdateRecipientCommandHandler(RecipientRepository recipientRepository, LogContext logContext) {
    this.logContext = logContext;
    this.recipientRepository = recipientRepository;
  }

  @Override
  @LogScope(RecipientOperation.UPDATE)
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
    logContext.with(RecipientLogKey.BANK_ACCOUNT_ID, command.bankAccountId().asString())
      .with(RecipientLogKey.RECIPIENT_ID, command.recipientId().asString());

    try {
      var recipient = recipientRepository.findByBankAccountIdAndId(command.bankAccountId(), command.recipientId())
        .orElseThrow(() -> new RecipientNotFoundException(command.recipientId(), command.bankAccountId()));

      recipient.update(command.recipientName(), command.iban());
      recipientRepository.save(recipient);

      logContext.info("Recipient updated")
        .success();
    }
    catch (DomainException ex) {
      var reason = RecipientFailureReason.from(ex);

      logContext.warn("Recipient update failed")
        .with(LogKey.REASON, reason.normalize())
        .with(RecipientLogKey.MASKED_IBAN, command.iban().masked())
        .failure();

      throw ex;
    }
    catch (Exception ex) {
      logContext.error("Unexpected error during recipient update", ex)
        .with(LogKey.REASON, RecipientFailureReason.INTERNAL_ERROR.normalize())
        .with(RecipientLogKey.MASKED_IBAN, command.iban().masked())
        .failure();

      throw ex;
    }
  }
}
