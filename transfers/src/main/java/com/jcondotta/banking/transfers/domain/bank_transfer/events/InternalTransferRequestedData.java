package com.jcondotta.banking.transfers.domain.bank_transfer.events;

import com.jcondotta.banking.money.Currency;
import com.jcondotta.banking.transfers.domain.bank_account.identity.BankAccountId;
import com.jcondotta.banking.transfers.domain.bank_transfer.validation.BankTransferErrors;

import java.math.BigDecimal;

import static com.jcondotta.domain.support.Preconditions.required;

public record InternalTransferRequestedData(
    BankAccountId senderAccountId,
    BankAccountId recipientAccountId,
    BigDecimal amount,
    Currency currency,
    String reference
) {

    public InternalTransferRequestedData {
        required(senderAccountId, BankTransferErrors.SENDER_ACCOUNT_ID_MUST_BE_PROVIDED);
        required(recipientAccountId, BankTransferErrors.RECIPIENT_ACCOUNT_ID_MUST_BE_PROVIDED);
        required(amount, BankTransferErrors.AMOUNT_MUST_BE_PROVIDED);
        required(currency, BankTransferErrors.CURRENCY_MUST_BE_PROVIDED);
    }
}
