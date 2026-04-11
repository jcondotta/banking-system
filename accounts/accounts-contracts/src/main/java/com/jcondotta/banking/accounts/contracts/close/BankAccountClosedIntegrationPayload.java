package com.jcondotta.banking.accounts.contracts.close;

import java.util.UUID;

public record BankAccountClosedIntegrationPayload(UUID bankAccountId) {
}
