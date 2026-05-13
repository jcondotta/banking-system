package com.jcondotta.banking.accounts.application.bankaccount.command.add_joint_holder;

import com.jcondotta.application.command.CommandHandler;
import com.jcondotta.application.logging.LogContext;
import com.jcondotta.application.logging.LogKey;
import com.jcondotta.banking.accounts.application.bankaccount.command.add_joint_holder.model.AddJointHolderCommand;
import com.jcondotta.banking.accounts.application.common.log.BankAccountEventType;
import com.jcondotta.banking.accounts.application.common.log.BankAccountLogKey;
import com.jcondotta.banking.accounts.domain.bankaccount.exceptions.BankAccountNotFoundException;
import com.jcondotta.banking.accounts.domain.bankaccount.repository.BankAccountRepository;
import com.jcondotta.banking.accounts.domain.common.FailureReason;
import com.jcondotta.domain.exception.DomainException;
import io.micrometer.observation.annotation.Observed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AddJointHolderCommandHandler implements CommandHandler<AddJointHolderCommand> {

  private final BankAccountRepository bankAccountRepository;

  @Override
  @Observed(
    name = "accounts.addJointHolder",
    contextualName = "addJointHolder",
    lowCardinalityKeyValues = {
      "aggregate", "bankAccount",
      "operation", "addJointHolder"
    }
  )
  public void handle(AddJointHolderCommand command) {
    var logContext = LogContext.timed(log, BankAccountEventType.ADD_JOINT_HOLDER)
      .with(BankAccountLogKey.BANK_ACCOUNT_ID, command.bankAccountId().value().toString());

    try {
      var bankAccount = bankAccountRepository.findById(command.bankAccountId())
        .orElseThrow(() -> new BankAccountNotFoundException(command.bankAccountId()));

      bankAccount.addJointHolder(
        command.personalInfo(),
        command.contactInfo(),
        command.address()
      );

      bankAccountRepository.save(bankAccount);

      logContext.info("Joint account holder added")
        .success()
        .log();
    }
    catch (DomainException ex) {
      var reason = FailureReason.from(ex);

      logContext.warn("Joint account holder addition failed")
        .failure()
        .with(LogKey.REASON, reason.normalize())
        .log();

      throw ex;
    }
    catch (Exception ex) {
      logContext.error("Unexpected error during joint account holder addition", ex)
        .failure()
        .with(LogKey.REASON, FailureReason.INTERNAL_ERROR.normalize())
        .log();

      throw ex;
    }
  }
}
