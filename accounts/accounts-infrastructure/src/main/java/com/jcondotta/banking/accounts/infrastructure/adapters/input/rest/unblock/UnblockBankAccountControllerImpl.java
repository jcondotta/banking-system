package com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.unblock;

import com.jcondotta.application.core.CommandHandler;
import com.jcondotta.banking.accounts.application.bankaccount.command.unblock.model.UnblockBankAccountCommand;
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
public class UnblockBankAccountControllerImpl implements UnblockBankAccountController {

  private final CommandHandler<UnblockBankAccountCommand> commandHandler;

  @Override
  public ResponseEntity<Void> unblock(UUID bankAccountId) {
    commandHandler.handle(new UnblockBankAccountCommand(BankAccountId.of(bankAccountId)));
    return ResponseEntity.noContent().build();
  }
}