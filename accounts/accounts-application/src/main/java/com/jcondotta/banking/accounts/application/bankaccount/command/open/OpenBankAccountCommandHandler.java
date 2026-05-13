package com.jcondotta.banking.accounts.application.bankaccount.command.open;

import com.jcondotta.application.command.CommandHandlerWithResult;
import com.jcondotta.application.logging.LogContext;
import com.jcondotta.application.logging.LogKey;
import com.jcondotta.banking.accounts.application.bankaccount.command.open.model.OpenBankAccountCommand;
import com.jcondotta.banking.accounts.application.bankaccount.ports.output.facade.IbanGeneratorFacade;
import com.jcondotta.banking.accounts.application.common.log.BankAccountEventType;
import com.jcondotta.banking.accounts.application.common.log.BankAccountLogKey;
import com.jcondotta.banking.accounts.domain.bankaccount.aggregate.BankAccount;
import com.jcondotta.banking.accounts.domain.bankaccount.identity.BankAccountId;
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
public class OpenBankAccountCommandHandler implements CommandHandlerWithResult<OpenBankAccountCommand, BankAccountId> {

  private final BankAccountRepository bankAccountRepository;
  private final IbanGeneratorFacade ibanGeneratorFacade;

  @Override
  @Observed(
    name = "accounts.open",
    contextualName = "openBankAccount",
    lowCardinalityKeyValues = {
      "aggregate", "bankAccount",
      "operation", "open"
    }
  )
  public BankAccountId handle(OpenBankAccountCommand command) {
    var bankAccountId = BankAccountId.newId();

    var logContext = LogContext.timed(log, BankAccountEventType.OPEN)
      .with(BankAccountLogKey.BANK_ACCOUNT_ID, bankAccountId.value().toString());;

    try {
      var iban = ibanGeneratorFacade.generate();

      BankAccount bankAccount = BankAccount.open(
        bankAccountId,
        command.personalInfo(),
        command.contactInfo(),
        command.address(),
        command.accountType(),
        command.currency(),
        iban
      );

      bankAccountRepository.save(bankAccount);

      logContext.info("Bank account opened")
        .success()
        .log();

      return bankAccount.getId();
    }
    catch (DomainException ex) {
      var reason = FailureReason.from(ex);

      logContext.warn("Bank account opening failed")
        .failure()
        .with(LogKey.REASON, reason.normalize())
        .log();

      throw ex;
    }
    catch (Exception ex) {
      logContext.error("Unexpected error during bank account opening", ex)
        .failure()
        .with(LogKey.REASON, FailureReason.INTERNAL_ERROR.normalize())
        .log();

      throw ex;
    }
  }
}
