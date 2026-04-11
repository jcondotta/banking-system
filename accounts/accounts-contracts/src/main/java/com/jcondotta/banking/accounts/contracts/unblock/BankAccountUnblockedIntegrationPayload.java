package com.jcondotta.banking.accounts.contracts.unblock;

import java.util.UUID;

public record BankAccountUnblockedIntegrationPayload(UUID bankAccountId) {
}
