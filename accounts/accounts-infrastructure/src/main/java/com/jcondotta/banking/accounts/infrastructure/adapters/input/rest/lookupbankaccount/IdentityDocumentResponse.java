package com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.lookupbankaccount;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Identity document of the account holder.")
public record IdentityDocumentResponse(

  @NotNull
  @Schema(
    description = "Country that issued the identity document.",
    example = "SPAIN",
    allowableValues = {"SPAIN"},
    requiredMode = Schema.RequiredMode.REQUIRED
  )
  String country,

  @NotNull
  @Schema(
    description = "Type of identity document.",
    example = "NATIONAL_ID",
    allowableValues = {"NATIONAL_ID", "FOREIGNER_ID"},
    requiredMode = Schema.RequiredMode.REQUIRED
  )
  String type,

  @NotBlank
  @Schema(
    description = "Document number.",
    example = "12345678Z",
    requiredMode = Schema.RequiredMode.REQUIRED
  )
  String number

) {}