package com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.block;

import com.jcondotta.application.command.CommandHandler;
import com.jcondotta.banking.accounts.application.bankaccount.command.block.model.BlockBankAccountCommand;
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
public class BlockBankAccountControllerImpl implements BlockBankAccountController {

  private final CommandHandler<BlockBankAccountCommand> commandHandler;

  @Override
  public ResponseEntity<Void> block(UUID bankAccountId) {
    commandHandler.handle(new BlockBankAccountCommand(BankAccountId.of(bankAccountId)));
    return ResponseEntity.noContent().build();
  }
}