package com.jcondotta.banking.accounts.application.bankaccount.command.activate;

import com.jcondotta.application.command.CommandHandler;
import com.jcondotta.scopedlogging.domain.logging.LogKey;
import com.jcondotta.scopedlogging.LogScope;
import com.jcondotta.scopedlogging.application.logscope.LogContext;
import com.jcondotta.banking.accounts.application.bankaccount.command.activate.model.ActivateBankAccountCommand;
import com.jcondotta.banking.accounts.application.common.log.BankAccountOperation;
import com.jcondotta.banking.accounts.application.common.log.BankAccountLogKey;
import com.jcondotta.banking.accounts.domain.bankaccount.exceptions.BankAccountNotFoundException;
import com.jcondotta.banking.accounts.domain.bankaccount.repository.BankAccountRepository;
import com.jcondotta.banking.accounts.application.common.log.BankAccountFailureReason;
import com.jcondotta.banking.accounts.application.bankaccount.services.IbanGenerator;
import com.jcondotta.banking.accounts.domain.bankaccount.enums.BankAccountStateChangeResult;
import com.jcondotta.domain.exception.DomainException;
import io.micrometer.observation.annotation.Observed;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ActivateBankAccountCommandHandler implements CommandHandler<ActivateBankAccountCommand> {

  private final BankAccountRepository bankAccountRepository;
  private final IbanGenerator ibanGenerator;
  private final LogContext logContext;

  @Override
  @LogScope(BankAccountOperation.ACTIVATE)
  @Observed(
    name = BankAccountOperation.ACTIVATE,
    contextualName = "activateBankAccount",
    lowCardinalityKeyValues = {
      "aggregate", "bankAccount",
      "operation", "activate"
    }
  )
  public void handle(ActivateBankAccountCommand command) {
    logContext.with(BankAccountLogKey.BANK_ACCOUNT_ID, command.bankAccountId().value().toString());

    try {
      var bankAccount = bankAccountRepository.findById(command.bankAccountId())
        .orElseThrow(() -> new BankAccountNotFoundException(command.bankAccountId()));

      var iban = ibanGenerator.generate();
      var activationResult = bankAccount.activate(iban);

      if (activationResult == BankAccountStateChangeResult.CHANGED) {
        bankAccountRepository.save(bankAccount);

        logContext.info("Bank account activated")
          .with(BankAccountLogKey.STATE_CHANGE_RESULT, "changed")
          .success();
      }
      else {
        logContext.info("Bank account activation skipped; account already active")
          .with(BankAccountLogKey.STATE_CHANGE_RESULT, "already_in_target_state")
          .success();
      }
    }
    catch (DomainException ex) {
      var reason = BankAccountFailureReason.from(ex);

      logContext.warn("Bank account activation failed")
        .with(LogKey.REASON, reason.normalize())
        .failure();

      throw ex;
    }
    catch (Exception ex) {
      logContext.error("Unexpected error during bank account activation", ex)
        .with(LogKey.REASON, BankAccountFailureReason.INTERNAL_ERROR.normalize())
        .failure();

      throw ex;
    }
  }
}
