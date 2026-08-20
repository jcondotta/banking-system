package com.jcondotta.banking.transfers.domain.bank_transfer.value_objects.party;

import com.jcondotta.banking.transfers.domain.bank_account.identity.BankAccountId;
import com.jcondotta.domain.support.Preconditions;

import java.util.UUID;

public record InternalAccountSender(BankAccountId bankAccountId) implements PartySender {

    public static final String SENDER_ACCOUNT_ID_NOT_PROVIDED = "sender account id must be provided.";

    public InternalAccountSender {
        Preconditions.required(bankAccountId, SENDER_ACCOUNT_ID_NOT_PROVIDED);
    }

    public static InternalAccountSender of(BankAccountId bankAccountId) {
        return new InternalAccountSender(bankAccountId);
    }

    public static InternalAccountSender of(UUID bankAccountId) {
        return new InternalAccountSender(BankAccountId.of(bankAccountId));
    }
}
