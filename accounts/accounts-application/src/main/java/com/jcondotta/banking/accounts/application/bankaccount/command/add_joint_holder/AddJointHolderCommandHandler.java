package com.jcondotta.banking.accounts.application.bankaccount.command.add_joint_holder;

import com.jcondotta.application.command.CommandHandler;
import com.jcondotta.banking.accounts.application.bankaccount.command.add_joint_holder.model.AddJointHolderCommand;
import com.jcondotta.banking.accounts.domain.bankaccount.exceptions.BankAccountNotFoundException;
import com.jcondotta.banking.accounts.domain.bankaccount.repository.BankAccountRepository;
import io.micrometer.observation.annotation.Observed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AddJointHolderCommandHandler implements CommandHandler<AddJointHolderCommand> {

  private final BankAccountRepository bankAccountRepository;

  @Override
  @Observed(
    name = "bankaccount.addJointHolder",
    contextualName = "addJointHolder",
    lowCardinalityKeyValues = {
      "aggregate", "bankAccount",
      "operation", "addJointHolder"
    }
  )
  public void handle(AddJointHolderCommand command) {

    var bankAccount = bankAccountRepository.findById(command.bankAccountId())
      .orElseThrow(() -> new BankAccountNotFoundException(command.bankAccountId()));

    bankAccount.addJointHolder(
      command.personalInfo(),
      command.contactInfo(),
      command.address()
    );

    bankAccountRepository.save(bankAccount);

    log.atInfo()
      .setMessage("Joint account holder added successfully.")
      .addKeyValue("id", bankAccount.getId())
      .log();
  }
}