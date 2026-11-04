package com.jcondotta.banking.accounts.application.bankaccount.command.open;

import com.jcondotta.application.command.CommandHandlerWithResult;
import com.jcondotta.application.logging.LogContext;
import com.jcondotta.application.logging.LogKey;
import com.jcondotta.application.logging.IdempotencyLogKey;
import com.jcondotta.banking.accounts.application.bankaccount.command.open.model.OpenBankAccountCommand;
import com.jcondotta.banking.accounts.application.common.idempotency.*;
import com.jcondotta.banking.accounts.application.common.exception.IdempotencyKeyConflictException;
import com.jcondotta.banking.accounts.application.common.log.*;
import com.jcondotta.banking.accounts.domain.bankaccount.aggregate.BankAccount;
import com.jcondotta.banking.accounts.domain.bankaccount.identity.BankAccountId;

import com.jcondotta.domain.exception.DomainException;
import io.micrometer.observation.annotation.Observed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OpenBankAccountCommandHandler implements CommandHandlerWithResult<OpenBankAccountCommand, BankAccountId> {
    private final IdempotentAccountWriter idempotentWriter;

    @Override
    @Observed(
        name = "accounts.open",
        contextualName = "openBankAccount",
        lowCardinalityKeyValues = {
          "aggregate", "bankAccount",
          "operation", "open"
        }
    )
    public BankAccountId handle(OpenBankAccountCommand command) {
        var context = LogContext.timed(log, BankAccountOperation.OPEN)
            .with(IdempotencyLogKey.IDEMPOTENCY_KEY, command.idempotencyKey().value().toString());
        try {
            return idempotentWriter.execute(command.idempotencyKey(), AccountCommandFingerprint.hash(command), () -> {
                return BankAccount.open(BankAccountId.newId(), command.personalInfo(), command.contactInfo(),
                    command.address(), command.accountType(), command.currency());
            }, context);
        }
        catch (IdempotencyKeyConflictException ex) {
            context.warn("Idempotency key reused with different request data").failure()
                .with(LogKey.REASON, "idempotency_conflict").log();
            throw ex;
        }
        catch (DomainException ex) {
            context.warn("Account command rejected").failure()
                .with(LogKey.REASON, BankAccountFailureReason.from(ex).normalize()).log();
            throw ex;
        }
        catch (Exception ex) {
            context.error("Account command failed", ex).failure()
              .with(LogKey.REASON, BankAccountFailureReason.INTERNAL_ERROR.normalize())
              .log();
            throw ex;
        }
    }
}
