package com.jcondotta.banking.transfers.application.bank_transfer.command.request_internal;

import com.jcondotta.application.command.CommandHandlerWithResult;
import com.jcondotta.application.logging.LogContext;
import com.jcondotta.application.logging.LogKey;
import com.jcondotta.banking.transfers.application.bank_account.ports.output.BankAccountLookupPort;
import com.jcondotta.banking.transfers.application.bank_transfer.command.request_internal.model.RequestInternalTransferCommand;
import com.jcondotta.banking.transfers.application.common.log.BankTransferEventType;
import com.jcondotta.banking.transfers.application.common.log.BankTransferLogKey;
import com.jcondotta.banking.transfers.domain.bank_account.exceptions.RecipientBankAccountNotFoundException;
import com.jcondotta.banking.transfers.domain.bank_transfer.aggregate.BankTransfer;
import com.jcondotta.banking.transfers.domain.bank_transfer.identity.BankTransferId;
import com.jcondotta.banking.transfers.domain.bank_transfer.repository.BankTransferRepository;
import com.jcondotta.banking.transfers.domain.common.FailureReason;
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

    var logContext = LogContext.timed(LOGGER, BankTransferEventType.REQUEST_INTERNAL)
      .with(BankTransferLogKey.BANK_TRANSFER_ID, bankTransferId.value().toString())
      .with(BankTransferLogKey.SENDER_ACCOUNT_ID, command.senderAccountId().value().toString())
      .with(BankTransferLogKey.RECIPIENT_NAME, command.recipientName().value())
      .with(BankTransferLogKey.MASKED_IBAN, command.recipientIban().masked());

    try {
      var recipientAccountId = bankAccountLookupPort.findByIban(command.recipientIban())
        .orElseThrow(() -> new RecipientBankAccountNotFoundException(command.recipientIban()));

      logContext = logContext.with(BankTransferLogKey.RECIPIENT_ACCOUNT_ID, recipientAccountId.value().toString());

      var bankTransfer = BankTransfer.requestInternalTransfer(
        bankTransferId,
        command.senderAccountId(),
        recipientAccountId,
        command.monetaryAmount(),
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
      var failureReason = FailureReason.from(ex);

      logContext.warn("Internal transfer request failed")
        .failure()
        .with(LogKey.REASON, failureReason.normalize())
        .log();

      throw ex;
    }
    catch (Exception ex) {
      logContext.error("Unexpected error during internal transfer request", ex)
        .failure()
        .with(LogKey.REASON, FailureReason.INTERNAL_ERROR.normalize())
        .log();

      throw ex;
    }
  }
}
