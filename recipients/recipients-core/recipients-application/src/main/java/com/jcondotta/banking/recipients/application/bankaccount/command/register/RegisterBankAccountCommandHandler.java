package com.jcondotta.banking.recipients.application.bankaccount.command.register;

import com.jcondotta.application.command.CommandHandler;
import com.jcondotta.banking.recipients.domain.recipient.aggregate.BankAccount;
import com.jcondotta.banking.recipients.domain.recipient.exceptions.BankAccountNotActiveException;
import com.jcondotta.banking.recipients.domain.recipient.repository.BankAccountRepository;
import io.micrometer.observation.annotation.Observed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RegisterBankAccountCommandHandler implements CommandHandler<RegisterBankAccountCommand> {

  private final BankAccountRepository bankAccountRepository;

  @Override
  @Observed(
    name = "bankaccount.register",
    contextualName = "registerBankAccount",
    lowCardinalityKeyValues = {
      "aggregate", "bankAccount",
      "operation", "register"
    })
  public void handle(RegisterBankAccountCommand command) {
    bankAccountRepository.findById(command.bankAccountId())
      .ifPresentOrElse(
        this::skipIfAlreadyActive,
        () -> register(command)
      );
  }

  private void skipIfAlreadyActive(BankAccount bankAccount) {
    if (!bankAccount.getAccountStatus().isActive()) {
      throw new BankAccountNotActiveException(bankAccount.getAccountStatus());
    }

    log.info(
      "BankAccount already registered - skipping [bankAccountId={}]",
      bankAccount.getId().value()
    );
  }

  private void register(RegisterBankAccountCommand command) {
    var bankAccount = BankAccount.register(command.bankAccountId());
    bankAccountRepository.save(bankAccount);

    log.info(
      "BankAccount registered successfully [bankAccountId={}]",
      command.bankAccountId().value()
    );
  }
}
