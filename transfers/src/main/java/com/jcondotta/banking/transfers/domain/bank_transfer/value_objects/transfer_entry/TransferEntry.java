package com.jcondotta.banking.transfers.domain.bank_transfer.value_objects.transfer_entry;

import com.jcondotta.banking.transfers.domain.bank_transfer.value_objects.party.PartyRecipient;
import com.jcondotta.banking.transfers.domain.bank_transfer.value_objects.party.PartySender;
import com.jcondotta.banking.money.Currency;
import com.jcondotta.banking.money.MonetaryMovement;
import com.jcondotta.banking.money.MovementType;

import java.math.BigDecimal;

public sealed interface TransferEntry permits InternalTransferEntry {

    String MONETARY_MOVEMENT_NOT_PROVIDED = "monetary movement must be provided.";

    PartySender partySender();
    PartyRecipient partyRecipient();
    MonetaryMovement monetaryMovement();

    default BigDecimal amount() {
        return monetaryMovement().amount();
    }

    default Currency currency() {
        return monetaryMovement().currency();
    }

    default MovementType movementType() {
        return monetaryMovement().movementType();
    }

    default boolean isDebit() {
        return monetaryMovement().isDebit();
    }

    default boolean isCredit() {
        return monetaryMovement().isCredit();
    }
}
