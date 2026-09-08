package com.jcondotta.banking.transfers.ledger.application.ledger_account.command.apply_transfer;

import com.jcondotta.application.command.CommandHandler;
import com.jcondotta.application.logging.LogContext;
import com.jcondotta.application.logging.LogKey;
import com.jcondotta.banking.transfers.domain.movement.Movement;
import com.jcondotta.banking.transfers.domain.bank_transfer.identity.BankTransferId;
import com.jcondotta.banking.transfers.domain.bank_transfer.repository.BankTransferRepository;
import com.jcondotta.banking.transfers.ledger.application.common.log.LedgerFailureReason;
import com.jcondotta.banking.transfers.ledger.application.common.log.LedgerOperation;
import com.jcondotta.banking.transfers.ledger.application.common.log.LedgerLogKey;
import com.jcondotta.banking.transfers.ledger.domain.ledger_account.exceptions.LedgerAccountNotYetProvisionedException;
import com.jcondotta.banking.transfers.ledger.domain.ledger_account.repository.LedgerAccountRepository;
import com.jcondotta.banking.transfers.ledger.domain.ledger_account.value_objects.AccountReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

// @Component
public class ApplyInternalTransferCommandHandler implements CommandHandler<ApplyInternalTransferCommand> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplyInternalTransferCommandHandler.class);

    private final LedgerAccountRepository ledgerAccountRepository;
    private final BankTransferRepository bankTransferRepository;

    public ApplyInternalTransferCommandHandler(
            LedgerAccountRepository ledgerAccountRepository,
            BankTransferRepository bankTransferRepository
    ) {
        this.ledgerAccountRepository = ledgerAccountRepository;
        this.bankTransferRepository = bankTransferRepository;
    }

    @Override
    public void handle(ApplyInternalTransferCommand command) {
        var logContext = LogContext.timed(LOGGER, LedgerOperation.APPLY_INTERNAL_TRANSFER)
                .with(LedgerLogKey.BANK_TRANSFER_ID, command.transferId().toString())
                .with(LedgerLogKey.SENDER_ACCOUNT_ID, command.senderAccountId().toString())
                .with(LedgerLogKey.RECIPIENT_ACCOUNT_ID, command.recipientAccountId().toString());

        try {
            var senderAccount = ledgerAccountRepository
                    .findByAccountReference(AccountReference.of(command.senderAccountId()))
                    .orElseThrow(() -> new LedgerAccountNotYetProvisionedException(command.senderAccountId()));

            var recipientAccount = ledgerAccountRepository
                    .findByAccountReference(AccountReference.of(command.recipientAccountId()))
                    .orElseThrow(() -> new LedgerAccountNotYetProvisionedException(command.recipientAccountId()));

            senderAccount.applyMovement(Movement.ofDebit(command.movementAmount()));
            recipientAccount.applyMovement(Movement.ofCredit(command.movementAmount()));

            ledgerAccountRepository.updateBalance(senderAccount);
            ledgerAccountRepository.updateBalance(recipientAccount);

            var transfer = bankTransferRepository
                    .findById(BankTransferId.of(command.transferId()))
                    .orElseThrow(() -> new IllegalStateException("BankTransfer not found: " + command.transferId()));

            transfer.complete(command.requestedAt());
            bankTransferRepository.complete(transfer);

            logContext.info("Internal transfer applied to ledger")
                    .success()
                    .log();
        }
        catch (Exception ex) {
            logContext.error("Failed to apply internal transfer to ledger", ex)
                    .failure()
                    .with(LogKey.REASON, LedgerFailureReason.INTERNAL_ERROR.normalize())
                    .log();

            throw ex;
        }
    }
}
