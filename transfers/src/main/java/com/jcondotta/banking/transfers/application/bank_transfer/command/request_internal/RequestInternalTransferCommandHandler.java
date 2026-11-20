package com.jcondotta.banking.transfers.application.bank_transfer.command.request_internal;

import com.jcondotta.application.command.CommandHandlerWithResult;
import com.jcondotta.scopedlogging.application.logscope.LogContext;
import com.jcondotta.scopedlogging.LogScope;
import com.jcondotta.scopedlogging.domain.logging.LogKey;
import com.jcondotta.banking.transfers.application.bank_account.ports.output.BankAccountLookupPort;
import com.jcondotta.banking.transfers.application.bank_transfer.command.request_internal.model.RequestInternalTransferCommand;
import com.jcondotta.banking.transfers.application.common.log.BankTransferFailureReason;
import com.jcondotta.banking.transfers.application.common.log.BankTransferOperation;
import com.jcondotta.banking.transfers.application.common.log.BankTransferLogKey;
import com.jcondotta.banking.transfers.domain.bank_account.BankAccountSummary;
import com.jcondotta.banking.transfers.domain.bank_account.exceptions.RecipientBankAccountNotActiveException;
import com.jcondotta.banking.transfers.domain.bank_account.exceptions.RecipientBankAccountNotFoundException;
import com.jcondotta.banking.transfers.domain.bank_account.exceptions.SenderBankAccountNotActiveException;
import com.jcondotta.banking.transfers.domain.bank_account.exceptions.SenderBankAccountNotFoundException;
import com.jcondotta.banking.transfers.domain.bank_transfer.aggregate.BankTransfer;
import com.jcondotta.banking.transfers.domain.bank_transfer.identity.BankTransferId;
import com.jcondotta.banking.transfers.domain.bank_transfer.repository.BankTransferRepository;
import com.jcondotta.domain.exception.DomainException;
import io.micrometer.context.ContextSnapshotFactory;
import io.micrometer.observation.annotation.Observed;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Instant;
import java.util.concurrent.StructuredTaskScope;

@Component
public class RequestInternalTransferCommandHandler implements CommandHandlerWithResult<RequestInternalTransferCommand, BankTransferId> {

  private static final ContextSnapshotFactory CONTEXT_SNAPSHOT_FACTORY = ContextSnapshotFactory.builder().build();

  private final LogContext logContext;

  private final BankTransferRepository bankTransferRepository;
  private final BankAccountLookupPort bankAccountLookupPort;
  private final Clock clock;

  public RequestInternalTransferCommandHandler(
    BankTransferRepository bankTransferRepository,
    BankAccountLookupPort bankAccountLookupPort,
    Clock clock,
    LogContext logContext
  ) {
    this.logContext = logContext;
    this.bankTransferRepository = bankTransferRepository;
    this.bankAccountLookupPort = bankAccountLookupPort;
    this.clock = clock;
  }

  @Override
  @LogScope(BankTransferOperation.REQUEST_INTERNAL)
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

    logContext.with(BankTransferLogKey.BANK_TRANSFER_ID, bankTransferId.asString())
      .with(BankTransferLogKey.SENDER_ACCOUNT_ID, command.senderAccountId().asString())
      .with(BankTransferLogKey.RECIPIENT_NAME, command.recipientName().value());

    try {
      var contextSnapshot = CONTEXT_SNAPSHOT_FACTORY.captureAll();
      BankAccountSummary recipientSummary;

      try (var scope = StructuredTaskScope.open()) {
        var senderLookup = scope.fork(contextSnapshot.wrap(() -> bankAccountLookupPort.findById(command.senderAccountId())));
        var recipientLookup = scope.fork(contextSnapshot.wrap(() -> bankAccountLookupPort.findByIban(command.recipientIban())));
        scope.join();

        var senderSummary = senderLookup.get()
          .orElseThrow(() -> new SenderBankAccountNotFoundException(command.senderAccountId()));

        if (!senderSummary.status().isActive()) {
          throw new SenderBankAccountNotActiveException(senderSummary.status());
        }

        recipientSummary = recipientLookup.get()
          .orElseThrow(() -> new RecipientBankAccountNotFoundException(command.recipientIban()));

        if (!recipientSummary.status().isActive()) {
          throw new RecipientBankAccountNotActiveException(recipientSummary.status());
        }
      }
      catch (InterruptedException ex) {
        Thread.currentThread().interrupt();
        throw new IllegalStateException("Interrupted while looking up bank accounts", ex);
      }
      catch (StructuredTaskScope.FailedException ex) {
        var failure = ex.getCause();
        if (failure instanceof RuntimeException runtimeException) {
          throw runtimeException;
        }
        if (failure instanceof Error error) {
          throw error;
        }
        throw new IllegalStateException("Bank account lookup failed", failure);
      }

      var recipientAccountId = recipientSummary.bankAccountId();
      logContext.with(BankTransferLogKey.RECIPIENT_ACCOUNT_ID, recipientAccountId.asString());

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
        .success();

      return bankTransfer.getId();
    }
    catch (DomainException ex) {
      var failureReason = BankTransferFailureReason.from(ex);

      logContext.warn("Internal transfer request failed")
        .with(LogKey.REASON, failureReason.normalize())
        .failure();

      throw ex;
    }
    catch (Exception ex) {
      logContext.error("Unexpected error during internal transfer request", ex)
        .with(LogKey.REASON, BankTransferFailureReason.INTERNAL_ERROR.normalize())
        .failure();

      throw ex;
    }
  }
}
