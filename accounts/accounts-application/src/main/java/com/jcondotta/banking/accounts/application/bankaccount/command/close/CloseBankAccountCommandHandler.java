package com.jcondotta.banking.accounts.application.bankaccount.command.close;

import com.jcondotta.application.command.CommandHandler;
import com.jcondotta.banking.accounts.application.common.log.BankAccountEventType;
import com.jcondotta.application.logging.LogContext;
import com.jcondotta.application.logging.LogKey;
import com.jcondotta.banking.accounts.application.common.log.BankAccountLogKey;
import com.jcondotta.banking.accounts.application.bankaccount.command.close.model.CloseBankAccountCommand;
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
public class CloseBankAccountCommandHandler implements CommandHandler<CloseBankAccountCommand> {

  private static final Logger LOGGER = LoggerFactory.getLogger(CloseBankAccountCommandHandler.class);

  private final BankAccountRepository bankAccountRepository;

  @Override
  @Observed(
    name = "accounts.close",
    contextualName = "closeBankAccount",
    lowCardinalityKeyValues = {
      "aggregate", "bankAccount",
      "operation", "close"
    }
  )
  public void handle(CloseBankAccountCommand command) {
    var logContext = LogContext.timed(LOGGER, BankAccountEventType.CLOSE)
      .with(BankAccountLogKey.BANK_ACCOUNT_ID, command.bankAccountId().value().toString());

    try {
      var bankAccount = bankAccountRepository.findById(command.bankAccountId())
        .orElseThrow(() -> new BankAccountNotFoundException(command.bankAccountId()));

      bankAccount.close();
      bankAccountRepository.save(bankAccount);

      logContext.info("Bank account closed")
        .success()
        .log();
    }
    catch (DomainException ex) {
      var reason = FailureReason.from(ex);

      logContext.warn("Bank account closing failed")
        .failure()
        .with(LogKey.REASON, reason.normalize())
        .log();

      throw ex;
    }
    catch (Exception ex) {
      logContext.error("Unexpected error during bank account closing", ex)
        .failure()
        .with(LogKey.REASON, FailureReason.INTERNAL_ERROR.normalize())
        .log();

      throw ex;
    }
  }
}
