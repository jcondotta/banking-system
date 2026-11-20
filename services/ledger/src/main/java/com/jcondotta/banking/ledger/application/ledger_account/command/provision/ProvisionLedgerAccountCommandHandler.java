package com.jcondotta.banking.ledger.application.ledger_account.command.provision;

import com.jcondotta.application.command.CommandHandler;
import com.jcondotta.scopedlogging.application.logscope.LogContext;
import com.jcondotta.scopedlogging.LogScope;
import com.jcondotta.scopedlogging.domain.logging.LogKey;
import com.jcondotta.banking.money.Currency;
import com.jcondotta.banking.ledger.application.common.log.LedgerFailureReason;
import com.jcondotta.banking.ledger.application.common.log.LedgerLogKey;
import com.jcondotta.banking.ledger.application.common.log.LedgerOperation;
import com.jcondotta.banking.ledger.domain.ledger_account.aggregate.LedgerAccount;
import com.jcondotta.banking.ledger.domain.ledger_account.repository.LedgerAccountRepository;
import com.jcondotta.banking.ledger.domain.ledger_account.value_objects.AccountReference;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

@Component
public class ProvisionLedgerAccountCommandHandler implements CommandHandler<ProvisionLedgerAccountCommand> {


    private final LogContext logContext;

    private final LedgerAccountRepository ledgerAccountRepository;

    public ProvisionLedgerAccountCommandHandler(LedgerAccountRepository ledgerAccountRepository, LogContext logContext) {
        this.logContext = logContext;
        this.ledgerAccountRepository = ledgerAccountRepository;
    }

    @Override
    @LogScope(LedgerOperation.PROVISION_LEDGER_ACCOUNT)
    public void handle(ProvisionLedgerAccountCommand command) {
        var accountReference = AccountReference.of(command.accountId());
        var currency = Currency.valueOf(command.currency());

        logContext.with(LedgerLogKey.ACCOUNT_REFERENCE, command.accountId().toString())
                .with(LedgerLogKey.CURRENCY, command.currency());

        try {
            var ledgerAccount = LedgerAccount.provision(accountReference, currency, command.activatedAt());

            ledgerAccountRepository.save(ledgerAccount);

            logContext.with(LedgerLogKey.LEDGER_ACCOUNT_ID, ledgerAccount.getId().value().toString())
                    .with(LedgerLogKey.LEDGER_ACCOUNT_TYPE, ledgerAccount.getAccountType().name())
                    .with(LedgerLogKey.LEDGER_ACCOUNT_STATUS, ledgerAccount.getStatus().name())
                    .info("Ledger account provisioned")
                    .success();
        }
        catch (DataIntegrityViolationException ex) {
            logContext.info("Ledger account already provisioned — idempotent redelivery")
                    .success();
        }
        catch (Exception ex) {
            logContext.error("Unexpected error during ledger account provisioning", ex)
                    .with(LogKey.REASON, LedgerFailureReason.INTERNAL_ERROR.normalize())
                    .failure();

            throw ex;
        }
    }
}
