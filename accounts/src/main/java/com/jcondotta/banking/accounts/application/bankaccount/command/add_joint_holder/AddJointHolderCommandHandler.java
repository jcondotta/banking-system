package com.jcondotta.banking.accounts.application.bankaccount.command.add_joint_holder;

import com.jcondotta.application.command.CommandHandler;
import com.jcondotta.application.logging.LogContext;
import com.jcondotta.application.logging.LogKey;
import com.jcondotta.banking.accounts.application.bankaccount.command.add_joint_holder.model.AddJointHolderCommand;
import com.jcondotta.banking.accounts.application.common.idempotency.*;
import com.jcondotta.banking.accounts.application.common.exception.IdempotencyKeyConflictException;
import com.jcondotta.banking.accounts.application.common.log.*;
import com.jcondotta.banking.accounts.domain.bankaccount.aggregate.BankAccount;
import com.jcondotta.banking.accounts.domain.bankaccount.identity.BankAccountId;
import com.jcondotta.banking.accounts.domain.bankaccount.repository.BankAccountRepository;
import com.jcondotta.banking.accounts.domain.bankaccount.exceptions.BankAccountNotFoundException;
import com.jcondotta.domain.exception.DomainException;
import io.micrometer.observation.annotation.Observed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AddJointHolderCommandHandler implements CommandHandler<AddJointHolderCommand> {
    private final IdempotentAccountWriter idempotentWriter;
    private final BankAccountRepository bankAccountRepository;

    @Override
    @Observed(name = "accounts.addJointHolder", contextualName = "addJointHolder",
        lowCardinalityKeyValues = {"aggregate", "bankAccount", "operation", "addJointHolder"})
    public void handle(AddJointHolderCommand command) {
        var context = LogContext.timed(log, BankAccountOperation.ADD_JOINT_HOLDER)
            .with(IdempotencyLogKey.IDEMPOTENCY_KEY, command.idempotencyKey().value().toString());
        try {
            idempotentWriter.execute(command.idempotencyKey(), AccountCommandFingerprint.hash(command), () -> {
                var account = bankAccountRepository.findById(command.bankAccountId())
                    .orElseThrow(() -> new BankAccountNotFoundException(command.bankAccountId()));
                account.addJointHolder(command.personalInfo(), command.contactInfo(), command.address());
                return account;
            }, context);
        } catch (IdempotencyKeyConflictException ex) {
            context.warn("Idempotency key reused with different request data").failure()
                .with(LogKey.REASON, "idempotency_conflict").log();
            throw ex;
        } catch (DomainException ex) {
            context.warn("Account command rejected").failure()
                .with(LogKey.REASON, BankAccountFailureReason.from(ex).normalize()).log();
            throw ex;
        } catch (Exception ex) {
            context.error("Account command failed", ex).failure()
                .with(LogKey.REASON, BankAccountFailureReason.INTERNAL_ERROR.normalize()).log();
            throw ex;
        }
    }
}
