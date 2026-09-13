package com.jcondotta.banking.ledger.application.ledger_account.command.apply_transfer;

import com.jcondotta.application.command.CommandHandler;
import com.jcondotta.application.logging.LogContext;
import com.jcondotta.application.logging.LogKey;
import com.jcondotta.banking.ledger.application.common.log.LedgerFailureReason;
import com.jcondotta.banking.ledger.application.common.log.LedgerLogKey;
import com.jcondotta.banking.ledger.application.common.log.LedgerOperation;
import com.jcondotta.banking.ledger.domain.ledger_account.entries.LedgerEntry;
import com.jcondotta.banking.ledger.domain.ledger_account.exceptions.LedgerAccountNotYetProvisionedException;
import com.jcondotta.banking.ledger.domain.ledger_account.repository.LedgerAccountRepository;
import com.jcondotta.banking.ledger.domain.ledger_account.value_objects.AccountReference;
import com.jcondotta.banking.movement.Movement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class ApplyInternalTransferCommandHandler implements CommandHandler<ApplyInternalTransferCommand> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplyInternalTransferCommandHandler.class);

    private final LedgerAccountRepository ledgerAccountRepository;

    public ApplyInternalTransferCommandHandler(LedgerAccountRepository ledgerAccountRepository) {
        this.ledgerAccountRepository = ledgerAccountRepository;
    }

    @Override
    @Transactional
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

            var debitEntry = senderAccount.applyMovement(Movement.ofDebit(command.movementAmount()), command.transferId(), command.requestedAt());
            var creditEntry = recipientAccount.applyMovement(Movement.ofCredit(command.movementAmount()), command.transferId(), command.requestedAt());

            ledgerAccountRepository.updateBalance(senderAccount);
            ledgerAccountRepository.updateBalance(recipientAccount);

            ledgerAccountRepository.saveLedgerEntries(List.of(debitEntry, creditEntry));

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
