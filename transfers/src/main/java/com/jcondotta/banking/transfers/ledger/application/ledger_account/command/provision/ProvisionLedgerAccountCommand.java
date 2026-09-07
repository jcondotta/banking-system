package com.jcondotta.banking.transfers.ledger.application.ledger_account.command.provision;

import com.jcondotta.application.command.Command;

import java.time.Instant;
import java.util.UUID;

public record ProvisionLedgerAccountCommand(
        UUID accountId,
        String iban,
        String currency,
        Instant activatedAt
) implements Command {}
