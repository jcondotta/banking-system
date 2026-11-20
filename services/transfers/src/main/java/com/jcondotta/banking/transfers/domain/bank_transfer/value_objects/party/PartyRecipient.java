package com.jcondotta.banking.transfers.domain.bank_transfer.value_objects.party;

public sealed interface PartyRecipient
    extends Party
    permits InternalAccountRecipient, ExternalPartyRecipient {
}
