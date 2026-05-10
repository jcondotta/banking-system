package com.jcondotta.banking.accounts.application.bankaccount.command.unblock;

import com.jcondotta.application.command.CommandHandler;
import com.jcondotta.banking.accounts.application.common.log.BankAccountEventType;
import com.jcondotta.application.logging.LogContext;
import com.jcondotta.application.logging.LogKey;
import com.jcondotta.banking.accounts.application.common.log.BankAccountLogKey;
import com.jcondotta.banking.accounts.application.bankaccount.command.unblock.model.UnblockBankAccountCommand;
import com.jcondotta.banking.accounts.domain.common.FailureReason;
import com.jcondotta.banking.accounts.domain.bankaccount.exceptions.BankAccountNotFoundException;
import com.jcondotta.banking.accounts.domain.bankaccount.repository.BankAccountRepository;
import com.jcondotta.domain.exception.DomainException;
import io.micrometer.observation.annotation.Observed;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UnblockBankAccountCommandHandler implements CommandHandler<UnblockBankAccountCommand> {

  private static final Logger LOGGER = LoggerFactory.getLogger(UnblockBankAccountCommandHandler.class);

  private final BankAccountRepository bankAccountRepository;

  @Override
  @Observed(
    name = "accounts.unblock",
    contextualName = "unblockBankAccount",
    lowCardinalityKeyValues = {
      "aggregate", "bankAccount",
      "operation", "unblock"
    }
  )
  public void handle(UnblockBankAccountCommand command) {
    var logContext = LogContext.timed(LOGGER, BankAccountEventType.UNBLOCK)
      .with(BankAccountLogKey.BANK_ACCOUNT_ID, command.bankAccountId().value().toString());

    try {
      var bankAccount = bankAccountRepository.findById(command.bankAccountId())
        .orElseThrow(() -> new BankAccountNotFoundException(command.bankAccountId()));

      bankAccount.unblock();

      bankAccountRepository.save(bankAccount);

      logContext.info("Bank account unblocked")
        .success()
        .log();
    }
    catch (DomainException ex) {
      var reason = FailureReason.from(ex);

      logContext.warn("Bank account unblocking failed")
        .failure()
        .with(LogKey.REASON, reason.normalize())
        .log();

      throw ex;
    }
    catch (Exception ex) {
      logContext.error("Unexpected error during bank account unblocking", ex)
        .failure()
        .with(LogKey.REASON, FailureReason.INTERNAL_ERROR.normalize())
        .log();

      throw ex;
    }
  }
}
