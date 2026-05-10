package com.jcondotta.banking.accounts.application.bankaccount.command.open;

import com.jcondotta.application.command.CommandHandlerWithResult;
import com.jcondotta.banking.accounts.application.common.log.BankAccountEventType;
import com.jcondotta.application.logging.LogContext;
import com.jcondotta.application.logging.LogEvent;
import com.jcondotta.application.logging.LogKey;
import com.jcondotta.banking.accounts.application.common.log.BankAccountLogKey;
import com.jcondotta.banking.accounts.application.bankaccount.command.open.model.OpenBankAccountCommand;
import com.jcondotta.banking.accounts.application.bankaccount.ports.output.facade.IbanGeneratorFacade;
import com.jcondotta.banking.accounts.domain.common.FailureReason;
import com.jcondotta.banking.accounts.domain.bankaccount.aggregate.BankAccount;
import com.jcondotta.banking.accounts.domain.bankaccount.identity.BankAccountId;
import com.jcondotta.banking.accounts.domain.bankaccount.repository.BankAccountRepository;
import com.jcondotta.banking.accounts.domain.bankaccount.value_objects.Iban;
import com.jcondotta.domain.exception.DomainException;
import io.micrometer.observation.annotation.Observed;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OpenBankAccountCommandHandler implements CommandHandlerWithResult<OpenBankAccountCommand, BankAccountId> {

  private static final Logger LOGGER = LoggerFactory.getLogger(OpenBankAccountCommandHandler.class);

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
    var logContext = LogContext.timed(LOGGER, BankAccountEventType.OPEN);
    Iban iban = null;

    try {
      iban = ibanGeneratorFacade.generate();

      BankAccount bankAccount = BankAccount.open(
        BankAccountId.newId(),
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
        .with(BankAccountLogKey.BANK_ACCOUNT_ID, bankAccount.getId().value().toString())
        .with(BankAccountLogKey.MASKED_IBAN, iban.masked())
        .log();

      return bankAccount.getId();
    }
    catch (DomainException ex) {
      var reason = FailureReason.from(ex);

      withMaskedIban(logContext.warn("Bank account opening failed"), iban)
        .failure()
        .with(LogKey.REASON, reason.normalize())
        .log();

      throw ex;
    }
    catch (Exception ex) {
      withMaskedIban(logContext.error("Unexpected error during bank account opening", ex), iban)
        .failure()
        .with(LogKey.REASON, FailureReason.INTERNAL_ERROR.normalize())
        .log();

      throw ex;
    }
  }

  private static LogEvent withMaskedIban(LogEvent event, Iban iban) {
    if (iban == null) {
      return event;
    }

    return event.with(BankAccountLogKey.MASKED_IBAN, iban.masked());
  }
}
