package com.jcondotta.banking.accounts.application.bankaccount.command.open;

import com.jcondotta.application.command.CommandHandlerWithResult;
import com.jcondotta.scopedlogging.domain.logging.LogKey;
import com.jcondotta.scopedlogging.LogScope;
import com.jcondotta.scopedlogging.application.logscope.LogContext;
import com.jcondotta.banking.accounts.application.bankaccount.command.open.model.OpenBankAccountCommand;
import com.jcondotta.banking.accounts.application.bankaccount.ports.output.BankAccountTransactionalWriter;
import com.jcondotta.banking.accounts.application.common.idempotency.IdempotencyService;
import com.jcondotta.banking.accounts.application.common.log.BankAccountOperation;
import com.jcondotta.banking.accounts.application.common.log.BankAccountLogKey;
import com.jcondotta.banking.accounts.domain.bankaccount.aggregate.BankAccount;
import com.jcondotta.banking.accounts.domain.bankaccount.identity.BankAccountId;
import com.jcondotta.banking.accounts.application.common.log.BankAccountFailureReason;
import com.jcondotta.domain.exception.DomainException;
import io.micrometer.observation.annotation.Observed;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OpenBankAccountCommandHandler implements CommandHandlerWithResult<OpenBankAccountCommand, BankAccountId> {

  private final IdempotencyService idempotencyService;
  private final BankAccountTransactionalWriter transactionalWriter;
  private final LogContext logContext;

  @Override
  @Observed(
    name = "accounts.open",
    contextualName = "openBankAccount",
    lowCardinalityKeyValues = {
      "aggregate", "bankAccount",
      "operation", "open"
    }
  )
  @LogScope(value = BankAccountOperation.OPEN)
  public BankAccountId handle(OpenBankAccountCommand command) {
    var summary = idempotencyService.check(command);

    if (summary.alreadyProcessed()) {
      logContext.with(BankAccountLogKey.BANK_ACCOUNT_ID, summary.aggregateId().value().toString())
        .with(BankAccountLogKey.EXECUTION_RESULT, "replayed")
        .info("Bank account opening replayed")
        .success();

      return summary.aggregateId();
    }

    try {
      var bankAccountId = BankAccountId.newId();
      logContext.with(BankAccountLogKey.BANK_ACCOUNT_ID, bankAccountId.value().toString());

      BankAccount bankAccount = BankAccount.open(
        bankAccountId,
        command.personalInfo(),
        command.contactInfo(),
        command.address(),
        command.accountType(),
        command.currency()
      );

      transactionalWriter.save(bankAccount, summary);

      logContext.info("Bank account opened")
        .success();

      return bankAccount.getId();
    }
    catch (DomainException ex) {
      var reason = BankAccountFailureReason.from(ex);

      logContext.warn("Bank account opening failed")
        .with(LogKey.REASON, reason.normalize())
        .failure();

      throw ex;
    }
    catch (Exception ex) {
      logContext.error("Unexpected error during bank account opening", ex)
        .with(LogKey.REASON, BankAccountFailureReason.INTERNAL_ERROR.normalize())
        .failure();

      throw ex;
    }
  }
}
