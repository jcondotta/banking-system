package com.jcondotta.banking.accounts.application.bankaccount.command.activate;

import com.jcondotta.application.core.CommandHandler;
import com.jcondotta.banking.accounts.application.bankaccount.command.activate.model.ActivateBankAccountCommand;
import com.jcondotta.banking.accounts.domain.bankaccount.exceptions.BankAccountNotFoundException;
import com.jcondotta.banking.accounts.domain.bankaccount.repository.BankAccountRepository;
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
    name = "bankaccount.activate",
    contextualName = "activateBankAccount",
    lowCardinalityKeyValues = {
      "aggregate", "bankAccount",
      "operation", "activate"
    }
  )
  public void handle(ActivateBankAccountCommand command) {
    var bankAccount = bankAccountRepository.findById(command.bankAccountId())
      .orElseThrow(() -> new BankAccountNotFoundException(command.bankAccountId()));

    bankAccount.activate();
    bankAccountRepository.save(bankAccount);

    log.atInfo()
      .setMessage("Bank account activated successfully.")
      .addKeyValue("id", bankAccount.getId())
      .log();
  }
}