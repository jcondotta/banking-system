package com.jcondotta.banking.accounts.application.bankaccount.command.unblock;

import com.jcondotta.application.command.CommandHandler;
import com.jcondotta.scopedlogging.LogScope;
import com.jcondotta.scopedlogging.application.logscope.LogContext;
import com.jcondotta.scopedlogging.domain.logging.LogKey;
import com.jcondotta.banking.accounts.application.bankaccount.command.unblock.model.UnblockBankAccountCommand;
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
public class UnblockBankAccountCommandHandler implements CommandHandler<UnblockBankAccountCommand> {

  private final BankAccountRepository bankAccountRepository;
  private final LogContext logContext;

  @Override
  @Observed(
    name = BankAccountOperation.UNBLOCK,
    contextualName = "unblockBankAccount",
    lowCardinalityKeyValues = {
      "aggregate", "bankAccount",
      "operation", "unblock"
    }
  )
  @LogScope(BankAccountOperation.UNBLOCK)
  public void handle(UnblockBankAccountCommand command) {
    logContext.with(BankAccountLogKey.BANK_ACCOUNT_ID, command.bankAccountId().value().toString());

    try {
      var bankAccount = bankAccountRepository.findById(command.bankAccountId())
        .orElseThrow(() -> new BankAccountNotFoundException(command.bankAccountId()));

      var stateChangeResult = bankAccount.unblock();

      if (stateChangeResult == BankAccountStateChangeResult.CHANGED) {
        bankAccountRepository.save(bankAccount);

        logContext.info("Bank account unblocked")
          .with(BankAccountLogKey.STATE_CHANGE_RESULT, "changed")
          .success();
      }
      else {
        logContext.info("Bank account unblocking skipped; account already active")
          .with(BankAccountLogKey.STATE_CHANGE_RESULT, "already_in_target_state")
          .success();
      }
    }
    catch (DomainException ex) {
      var reason = BankAccountFailureReason.from(ex);

      logContext.warn("Bank account unblocking failed")
        .with(LogKey.REASON, reason.normalize())
        .failure();

      throw ex;
    }
    catch (Exception ex) {
      logContext.error("Unexpected error during bank account unblocking", ex)
        .with(LogKey.REASON, BankAccountFailureReason.INTERNAL_ERROR.normalize())
        .failure();

      throw ex;
    }
  }
}
