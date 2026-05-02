package com.jcondotta.banking.recipients.application.recipient.command.create;

import com.jcondotta.application.command.CommandHandlerWithResult;
import com.jcondotta.banking.recipients.application.common.log.LogContext;
import com.jcondotta.banking.recipients.application.common.log.LogKey;
import com.jcondotta.banking.recipients.application.common.log.RecipientEventType;
import com.jcondotta.banking.recipients.domain.common.FailureReason;
import com.jcondotta.banking.recipients.domain.recipient.aggregate.Recipient;
import com.jcondotta.banking.recipients.domain.recipient.identity.RecipientId;
import com.jcondotta.banking.recipients.domain.recipient.repository.RecipientRepository;
import com.jcondotta.domain.exception.DomainException;
import io.micrometer.observation.annotation.Observed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.resilience.annotation.ConcurrencyLimit;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Instant;

@Component
public class CreateRecipientCommandHandler implements CommandHandlerWithResult<CreateRecipientCommand, RecipientId> {

  private static final Logger LOGGER = LoggerFactory.getLogger(CreateRecipientCommandHandler.class);

  private final RecipientRepository recipientRepository;
  private final Clock clock;

  public CreateRecipientCommandHandler(RecipientRepository recipientRepository, Clock clock) {
    this.recipientRepository = recipientRepository;
    this.clock = clock;
  }

  @Override
  @Observed(
      name = "recipients.create",
      contextualName = "createRecipient",
      lowCardinalityKeyValues = {
          "aggregate", "recipient",
          "operation", "create"
      }
  )
  @ConcurrencyLimit(limitString = "${app.concurrency.recipients.create.limit}", policy = ConcurrencyLimit.ThrottlePolicy.REJECT)
  public RecipientId handle(CreateRecipientCommand command) {
    var logContext = LogContext.timed(LOGGER, RecipientEventType.CREATE)
      .with(LogKey.BANK_ACCOUNT_ID, command.bankAccountId().asString());

    try {
      var recipient = Recipient.create(
        RecipientId.newId(),
        command.bankAccountId(),
        command.recipientName(),
        command.iban(),
        Instant.now(clock)
      );

      recipientRepository.save(recipient);

      logContext.info("Recipient created")
        .success()
        .with(LogKey.RECIPIENT_ID, recipient.getId().asString())
        .log();

      return recipient.getId();
    }
    catch (DomainException ex) {
      var failureReason = FailureReason.from(ex);

      logContext.warn("Recipient creation failed")
        .failure()
        .with(LogKey.REASON, failureReason.normalize())
        .with(LogKey.MASKED_IBAN, command.iban().masked())
        .log();

      throw ex;
    }
    catch (Exception ex) {
      logContext.error("Unexpected error during recipient creation", ex)
        .failure()
        .with(LogKey.REASON, FailureReason.INTERNAL_ERROR.normalize())
        .with(LogKey.MASKED_IBAN, command.iban().masked())
        .log();

      throw ex;
    }
  }
}
