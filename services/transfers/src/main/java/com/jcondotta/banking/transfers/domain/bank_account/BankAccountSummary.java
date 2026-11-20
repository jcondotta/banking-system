package com.jcondotta.banking.transfers.domain.bank_account;

import com.jcondotta.banking.transfers.domain.bank_account.enums.BankAccountStatus;
import com.jcondotta.banking.transfers.domain.bank_account.identity.BankAccountId;

import static com.jcondotta.domain.support.Preconditions.required;

public record BankAccountSummary(BankAccountId bankAccountId, BankAccountStatus status) {

    public BankAccountSummary {
        required(bankAccountId, "bank account id must be provided");
        required(status, "bank account status must be provided");
    }
}
