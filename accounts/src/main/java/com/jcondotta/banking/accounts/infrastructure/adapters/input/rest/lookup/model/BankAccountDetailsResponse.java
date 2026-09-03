package com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.lookup.model;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

@Schema(name = "BankAccountDetailsResponse", description = "Represents the details of a bank account.")
public record BankAccountDetailsResponse(

  @Schema(description = "The UUID value representing the bank account identifier.",
    example = "01920bff-1338-7efd-ade6-e9128debe5d4",
    requiredMode = RequiredMode.REQUIRED)
  UUID id,

  @Schema(description = "Type of bank account (e.g., SAVINGS, CHECKING)", example = "SAVINGS",
    requiredMode = RequiredMode.REQUIRED)
  AccountTypeResponse accountType,

  @Schema(description = "Currency for the bank account (e.g., USD, EUR)", example = "USD", requiredMode = RequiredMode.REQUIRED)
  CurrencyResponse currency,

  @Schema(description = "International Bank Account Number (IBAN) for the bank account.",
    example = "GB29NWBK60161331926819",
    maxLength = 34,
    requiredMode = RequiredMode.REQUIRED)
  String iban,

  @Schema(
    description = "Date and time when the bank account was opened (UTC).",
    example = "2023-08-23T12:55:00Z",
    requiredMode = RequiredMode.REQUIRED
  )
  Instant createdAt,

  @Schema(description = "Current accountStatus of the bank account",
    requiredMode = RequiredMode.REQUIRED)
  AccountStatusResponse accountStatus,

  @Schema(description = "Account holders associated with this bank account.", requiredMode = RequiredMode.REQUIRED)
  List<AccountHolderDetailsResponse> holders
) {
}
