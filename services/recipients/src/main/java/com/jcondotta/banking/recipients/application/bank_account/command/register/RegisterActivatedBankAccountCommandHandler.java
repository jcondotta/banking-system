package com.jcondotta.banking.recipients.application.bank_account.command.register;

import com.jcondotta.application.command.CommandHandler;
import com.jcondotta.banking.recipients.domain.bank_account.BankAccount;
import com.jcondotta.banking.recipients.domain.bank_account.repository.BankAccountRepository;
import org.springframework.stereotype.Component;

@Component
public class RegisterActivatedBankAccountCommandHandler implements CommandHandler<RegisterActivatedBankAccountCommand> {

  private final BankAccountRepository bankAccountRepository;

  public RegisterActivatedBankAccountCommandHandler(BankAccountRepository bankAccountRepository) {
    this.bankAccountRepository = bankAccountRepository;
  }

  @Override
  public void handle(RegisterActivatedBankAccountCommand command) {
    bankAccountRepository.registerIfAbsent(BankAccount.active(command.bankAccountId()));
  }
}
