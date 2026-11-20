package com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.common;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record IdentityDocumentRequest(

  @NotNull
  @Schema(
    description = "Type of identity document.",
    example = "NATIONAL_ID",
    requiredMode = Schema.RequiredMode.REQUIRED
  )
  DocumentTypeRequest type,

  @NotNull
  @Schema(
    description = "Country that issued the identity document.",
    example = "SPAIN",
    requiredMode = Schema.RequiredMode.REQUIRED
  )
  DocumentCountryRequest country,

  @NotBlank
  @Size(max = 40)
  @Schema(
    description = "Document number.",
    example = "12345678A",
    requiredMode = Schema.RequiredMode.REQUIRED
  )
  String number
) {
}