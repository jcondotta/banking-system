package com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.common;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

@Schema(description = "Personal information of the account holder.")
public record PersonalInfoRequest(

  @NotBlank
  @Size(max = 255)
  @Schema(
    description = "First name of the account holder.",
    example = "Jefferson",
    requiredMode = Schema.RequiredMode.REQUIRED
  )
  String firstName,

  @NotBlank
  @Size(max = 255)
  @Schema(
    description = "Last name of the account holder.",
    example = "Condotta",
    requiredMode = Schema.RequiredMode.REQUIRED
  )
  String lastName,

  @Valid
  @NotNull
  @Schema(
    description = "Identity document information.",
    requiredMode = Schema.RequiredMode.REQUIRED
  )
  IdentityDocumentRequest identityDocument,

  @NotNull
  @PastOrPresent
  @JsonFormat(pattern = "yyyy-MM-dd")
  @Schema(
    description = "Date of birth of the account holder.",
    example = "1988-02-01",
    requiredMode = Schema.RequiredMode.REQUIRED
  )
  LocalDate dateOfBirth

) {
}