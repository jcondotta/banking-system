package com.jcondotta.banking.contracts.activate;

import java.util.UUID;

public record BankAccountActivatedIntegrationPayload(UUID bankAccountId) {
}