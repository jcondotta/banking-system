package com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.open;

import com.jcondotta.application.command.CommandHandlerWithResult;
import com.jcondotta.banking.accounts.application.bankaccount.command.open.model.OpenBankAccountCommand;
import com.jcondotta.banking.accounts.domain.bankaccount.identity.BankAccountId;
import com.jcondotta.banking.accounts.application.common.idempotency.IdempotencyKey;
import com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.open.mapper.OpenBankAccountRequestControllerMapper;
import com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.open.model.OpenBankAccountRequest;
import com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.properties.BankAccountsURIProperties;
import io.micrometer.core.annotation.Timed;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Validated
@RestController
@AllArgsConstructor
public class OpenBankAccountControllerImpl implements OpenBankAccountController {

  private final CommandHandlerWithResult<OpenBankAccountCommand, BankAccountId> commandHandler;
  private final OpenBankAccountRequestControllerMapper requestMapper;
  private final BankAccountsURIProperties uriProperties;

  @Override
  @Timed(
      value = "bankAccounts.open.time",
      description = "bank account opening time measurement",
      percentiles = {0.5, 0.95, 0.99})
  public ResponseEntity<Void> openBankAccount(OpenBankAccountRequest request, UUID idempotencyKey) {
    var command = requestMapper.toCommand(request, IdempotencyKey.of(idempotencyKey));
    var bankAccountId = commandHandler.handle(command);

    return ResponseEntity
      .created(uriProperties.bankAccountURI(bankAccountId.value()))
      .build();
  }
}
