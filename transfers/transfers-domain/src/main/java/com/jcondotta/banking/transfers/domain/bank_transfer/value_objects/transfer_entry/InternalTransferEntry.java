package com.jcondotta.banking.transfers.domain.bank_transfer.value_objects.transfer_entry;

import com.jcondotta.banking.transfers.domain.bank_account.identity.BankAccountId;
import com.jcondotta.banking.transfers.domain.bank_transfer.exceptions.IdenticalInternalPartiesException;
import com.jcondotta.banking.transfers.domain.bank_transfer.value_objects.party.InternalAccountRecipient;
import com.jcondotta.banking.transfers.domain.bank_transfer.value_objects.party.InternalAccountSender;
import com.jcondotta.banking.transfers.domain.monetary_movement.enums.MovementType;
import com.jcondotta.banking.transfers.domain.monetary_movement.value_objects.MonetaryAmount;
import com.jcondotta.banking.transfers.domain.monetary_movement.value_objects.MonetaryMovement;

import static com.jcondotta.domain.support.Preconditions.required;

public record InternalTransferEntry(InternalAccountSender partySender, InternalAccountRecipient partyRecipient, MonetaryMovement monetaryMovement)
    implements TransferEntry {

    public InternalTransferEntry {
        required(partySender, InternalAccountSender.SENDER_ACCOUNT_ID_NOT_PROVIDED);
        required(partyRecipient, InternalAccountRecipient.RECIPIENT_ACCOUNT_ID_NOT_PROVIDED);
        required(monetaryMovement, MONETARY_MOVEMENT_NOT_PROVIDED);

        if (partySender.bankAccountId().equals(partyRecipient.bankAccountId())) {
            throw new IdenticalInternalPartiesException();
        }
    }

    public static InternalTransferEntry of(BankAccountId senderAccountId, BankAccountId recipientAccountId, MovementType movementType, MonetaryAmount monetaryAmount) {
        return new InternalTransferEntry(
            InternalAccountSender.of(senderAccountId),
            InternalAccountRecipient.of(recipientAccountId),
            MonetaryMovement.of(movementType, monetaryAmount)
        );
    }

    public static InternalTransferEntry ofDebit(BankAccountId senderAccountId, BankAccountId recipientAccountId, MonetaryAmount monetaryAmount) {
        return of(senderAccountId, recipientAccountId, MovementType.DEBIT, monetaryAmount);
    }

    public static InternalTransferEntry ofCredit(BankAccountId senderAccountId, BankAccountId recipientAccountId, MonetaryAmount monetaryAmount) {
        return of(senderAccountId, recipientAccountId, MovementType.CREDIT, monetaryAmount);
    }
}
