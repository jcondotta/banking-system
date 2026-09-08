package com.jcondotta.banking.transfers.application.bank_transfer.command.request_internal;

import com.jcondotta.application.command.CommandHandlerWithResult;
import com.jcondotta.application.logging.LogContext;
import com.jcondotta.application.logging.LogKey;
import com.jcondotta.banking.transfers.application.bank_account.ports.output.BankAccountLookupPort;
import com.jcondotta.banking.transfers.application.bank_transfer.command.request_internal.model.RequestInternalTransferCommand;
import com.jcondotta.banking.transfers.application.common.log.BankTransferFailureReason;
import com.jcondotta.banking.transfers.application.common.log.BankTransferOperation;
import com.jcondotta.banking.transfers.application.common.log.BankTransferLogKey;
import com.jcondotta.banking.transfers.domain.bank_account.exceptions.RecipientBankAccountNotActiveException;
import com.jcondotta.banking.transfers.domain.bank_account.exceptions.RecipientBankAccountNotFoundException;
import com.jcondotta.banking.transfers.domain.bank_transfer.aggregate.BankTransfer;
import com.jcondotta.banking.transfers.domain.bank_transfer.identity.BankTransferId;
import com.jcondotta.banking.transfers.domain.bank_transfer.repository.BankTransferRepository;
import com.jcondotta.domain.exception.DomainException;
import io.micrometer.observation.annotation.Observed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Instant;

@Component
public class RequestInternalTransferCommandHandler implements CommandHandlerWithResult<RequestInternalTransferCommand, BankTransferId> {

  private static final Logger LOGGER = LoggerFactory.getLogger(RequestInternalTransferCommandHandler.class);

  private final BankTransferRepository bankTransferRepository;
  private final BankAccountLookupPort bankAccountLookupPort;
  private final Clock clock;

  public RequestInternalTransferCommandHandler(
    BankTransferRepository bankTransferRepository,
    BankAccountLookupPort bankAccountLookupPort,
    Clock clock
  ) {
    this.bankTransferRepository = bankTransferRepository;
    this.bankAccountLookupPort = bankAccountLookupPort;
    this.clock = clock;
  }

  @Override
  @Observed(
    name = "transfers.request.internal",
    contextualName = "requestInternalTransfer",
    lowCardinalityKeyValues = {
      "aggregate", "bankTransfer",
      "operation", "requestInternal"
    }
  )
  public BankTransferId handle(RequestInternalTransferCommand command) {
    var bankTransferId = BankTransferId.newId();

    var logContext = LogContext.timed(LOGGER, BankTransferOperation.REQUEST_INTERNAL)
      .with(BankTransferLogKey.BANK_TRANSFER_ID, bankTransferId.asString())
      .with(BankTransferLogKey.SENDER_ACCOUNT_ID, command.senderAccountId().asString())
      .with(BankTransferLogKey.RECIPIENT_NAME, command.recipientName().value());

    try {
      var recipientSummary = bankAccountLookupPort.findByIban(command.recipientIban())
        .orElseThrow(() -> new RecipientBankAccountNotFoundException(command.recipientIban()));

      if (!recipientSummary.status().isActive()) {
        throw new RecipientBankAccountNotActiveException(recipientSummary.status());
      }

      var recipientAccountId = recipientSummary.bankAccountId();
      logContext = logContext.with(BankTransferLogKey.RECIPIENT_ACCOUNT_ID, recipientAccountId.asString());

      var bankTransfer = BankTransfer.requestInternalTransfer(
        bankTransferId,
        command.senderAccountId(),
        recipientAccountId,
        command.movementAmount(),
        command.reference(),
        Instant.now(clock)
      );

      bankTransferRepository.save(bankTransfer);

      logContext.info("Internal transfer requested")
        .success()
        .log();

      return bankTransfer.getId();
    }
    catch (DomainException ex) {
      var failureReason = BankTransferFailureReason.from(ex);

      logContext.warn("Internal transfer request failed")
        .failure()
        .with(LogKey.REASON, failureReason.normalize())
        .log();

      throw ex;
    }
    catch (Exception ex) {
      logContext.error("Unexpected error during internal transfer request", ex)
        .failure()
        .with(LogKey.REASON, BankTransferFailureReason.INTERNAL_ERROR.normalize())
        .log();

      throw ex;
    }
  }
}
