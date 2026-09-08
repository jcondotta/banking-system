package com.jcondotta.banking.transfers.domain.bank_transfer.value_objects.transfer_entry;

import com.jcondotta.banking.transfers.domain.bank_account.identity.BankAccountId;
import com.jcondotta.banking.transfers.domain.bank_transfer.exceptions.IdenticalInternalPartiesException;
import com.jcondotta.banking.transfers.domain.bank_transfer.value_objects.party.InternalAccountRecipient;
import com.jcondotta.banking.transfers.domain.bank_transfer.value_objects.party.InternalAccountSender;
import com.jcondotta.banking.transfers.domain.movement.MovementAmount;
import com.jcondotta.banking.transfers.domain.movement.Movement;
import com.jcondotta.banking.transfers.domain.movement.MovementType;

import static com.jcondotta.domain.support.Preconditions.required;

public record InternalTransferEntry(
  InternalAccountSender partySender,
  InternalAccountRecipient partyRecipient,
  Movement movement)
  implements TransferEntry {

    public InternalTransferEntry {
        required(partySender, InternalAccountSender.SENDER_ACCOUNT_ID_NOT_PROVIDED);
        required(partyRecipient, InternalAccountRecipient.RECIPIENT_ACCOUNT_ID_NOT_PROVIDED);
        required(movement, MONETARY_MOVEMENT_NOT_PROVIDED);

        if (partySender.bankAccountId().equals(partyRecipient.bankAccountId())) {
            throw new IdenticalInternalPartiesException();
        }
    }

    public static InternalTransferEntry of(BankAccountId senderAccountId, BankAccountId recipientAccountId, MovementType movementType, MovementAmount movementAmount) {
        return new InternalTransferEntry(
            InternalAccountSender.of(senderAccountId),
            InternalAccountRecipient.of(recipientAccountId),
            Movement.of(movementType, movementAmount)
        );
    }

    public static InternalTransferEntry ofDebit(BankAccountId senderAccountId, BankAccountId recipientAccountId, MovementAmount movementAmount) {
        return of(senderAccountId, recipientAccountId, MovementType.DEBIT, movementAmount);
    }

    public static InternalTransferEntry ofCredit(BankAccountId senderAccountId, BankAccountId recipientAccountId, MovementAmount movementAmount) {
        return of(senderAccountId, recipientAccountId, MovementType.CREDIT, movementAmount);
    }
}
