package com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.lookupbankaccount;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(name = "ContactInfoResponse", description = "Contact information of the account holder.")
public record ContactInfoResponse(

  @NotBlank
  @Schema(
    description = "Email address of the account holder.",
    example = "jefferson.condotta@email.com"
  )
  String email,

  @NotBlank
  @Schema(
    description = "Phone number of the account holder.",
    example = "+34600111222"
  )
  String phoneNumber
) {}