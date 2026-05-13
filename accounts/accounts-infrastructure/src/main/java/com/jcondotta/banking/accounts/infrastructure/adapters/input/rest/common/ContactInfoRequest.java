package com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.common;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Contact information of the account holder.")
public record ContactInfoRequest(

  @NotBlank
  @Email
  @Size(max = 100)
  @Schema(
    description = "Email address of the account holder.",
    example = "jefferson.condotta@email.com",
    requiredMode = Schema.RequiredMode.REQUIRED
  )
  String email,

  @NotBlank
  @Schema(
    description = "Phone number of the account holder.",
    example = "+49123456789",
    requiredMode = Schema.RequiredMode.REQUIRED
  )
  String phoneNumber

) {
}