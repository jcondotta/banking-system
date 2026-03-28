package com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.open.model;

import com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.common.AccountHolderRequest;
import com.jcondotta.banking.accounts.domain.bankaccount.enums.AccountType;
import com.jcondotta.banking.accounts.domain.bankaccount.enums.Currency;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

@Schema(description = "Request object used for creating a new bank account with a primary account holder.")
public record OpenBankAccountRequest(

    @NotNull
    @Schema(description = "Type of bank account (e.g., SAVINGS, CHECKING)", example = "SAVINGS",
        requiredMode = RequiredMode.REQUIRED, allowableValues = { "SAVINGS", "CHECKING "})
    AccountType accountType,

    @NotNull
    @Schema(description = "Currency for the bank account (e.g., USD, EUR)", example = "USD", requiredMode = RequiredMode.REQUIRED)
    Currency currency,

    @Valid
    @NotNull
    @Schema(
      description = "Account holder information required to open the bank account.",
      requiredMode = Schema.RequiredMode.REQUIRED
    )
    AccountHolderRequest primaryHolder
){
}
