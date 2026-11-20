package com.jcondotta.banking.transfers.domain.bank_transfer.value_objects.party;

public sealed interface PartySender
    extends Party
    permits InternalAccountSender, ExternalPartySender {
}
