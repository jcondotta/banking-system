package com.jcondotta.banking.transfers.infrastructure.adapters.input.rest.request_internal;

import com.jcondotta.application.command.CommandHandlerWithResult;
import com.jcondotta.banking.transfers.application.bank_transfer.command.request_internal.model.RequestInternalTransferCommand;
import com.jcondotta.banking.transfers.domain.bank_account.identity.BankAccountId;
import com.jcondotta.banking.transfers.domain.bank_account.value_objects.Iban;
import com.jcondotta.banking.transfers.domain.bank_transfer.identity.BankTransferId;
import com.jcondotta.banking.transfers.domain.bank_transfer.value_objects.party.PartyName;
import com.jcondotta.banking.transfers.domain.monetary_movement.value_objects.MonetaryAmount;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
class RequestInternalTransferController {

  private final CommandHandlerWithResult<RequestInternalTransferCommand, BankTransferId> commandHandler;

  @PostMapping("${app.api.transfers.internal-transfer-path:/api/transfers/internal}")
  @ResponseStatus(HttpStatus.ACCEPTED)
  RequestInternalTransferResponse requestInternalTransfer(@RequestBody RequestInternalTransferRequest request) {
    var command = new RequestInternalTransferCommand(
      BankAccountId.of(request.senderAccountId()),
      PartyName.of(request.recipientName()),
      Iban.of(request.recipientIban()),
      MonetaryAmount.of(request.amount(), request.currency()),
      request.reference()
    );

    var bankTransferId = commandHandler.handle(command);

    return new RequestInternalTransferResponse(bankTransferId.value());
  }
}
