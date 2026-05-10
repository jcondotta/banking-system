package com.jcondotta.banking.accounts.contracts.status;

import java.util.UUID;

public record BankAccountStatusChangedIntegrationPayload(
  UUID bankAccountId,
  BankAccountStatus previousStatus,
  BankAccountStatus currentStatus
) {
}
