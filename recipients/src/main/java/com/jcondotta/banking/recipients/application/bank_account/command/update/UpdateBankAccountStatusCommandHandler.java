package com.jcondotta.banking.recipients.application.bank_account.command.update;

import com.jcondotta.application.command.CommandHandler;
import com.jcondotta.banking.recipients.domain.bank_account.BankAccount;
import com.jcondotta.banking.recipients.domain.bank_account.repository.BankAccountRepository;
import org.springframework.stereotype.Component;

@Component
public class UpdateBankAccountStatusCommandHandler implements CommandHandler<UpdateBankAccountStatusCommand> {

  private final BankAccountRepository bankAccountRepository;

  public UpdateBankAccountStatusCommandHandler(BankAccountRepository bankAccountRepository) {
    this.bankAccountRepository = bankAccountRepository;
  }

  @Override
  public void handle(UpdateBankAccountStatusCommand command) {
    bankAccountRepository.save(new BankAccount(command.bankAccountId(), command.status()));
  }
}
