package com.jcondotta.banking.recipients.application.recipient.command.create;

import com.jcondotta.application.command.CommandHandlerWithResult;
import com.jcondotta.scopedlogging.application.logscope.LogContext;
import com.jcondotta.scopedlogging.LogScope;
import com.jcondotta.scopedlogging.domain.logging.LogKey;
import com.jcondotta.banking.recipients.application.common.log.RecipientLogKey;
import com.jcondotta.banking.recipients.application.common.log.RecipientOperation;
import com.jcondotta.banking.recipients.application.common.log.RecipientFailureReason;
import com.jcondotta.banking.recipients.domain.bank_account.exceptions.BankAccountNotActiveException;
import com.jcondotta.banking.recipients.domain.bank_account.exceptions.BankAccountNotFoundException;
import com.jcondotta.banking.recipients.domain.bank_account.repository.BankAccountRepository;
import com.jcondotta.banking.recipients.domain.recipient.aggregate.Recipient;
import com.jcondotta.banking.recipients.domain.recipient.identity.RecipientId;
import com.jcondotta.banking.recipients.domain.recipient.repository.RecipientRepository;
import com.jcondotta.banking.recipients.application.recipient.ports.output.RecipientEventPublisher;
import com.jcondotta.domain.exception.DomainException;
import io.micrometer.observation.annotation.Observed;
import org.springframework.resilience.annotation.ConcurrencyLimit;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Instant;

@Component
public class CreateRecipientCommandHandler implements CommandHandlerWithResult<CreateRecipientCommand, RecipientId> {

  private final LogContext logContext;

  private final RecipientRepository recipientRepository;
  private final RecipientEventPublisher recipientEventPublisher;
  private final BankAccountRepository bankAccountRepository;
  private final Clock clock;

  public CreateRecipientCommandHandler(
    RecipientRepository recipientRepository,
    RecipientEventPublisher recipientEventPublisher,
    BankAccountRepository bankAccountRepository,
    Clock clock,
    LogContext logContext
  ) {
    this.logContext = logContext;
    this.recipientRepository = recipientRepository;
    this.recipientEventPublisher = recipientEventPublisher;
    this.bankAccountRepository = bankAccountRepository;
    this.clock = clock;
  }

  @Override
  @LogScope(RecipientOperation.CREATE)
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
    logContext.with(RecipientLogKey.BANK_ACCOUNT_ID, command.bankAccountId().asString());

    try {
      var bankAccount = bankAccountRepository.findById(command.bankAccountId())
        .orElseThrow(() -> new BankAccountNotFoundException(command.bankAccountId()));

      if (!bankAccount.isActive()) {
        throw new BankAccountNotActiveException(bankAccount.status());
      }

      var recipient = Recipient.create(
        RecipientId.newId(),
        command.bankAccountId(),
        command.recipientName(),
        command.iban(),
        Instant.now(clock)
      );

      recipientRepository.save(recipient);
      recipientEventPublisher.publish(recipient.pullEvents());

      logContext.info("Recipient created")
        .with(RecipientLogKey.RECIPIENT_ID, recipient.getId().asString())
        .success();

      return recipient.getId();
    }
    catch (DomainException ex) {
      var failureReason = RecipientFailureReason.from(ex);

      logContext.warn("Recipient creation failed")
        .with(LogKey.REASON, failureReason.normalize())
        .with(RecipientLogKey.MASKED_IBAN, command.iban().masked())
        .failure();

      throw ex;
    }
    catch (Exception ex) {
      logContext.error("Unexpected error during recipient creation", ex)
        .with(LogKey.REASON, RecipientFailureReason.INTERNAL_ERROR.normalize())
        .with(RecipientLogKey.MASKED_IBAN, command.iban().masked())
        .failure();

      throw ex;
    }
  }
}
