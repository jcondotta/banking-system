package com.jcondotta.banking.recipients.application.recipient.command.create;

import com.jcondotta.application.command.CommandHandlerWithResult;
import com.jcondotta.banking.recipients.application.common.log.LogKey;
import com.jcondotta.banking.recipients.application.common.log.LogOutcome;
import com.jcondotta.banking.recipients.application.common.log.RecipientEventType;
import com.jcondotta.banking.recipients.domain.common.FailureReason;
import com.jcondotta.banking.recipients.domain.recipient.aggregate.Recipient;
import com.jcondotta.banking.recipients.domain.recipient.exceptions.DuplicateRecipientIbanException;
import com.jcondotta.banking.recipients.domain.recipient.identity.RecipientId;
import com.jcondotta.banking.recipients.domain.recipient.repository.RecipientRepository;
import com.jcondotta.domain.exception.DomainException;
import io.micrometer.observation.annotation.Observed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

@Component
public class CreateRecipientCommandHandler implements CommandHandlerWithResult<CreateRecipientCommand, RecipientId> {

  private static final Logger log = LoggerFactory.getLogger(CreateRecipientCommandHandler.class);

  private final RecipientRepository recipientRepository;
  private final Clock clock;

  public CreateRecipientCommandHandler(RecipientRepository recipientRepository, Clock clock) {
    this.recipientRepository = recipientRepository;
    this.clock = clock;
  }

  @Override
  @Observed(
    name = "recipient.create",
    contextualName = "createRecipient",
    lowCardinalityKeyValues = {
      "aggregate", "recipient",
      "operation", "create"
    }
  )
  public RecipientId handle(CreateRecipientCommand command) {
    var startNs = System.nanoTime();

    try {
      var recipient = Recipient.create(
        RecipientId.newId(),
        command.bankAccountId(),
        command.recipientName(),
        command.iban(),
        Instant.now(clock)
      );

      recipientRepository.save(recipient);

      log.atInfo()
        .setMessage("Recipient created")
        .addKeyValue(LogKey.EVENT_TYPE, RecipientEventType.CREATE)
        .addKeyValue(LogKey.OUTCOME, LogOutcome.SUCCESS)
        .addKeyValue(LogKey.BANK_ACCOUNT_ID, command.bankAccountId().asString())
        .addKeyValue(LogKey.RECIPIENT_ID, recipient.getId().asString())
        .addKeyValue(LogKey.DURATION_MS, durationMs(startNs))
        .log();

      return recipient.getId();
    }
    catch (DomainException ex) {
      var failureReason = FailureReason.from(ex);

      log.atWarn()
        .setMessage("Recipient creation failed")
        .addKeyValue(LogKey.EVENT_TYPE, RecipientEventType.CREATE)
        .addKeyValue(LogKey.OUTCOME, LogOutcome.FAILURE)
        .addKeyValue(LogKey.REASON, failureReason.normalize())
        .addKeyValue(LogKey.BANK_ACCOUNT_ID, command.bankAccountId().asString())
        .addKeyValue(LogKey.DURATION_MS, durationMs(startNs))
        .addKeyValue(LogKey.MASKED_IBAN, command.iban().masked())
        .log();

      throw ex;
    }
    catch (Exception ex) {
      log.atError()
        .setMessage("Unexpected error during recipient creation")
        .addKeyValue(LogKey.EVENT_TYPE, RecipientEventType.CREATE)
        .addKeyValue(LogKey.OUTCOME, LogOutcome.FAILURE)
        .addKeyValue(LogKey.REASON, FailureReason.INTERNAL_ERROR.normalize())
        .addKeyValue(LogKey.BANK_ACCOUNT_ID, command.bankAccountId().asString())
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
