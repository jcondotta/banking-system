package com.jcondotta.banking.accounts.application.bankaccount.command.add_joint_holder;

import com.jcondotta.application.command.CommandHandler;
import com.jcondotta.banking.accounts.application.bankaccount.command.add_joint_holder.model.AddJointHolderCommand;
import com.jcondotta.banking.accounts.application.bankaccount.ports.output.BankAccountTransactionalWriter;
import com.jcondotta.banking.accounts.application.common.idempotency.IdempotencyService;
import com.jcondotta.banking.accounts.application.common.log.BankAccountOperation;
import com.jcondotta.banking.accounts.application.common.log.BankAccountLogKey;
import com.jcondotta.banking.accounts.domain.bankaccount.exceptions.BankAccountNotFoundException;
import com.jcondotta.banking.accounts.domain.bankaccount.repository.BankAccountRepository;
import com.jcondotta.banking.accounts.application.common.log.BankAccountFailureReason;
import com.jcondotta.domain.exception.DomainException;
import com.jcondotta.scopedlogging.LogScope;
import com.jcondotta.scopedlogging.application.logscope.LogContext;
import com.jcondotta.scopedlogging.domain.logging.LogKey;
import com.jcondotta.scopedlogging.domain.logging.LogLevel;
import io.micrometer.observation.annotation.Observed;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AddJointHolderCommandHandler implements CommandHandler<AddJointHolderCommand> {

  private final BankAccountRepository bankAccountRepository;
  private final IdempotencyService idempotencyService;
  private final BankAccountTransactionalWriter transactionalWriter;
  private final LogContext logContext;

  @Override
  @Observed(
    name = BankAccountOperation.ADD_JOINT_HOLDER,
    contextualName = "addJointHolder",
    lowCardinalityKeyValues = {
      "aggregate", "bankAccount",
      "operation", "addJointHolder"
    }
  )
  @LogScope(value = BankAccountOperation.ADD_JOINT_HOLDER, level = LogLevel.INFO)
  public void handle(AddJointHolderCommand command) {
    logContext.with(BankAccountLogKey.BANK_ACCOUNT_ID, command.bankAccountId().value().toString());

    var summary = idempotencyService.check(command);

    if (summary.alreadyProcessed()) {
      logContext.with(BankAccountLogKey.EXECUTION_RESULT, "replayed")
        .info("Joint holder addition replayed")
        .success();
      return;
    }

    try {
      var bankAccount = bankAccountRepository.findById(command.bankAccountId())
        .orElseThrow(() -> new BankAccountNotFoundException(command.bankAccountId()));

      bankAccount.addJointHolder(
        command.personalInfo(),
        command.contactInfo(),
        command.address()
      );

      transactionalWriter.save(bankAccount, summary);

      logContext.info("Joint account holder added")
        .success();
    }
    catch (DomainException ex) {
      var reason = BankAccountFailureReason.from(ex);

      logContext.warn("Joint account holder addition failed")
        .with(LogKey.REASON, reason.normalize())
        .failure();

      throw ex;
    }
    catch (Exception ex) {
      logContext.error("Unexpected error during joint account holder addition", ex)
        .with(LogKey.REASON, BankAccountFailureReason.INTERNAL_ERROR.normalize())
        .failure();

      throw ex;
    }
  }
}
