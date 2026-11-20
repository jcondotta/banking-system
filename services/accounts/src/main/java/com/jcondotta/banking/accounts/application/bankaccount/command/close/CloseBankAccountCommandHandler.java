package com.jcondotta.banking.accounts.application.bankaccount.command.close;

import com.jcondotta.application.command.CommandHandler;
import com.jcondotta.scopedlogging.LogScope;
import com.jcondotta.scopedlogging.application.logscope.LogContext;
import com.jcondotta.scopedlogging.domain.logging.LogKey;
import com.jcondotta.banking.accounts.application.bankaccount.command.close.model.CloseBankAccountCommand;
import com.jcondotta.banking.accounts.application.common.log.BankAccountOperation;
import com.jcondotta.banking.accounts.application.common.log.BankAccountLogKey;
import com.jcondotta.banking.accounts.domain.bankaccount.exceptions.BankAccountNotFoundException;
import com.jcondotta.banking.accounts.domain.bankaccount.enums.BankAccountStateChangeResult;
import com.jcondotta.banking.accounts.domain.bankaccount.repository.BankAccountRepository;
import com.jcondotta.banking.accounts.application.common.log.BankAccountFailureReason;
import com.jcondotta.domain.exception.DomainException;
import io.micrometer.observation.annotation.Observed;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CloseBankAccountCommandHandler implements CommandHandler<CloseBankAccountCommand> {

  private final BankAccountRepository bankAccountRepository;
  private final LogContext logContext;

  @Override
  @LogScope(BankAccountOperation.CLOSE)
  @Observed(
    name = "accounts.close",
    contextualName = "closeBankAccount",
    lowCardinalityKeyValues = {
      "aggregate", "bankAccount",
      "operation", "close"
    }
  )
  public void handle(CloseBankAccountCommand command) {
    logContext.with(BankAccountLogKey.BANK_ACCOUNT_ID, command.bankAccountId().value().toString());

    try {
      var bankAccount = bankAccountRepository.findById(command.bankAccountId())
        .orElseThrow(() -> new BankAccountNotFoundException(command.bankAccountId()));

      var stateChangeResult = bankAccount.close();

      if (stateChangeResult == BankAccountStateChangeResult.CHANGED) {
        bankAccountRepository.save(bankAccount);

        logContext.info("Bank account closed")
          .with(BankAccountLogKey.STATE_CHANGE_RESULT, "changed")
          .success();
      }
      else {
        logContext.info("Bank account closing skipped; account already closed")
          .with(BankAccountLogKey.STATE_CHANGE_RESULT, "already_in_target_state")
          .success();
      }
    }
    catch (DomainException ex) {
      var reason = BankAccountFailureReason.from(ex);

      logContext.warn("Bank account closing failed")
        .with(LogKey.REASON, reason.normalize())
        .failure();

      throw ex;
    }
    catch (Exception ex) {
      logContext.error("Unexpected error during bank account closing", ex)
        .with(LogKey.REASON, BankAccountFailureReason.INTERNAL_ERROR.normalize())
        .failure();

      throw ex;
    }
  }
}
