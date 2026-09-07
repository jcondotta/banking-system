package com.jcondotta.banking.transfers.ledger.application.ledger_account.command.provision;

import com.jcondotta.application.command.CommandHandler;
import com.jcondotta.application.logging.LogContext;
import com.jcondotta.application.logging.LogKey;
import com.jcondotta.banking.transfers.domain.common.FailureReason;
import com.jcondotta.banking.money.Currency;
import com.jcondotta.banking.transfers.ledger.application.common.log.LedgerOperation;
import com.jcondotta.banking.transfers.ledger.application.common.log.LedgerLogKey;
import com.jcondotta.banking.transfers.ledger.domain.ledger_account.aggregate.LedgerAccount;
import com.jcondotta.banking.transfers.ledger.domain.ledger_account.repository.LedgerAccountRepository;
import com.jcondotta.banking.transfers.ledger.domain.ledger_account.value_objects.AccountReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

@Component
public class ProvisionLedgerAccountCommandHandler implements CommandHandler<ProvisionLedgerAccountCommand> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProvisionLedgerAccountCommandHandler.class);

    private final LedgerAccountRepository ledgerAccountRepository;

    public ProvisionLedgerAccountCommandHandler(LedgerAccountRepository ledgerAccountRepository) {
        this.ledgerAccountRepository = ledgerAccountRepository;
    }

    @Override
    public void handle(ProvisionLedgerAccountCommand command) {
        var accountReference = AccountReference.of(command.accountId());
        var currency = Currency.valueOf(command.currency());

        var logContext = LogContext.timed(LOGGER, LedgerOperation.PROVISION_LEDGER_ACCOUNT)
                .with(LedgerLogKey.ACCOUNT_REFERENCE, command.accountId().toString())
                .with(LedgerLogKey.CURRENCY, command.currency());

        try {
            var ledgerAccount = LedgerAccount.provision(accountReference, currency, command.activatedAt());

            ledgerAccountRepository.save(ledgerAccount);

            logContext.with(LedgerLogKey.LEDGER_ACCOUNT_ID, ledgerAccount.getId().value().toString())
                    .with(LedgerLogKey.LEDGER_ACCOUNT_TYPE, ledgerAccount.getAccountType().name())
                    .with(LedgerLogKey.LEDGER_ACCOUNT_STATUS, ledgerAccount.getStatus().name())
                    .info("Ledger account provisioned")
                    .success()
                    .log();
        }
        catch (DataIntegrityViolationException ex) {
            logContext.info("Ledger account already provisioned — idempotent redelivery")
                    .success()
                    .log();
        }
        catch (Exception ex) {
            logContext.error("Unexpected error during ledger account provisioning", ex)
                    .failure()
                    .with(LogKey.REASON, FailureReason.INTERNAL_ERROR.normalize())
                    .log();

            throw ex;
        }
    }
}
