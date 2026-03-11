package com.jcondotta.banking.contracts.addholder;

import java.util.UUID;

public record JointHolderAddedIntegrationPayload(UUID bankAccountId, UUID accountHolderId) {
}
