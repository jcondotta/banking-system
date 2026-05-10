package com.jcondotta.banking.accounts.application.bankaccount.command.block;

import com.jcondotta.application.command.CommandHandler;
import com.jcondotta.banking.accounts.application.common.log.BankAccountEventType;
import com.jcondotta.application.logging.LogContext;
import com.jcondotta.application.logging.LogKey;
import com.jcondotta.banking.accounts.application.common.log.BankAccountLogKey;
import com.jcondotta.banking.accounts.application.bankaccount.command.block.model.BlockBankAccountCommand;
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
public class BlockBankAccountCommandHandler implements CommandHandler<BlockBankAccountCommand> {

  private static final Logger LOGGER = LoggerFactory.getLogger(BlockBankAccountCommandHandler.class);

  private final BankAccountRepository bankAccountRepository;

  @Override
  @Observed(
    name = "accounts.block",
    contextualName = "blockBankAccount",
    lowCardinalityKeyValues = {
      "aggregate", "bankAccount",
      "operation", "block"
    }
  )
  public void handle(BlockBankAccountCommand command) {
    var logContext = LogContext.timed(LOGGER, BankAccountEventType.BLOCK)
      .with(BankAccountLogKey.BANK_ACCOUNT_ID, command.bankAccountId().value().toString());

    try {
      var bankAccount = bankAccountRepository.findById(command.bankAccountId())
        .orElseThrow(() -> new BankAccountNotFoundException(command.bankAccountId()));

      bankAccount.block();
      bankAccountRepository.save(bankAccount);

      logContext.info("Bank account blocked")
        .success()
        .log();
    }
    catch (DomainException ex) {
      var reason = FailureReason.from(ex);

      logContext.warn("Bank account blocking failed")
        .failure()
        .with(LogKey.REASON, reason.normalize())
        .log();

      throw ex;
    }
    catch (Exception ex) {
      logContext.error("Unexpected error during bank account blocking", ex)
        .failure()
        .with(LogKey.REASON, FailureReason.INTERNAL_ERROR.normalize())
        .log();

      throw ex;
    }
  }
}
