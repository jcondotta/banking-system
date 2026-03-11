package com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.common;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record IdentityDocumentRequest(

  @NotBlank
  @Schema(
    description = "Type of identity document.",
    example = "NATIONAL_ID",
    allowableValues = {"NATIONAL_ID", "FOREIGNER_ID"},
    requiredMode = Schema.RequiredMode.REQUIRED
  )
  String type,

  @NotBlank
  @Schema(
    description = "Country that issued the identity document.",
    example = "SPAIN",
    allowableValues = {"SPAIN"},
    requiredMode = Schema.RequiredMode.REQUIRED
  )
  String country,

  @NotBlank
  @Schema(
    description = "Document number.",
    example = "12345678A",
    requiredMode = Schema.RequiredMode.REQUIRED
  )
  String number
) {
}