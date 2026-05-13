package com.jcondotta.banking.accounts.application.bankaccount.command.activate;

import com.jcondotta.application.command.CommandHandler;
import com.jcondotta.application.logging.LogContext;
import com.jcondotta.application.logging.LogKey;
import com.jcondotta.banking.accounts.application.bankaccount.command.activate.model.ActivateBankAccountCommand;
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
public class ActivateBankAccountCommandHandler implements CommandHandler<ActivateBankAccountCommand> {

  private final BankAccountRepository bankAccountRepository;

  @Override
  @Observed(
    name = "accounts.activate",
    contextualName = "activateBankAccount",
    lowCardinalityKeyValues = {
      "aggregate", "bankAccount",
      "operation", "activate"
    }
  )
  public void handle(ActivateBankAccountCommand command) {
    var logContext = LogContext.timed(log, BankAccountEventType.ACTIVATE)
      .with(BankAccountLogKey.BANK_ACCOUNT_ID, command.bankAccountId().value().toString());

    try {
      var bankAccount = bankAccountRepository.findById(command.bankAccountId())
        .orElseThrow(() -> new BankAccountNotFoundException(command.bankAccountId()));

      bankAccount.activate();
      bankAccountRepository.save(bankAccount);

      logContext.info("Bank account activated")
        .success()
        .log();
    }
    catch (DomainException ex) {
      var reason = FailureReason.from(ex);

      logContext.warn("Bank account activation failed")
        .failure()
        .with(LogKey.REASON, reason.normalize())
        .log();

      throw ex;
    }
    catch (Exception ex) {
      logContext.error("Unexpected error during bank account activation", ex)
        .failure()
        .with(LogKey.REASON, FailureReason.INTERNAL_ERROR.normalize())
        .log();

      throw ex;
    }
  }
}
