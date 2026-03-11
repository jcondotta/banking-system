package com.jcondotta.banking.accounts.application.bankaccount.command.unblock;

import com.jcondotta.application.core.CommandHandler;
import com.jcondotta.banking.accounts.application.bankaccount.command.unblock.model.UnblockBankAccountCommand;
import com.jcondotta.banking.accounts.domain.bankaccount.exceptions.BankAccountNotFoundException;
import com.jcondotta.banking.accounts.domain.bankaccount.repository.BankAccountRepository;
import io.micrometer.observation.annotation.Observed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UnblockBankAccountCommandHandler implements CommandHandler<UnblockBankAccountCommand> {

  private final BankAccountRepository bankAccountRepository;

  @Override
  @Observed(
    name = "bankaccount.unblock",
    contextualName = "unblockBankAccount",
    lowCardinalityKeyValues = {
      "aggregate", "bankAccount",
      "operation", "unblock"
    }
  )
  public void handle(UnblockBankAccountCommand command) {

    var bankAccount = bankAccountRepository.findById(command.bankAccountId())
      .orElseThrow(() -> new BankAccountNotFoundException(command.bankAccountId()));

    bankAccount.unblock();

    bankAccountRepository.save(bankAccount);

    log.atInfo()
      .setMessage("Bank account unblocked successfully.")
      .addKeyValue("id", bankAccount.getId())
      .log();
  }
}