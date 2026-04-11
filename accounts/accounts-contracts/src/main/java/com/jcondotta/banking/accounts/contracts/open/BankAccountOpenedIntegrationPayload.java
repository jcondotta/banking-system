package com.jcondotta.banking.accounts.contracts.open;

import java.util.UUID;

public record BankAccountOpenedIntegrationPayload(
  UUID bankAccountId,
  String accountType,
  String currency,
  UUID accountHolderId
) {
}