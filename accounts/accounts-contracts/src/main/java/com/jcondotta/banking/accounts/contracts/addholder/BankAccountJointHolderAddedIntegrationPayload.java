package com.jcondotta.banking.accounts.contracts.addholder;

import java.util.UUID;

public record BankAccountJointHolderAddedIntegrationPayload(UUID bankAccountId, UUID accountHolderId) {
}
