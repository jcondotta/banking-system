package com.jcondotta.banking.contracts.addholder;

import java.util.UUID;

public record BankAccountJointHolderAddedIntegrationPayload(UUID bankAccountId, UUID accountHolderId) {
}
