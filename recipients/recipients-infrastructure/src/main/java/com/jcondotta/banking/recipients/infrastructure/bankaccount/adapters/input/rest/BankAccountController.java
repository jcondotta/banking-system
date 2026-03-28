package com.jcondotta.banking.recipients.infrastructure.bankaccount.adapters.input.rest;

import com.jcondotta.application.core.command.CommandHandler;
import com.jcondotta.banking.recipients.application.bankaccount.command.register.RegisterBankAccountCommand;
import com.jcondotta.banking.recipients.domain.recipient.identity.BankAccountId;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/bank-accounts")
@RequiredArgsConstructor
public class BankAccountController {

  private final CommandHandler<RegisterBankAccountCommand> commandHandler;

  @PostMapping
  public UUID createBankAccount() {

    var bankAccountId = BankAccountId.of(UUID.randomUUID());

    commandHandler.handle(new RegisterBankAccountCommand(bankAccountId));

    return bankAccountId.value();
  }
}