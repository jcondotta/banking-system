package com.jcondotta.banking.ledger.application.ledger_account.command.apply_transfer;

import com.jcondotta.application.command.Command;
import com.jcondotta.banking.movement.MovementAmount;

import java.time.Instant;
import java.util.UUID;

public record ApplyInternalTransferCommand(
        UUID transferId,
        UUID senderAccountId,
        UUID recipientAccountId,
        MovementAmount movementAmount,
        Instant requestedAt
) implements Command {}
