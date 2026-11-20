package com.jcondotta.banking.transfers.domain.bank_account.exceptions;

import com.jcondotta.banking.transfers.domain.bank_account.enums.BankAccountStatus;
import com.jcondotta.domain.exception.DomainRuleViolationException;

public final class SenderBankAccountNotActiveException extends DomainRuleViolationException {

    public static final String MESSAGE = "Sender bank account is not active";

    private final BankAccountStatus status;

    public SenderBankAccountNotActiveException(BankAccountStatus status) {
        super(MESSAGE);
        this.status = status;
    }

    public BankAccountStatus getStatus() {
        return status;
    }
}
