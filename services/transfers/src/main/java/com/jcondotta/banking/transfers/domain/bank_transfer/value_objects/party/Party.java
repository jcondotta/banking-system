package com.jcondotta.banking.transfers.domain.bank_transfer.value_objects.party;

public sealed interface Party permits PartySender, PartyRecipient {
}
