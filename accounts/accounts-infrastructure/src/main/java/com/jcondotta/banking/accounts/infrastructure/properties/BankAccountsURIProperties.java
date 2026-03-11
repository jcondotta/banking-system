package com.jcondotta.banking.accounts.infrastructure.properties;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.net.URI;
import java.util.UUID;

@Validated
@ConfigurationProperties(prefix = "api.v1.bank-accounts")
public record BankAccountsURIProperties(@NotBlank String rootPath, @NotBlank String bankAccountIdPath) {

  public URI bankAccountURI(UUID bankAccountId) {
    return URI.create(bankAccountExpanded(bankAccountId));
  }

  public URI accountHoldersURI(UUID bankAccountId) {
    return URI.create(bankAccountExpanded(bankAccountId) + "/account-holders");
  }

  public URI activateBankAccountURI(UUID bankAccountId) {
    return URI.create(bankAccountExpanded(bankAccountId) + "/activate");
  }

  public URI blockBankAccountURI(UUID bankAccountId) {
    return URI.create(bankAccountExpanded(bankAccountId) + "/block");
  }

  public URI unblockBankAccountURI(UUID bankAccountId) {
    return URI.create(bankAccountExpanded(bankAccountId) + "/unblock");
  }

  public URI closeBankAccountURI(UUID bankAccountId) {
    return URI.create(bankAccountExpanded(bankAccountId) + "/close");
  }

  private String bankAccountExpanded(UUID bankAccountId) {
    return bankAccountIdPath.replace("{bank-account-id}", bankAccountId.toString());
  }
}
