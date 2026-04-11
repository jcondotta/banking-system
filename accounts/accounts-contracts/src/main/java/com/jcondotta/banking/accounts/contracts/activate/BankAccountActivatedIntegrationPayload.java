package com.jcondotta.banking.accounts.contracts.activate;

import java.util.UUID;

public record BankAccountActivatedIntegrationPayload(UUID bankAccountId) {
}