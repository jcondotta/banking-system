package com.jcondotta.banking.transfers.domain.bank_transfer.events;

import com.jcondotta.banking.transfers.domain.bank_account.identity.BankAccountId;
import com.jcondotta.banking.transfers.domain.bank_transfer.validation.BankTransferErrors;
import com.jcondotta.banking.transfers.domain.monetary_movement.value_objects.MonetaryAmount;

import static com.jcondotta.domain.support.Preconditions.required;

public record InternalTransferRequestedData(
    BankAccountId senderAccountId,
    BankAccountId recipientAccountId,
    MonetaryAmount monetaryAmount,
    String reference
) {

    public InternalTransferRequestedData {
        required(senderAccountId, BankTransferErrors.SENDER_ACCOUNT_ID_MUST_BE_PROVIDED);
        required(recipientAccountId, BankTransferErrors.RECIPIENT_ACCOUNT_ID_MUST_BE_PROVIDED);
        required(monetaryAmount, BankTransferErrors.MONETARY_AMOUNT_MUST_BE_PROVIDED);
    }
}
