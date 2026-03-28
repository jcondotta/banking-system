package com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.lookup;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@Schema(description = "Personal information of the account holder.")
public record PersonalInfoResponse(

  @NotBlank
  @Schema(
    description = "First name of the account holder.",
    example = "Jefferson",
    requiredMode = Schema.RequiredMode.REQUIRED
  )
  String firstName,

  @NotBlank
  @Schema(
    description = "Last name of the account holder.",
    example = "Condotta",
    requiredMode = Schema.RequiredMode.REQUIRED
  )
  String lastName,

  @NotNull
  @Schema(
    description = "Identity document of the account holder.",
    requiredMode = Schema.RequiredMode.REQUIRED
  )
  IdentityDocumentResponse identityDocument,

  @NotNull
  @Schema(
    description = "Date of birth of the account holder.",
    example = "1988-02-01",
    requiredMode = Schema.RequiredMode.REQUIRED
  )
  LocalDate dateOfBirth

) {}