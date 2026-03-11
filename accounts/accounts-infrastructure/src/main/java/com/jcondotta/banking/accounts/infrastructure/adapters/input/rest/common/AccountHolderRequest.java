package com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.common;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@Schema(
  name = "AccountHolderRequest",
  description = "Represents the data of an account holder."
)
public record AccountHolderRequest(

  @Valid
  @NotNull
  @Schema(
    description = "Personal information of the account holder.",
    requiredMode = Schema.RequiredMode.REQUIRED
  )
  PersonalInfoRequest personalInfo,

  @Valid
  @NotNull
  @Schema(
    description = "Contact information of the account holder.",
    requiredMode = Schema.RequiredMode.REQUIRED
  )
  ContactInfoRequest contactInfo,

  @Valid
  @NotNull
  @Schema(
    description = "Address of the account holder.",
    requiredMode = Schema.RequiredMode.REQUIRED
  )
  AddressRequest address

) {}