package com.jcondotta.banking.transfers.domain.bank_account.exceptions;

import com.jcondotta.banking.transfers.domain.bank_account.identity.BankAccountId;
import com.jcondotta.domain.exception.DomainNotFoundException;

public final class SenderBankAccountNotFoundException extends DomainNotFoundException {

    public static final String MESSAGE = "Sender bank account not found";

    private final BankAccountId bankAccountId;

    public SenderBankAccountNotFoundException(BankAccountId bankAccountId) {
        super(MESSAGE);
        this.bankAccountId = bankAccountId;
    }

    public BankAccountId getBankAccountId() {
        return bankAccountId;
    }
}
