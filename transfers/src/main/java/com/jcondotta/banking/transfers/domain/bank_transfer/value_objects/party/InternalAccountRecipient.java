package com.jcondotta.banking.transfers.domain.bank_transfer.value_objects.party;

import com.jcondotta.banking.transfers.domain.bank_account.identity.BankAccountId;
import com.jcondotta.domain.support.Preconditions;

import java.util.UUID;

public record InternalAccountRecipient(BankAccountId bankAccountId) implements PartyRecipient {

    public static final String RECIPIENT_ACCOUNT_ID_NOT_PROVIDED = "recipient account id must be provided.";

    public InternalAccountRecipient {
        Preconditions.required(bankAccountId, RECIPIENT_ACCOUNT_ID_NOT_PROVIDED);
    }

    public static InternalAccountRecipient of(BankAccountId bankAccountId) {
        return new InternalAccountRecipient(bankAccountId);
    }

    public static InternalAccountRecipient of(UUID bankAccountId) {
        return of(BankAccountId.of(bankAccountId));
    }

    public static InternalAccountRecipient of(String bankAccountId) {
        return of(UUID.fromString(bankAccountId));
    }
}
