package com.jcondotta.banking.transfers.domain.bank_transfer.value_objects.transfer_entry;

import com.jcondotta.banking.transfers.domain.bank_transfer.value_objects.party.PartyRecipient;
import com.jcondotta.banking.transfers.domain.bank_transfer.value_objects.party.PartySender;
import com.jcondotta.banking.money.Currency;
import com.jcondotta.banking.transfers.domain.movement.Movement;
import com.jcondotta.banking.transfers.domain.movement.MovementType;

import java.math.BigDecimal;

public sealed interface TransferEntry permits InternalTransferEntry {

    String MONETARY_MOVEMENT_NOT_PROVIDED = "monetary movement must be provided";

    PartySender partySender();
    PartyRecipient partyRecipient();
    Movement movement();

    default BigDecimal amount() {
        return movement().amount();
    }

    default Currency currency() {
        return movement().currency();
    }

    default MovementType movementType() {
        return movement().movementType();
    }

    default boolean isDebit() {
        return movement().isDebit();
    }

    default boolean isCredit() {
        return movement().isCredit();
    }
}
