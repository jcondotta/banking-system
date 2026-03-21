package com.jcondotta.banking.accounts.application.bankaccount.command.close;

import com.jcondotta.application.core.command.CommandHandler;
import com.jcondotta.banking.accounts.application.bankaccount.command.close.model.CloseBankAccountCommand;
import com.jcondotta.banking.accounts.domain.bankaccount.exceptions.BankAccountNotFoundException;
import com.jcondotta.banking.accounts.domain.bankaccount.repository.BankAccountRepository;
import io.micrometer.observation.annotation.Observed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CloseBankAccountCommandHandler implements CommandHandler<CloseBankAccountCommand> {

  private final BankAccountRepository bankAccountRepository;

  @Override
  @Observed(
    name = "bankaccount.close",
    contextualName = "closeBankAccount",
    lowCardinalityKeyValues = {
      "aggregate", "bankAccount",
      "operation", "close"
    }
  )
  public void handle(CloseBankAccountCommand command) {
    var bankAccount = bankAccountRepository.findById(command.bankAccountId())
      .orElseThrow(() -> new BankAccountNotFoundException(command.bankAccountId()));

    bankAccount.close();
    bankAccountRepository.save(bankAccount);

    log.atInfo()
      .setMessage("Bank account closed successfully.")
      .addKeyValue("id", bankAccount.getId())
      .log();
  }
}