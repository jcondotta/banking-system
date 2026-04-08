package com.jcondotta.banking.contracts.unblock;

import java.util.UUID;

public record BankAccountUnblockedIntegrationPayload(UUID bankAccountId) {
}
