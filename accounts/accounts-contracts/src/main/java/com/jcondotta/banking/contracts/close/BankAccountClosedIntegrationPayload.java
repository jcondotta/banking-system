package com.jcondotta.banking.contracts.close;

import java.util.UUID;

public record BankAccountClosedIntegrationPayload(UUID bankAccountId) {
}
