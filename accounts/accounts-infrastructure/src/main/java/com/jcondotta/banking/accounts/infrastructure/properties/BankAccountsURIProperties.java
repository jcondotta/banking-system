package com.jcondotta.banking.accounts.infrastructure.properties;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.net.URI;
import java.util.UUID;

@Validated
@ConfigurationProperties(prefix = "api.bank-accounts")
public record BankAccountsURIProperties(@NotBlank String rootPath, @NotBlank String bankAccountIdPath) {

  public URI bankAccountURI(UUID bankAccountId) {
    return URI.create(bankAccountExpanded(bankAccountId));
  }

  private String bankAccountExpanded(UUID bankAccountId) {
    return bankAccountIdPath.replace("{bank-account-id}", bankAccountId.toString());
  }
}
