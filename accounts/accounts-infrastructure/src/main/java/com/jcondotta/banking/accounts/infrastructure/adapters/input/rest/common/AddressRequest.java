package com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.common;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(name = "AddressRequest", description = "Represents the address information of the account holder.")
public record AddressRequest(

  @NotBlank
  @Size(max = 255)
  @Schema(
    description = "Street name.",
    example = "Carrer de Mallorca",
    requiredMode = Schema.RequiredMode.REQUIRED
  )
  String street,

  @NotBlank
  @Size(max = 20)
  @Schema(
    description = "Street number.",
    example = "401",
    requiredMode = Schema.RequiredMode.REQUIRED
  )
  String streetNumber,

  @Size(max = 255)
  @Schema(
    description = "Additional address complement (apartment, floor, etc).",
    example = "Apartment 4B",
    requiredMode = Schema.RequiredMode.NOT_REQUIRED
  )
  String complement,

  @NotBlank
  @Size(max = 20)
  @Schema(
    description = "Postal code.",
    example = "08013",
    requiredMode = Schema.RequiredMode.REQUIRED
  )
  String postalCode,

  @NotBlank
  @Size(max = 120)
  @Schema(
    description = "City name.",
    example = "Barcelona",
    requiredMode = Schema.RequiredMode.REQUIRED
  )
  String city
) {}