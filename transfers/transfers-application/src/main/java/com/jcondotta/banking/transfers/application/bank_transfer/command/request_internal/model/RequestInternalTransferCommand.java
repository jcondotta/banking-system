package com.jcondotta.banking.transfers.application.bank_transfer.command.request_internal.model;

import com.jcondotta.application.command.Command;
import com.jcondotta.banking.transfers.domain.bank_account.identity.BankAccountId;
import com.jcondotta.banking.transfers.domain.bank_account.value_objects.Iban;
import com.jcondotta.banking.transfers.domain.bank_transfer.identity.BankTransferId;
import com.jcondotta.banking.transfers.domain.bank_transfer.value_objects.party.PartyName;
import com.jcondotta.banking.transfers.domain.monetary_movement.value_objects.MonetaryAmount;

import static java.util.Objects.requireNonNull;

public record RequestInternalTransferCommand(
  BankAccountId senderAccountId,
  PartyName recipientName,
  Iban recipientIban,
  MonetaryAmount monetaryAmount,
  String reference
) implements Command<BankTransferId> {

  public static final String SENDER_ACCOUNT_ID_REQUIRED = "senderAccountId must be provided";
  public static final String RECIPIENT_NAME_REQUIRED = "recipientName must be provided";
  public static final String RECIPIENT_IBAN_REQUIRED = "recipientIban must be provided";
  public static final String MONETARY_AMOUNT_REQUIRED = "monetaryAmount must be provided";

  public RequestInternalTransferCommand {
    requireNonNull(senderAccountId, SENDER_ACCOUNT_ID_REQUIRED);
    requireNonNull(recipientName, RECIPIENT_NAME_REQUIRED);
    requireNonNull(recipientIban, RECIPIENT_IBAN_REQUIRED);
    requireNonNull(monetaryAmount, MONETARY_AMOUNT_REQUIRED);
  }
}
