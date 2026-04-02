package com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.close;

import com.jcondotta.application.command.CommandHandler;
import com.jcondotta.banking.accounts.application.bankaccount.command.close.model.CloseBankAccountCommand;
import com.jcondotta.banking.accounts.domain.bankaccount.identity.BankAccountId;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@Validated
@RestController
@AllArgsConstructor
public class CloseBankAccountControllerImpl implements CloseBankAccountController {

  private final CommandHandler<CloseBankAccountCommand> commandHandler;

  @Override
  public ResponseEntity<Void> close(UUID bankAccountId) {
    commandHandler.handle(new CloseBankAccountCommand(BankAccountId.of(bankAccountId)));
    return ResponseEntity.noContent().build();
  }
}