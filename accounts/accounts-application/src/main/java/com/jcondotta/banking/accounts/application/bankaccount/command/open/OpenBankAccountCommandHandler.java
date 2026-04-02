package com.jcondotta.banking.accounts.application.bankaccount.command.open;

import com.jcondotta.application.command.CommandHandlerWithResult;
import com.jcondotta.banking.accounts.application.bankaccount.command.open.model.OpenBankAccountCommand;
import com.jcondotta.banking.accounts.application.bankaccount.ports.output.facade.IbanGeneratorFacade;
import com.jcondotta.banking.accounts.domain.bankaccount.aggregate.BankAccount;
import com.jcondotta.banking.accounts.domain.bankaccount.identity.BankAccountId;
import com.jcondotta.banking.accounts.domain.bankaccount.repository.BankAccountRepository;
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
    name = "bankaccount.open",
    contextualName = "openBankAccount",
    lowCardinalityKeyValues = {
      "aggregate", "bankAccount",
      "operation", "open"
    }
  )
  public BankAccountId handle(OpenBankAccountCommand command) {
    var iban = ibanGeneratorFacade.generate();

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

    log.atInfo()
      .setMessage("Bank account created successfully.")
      .addKeyValue("id", bankAccount.getId())
      .log();

    return bankAccount.getId();
  }
}
