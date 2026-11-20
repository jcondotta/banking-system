package com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.activate;

import com.jcondotta.application.command.CommandHandler;
import com.jcondotta.banking.accounts.application.bankaccount.command.activate.model.ActivateBankAccountCommand;
import com.jcondotta.banking.accounts.domain.bankaccount.identity.BankAccountId;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Validated
@RestController
@AllArgsConstructor
public class ActivateBankAccountControllerImpl implements ActivateBankAccountController {

  private final CommandHandler<ActivateBankAccountCommand> commandHandler;

  @Override
  public ResponseEntity<Void> activateBankAccount(@PathVariable("bank-account-id") UUID bankAccountId) {
    commandHandler.handle(new ActivateBankAccountCommand(BankAccountId.of(bankAccountId)));
    return ResponseEntity.noContent().build();
  }
}
