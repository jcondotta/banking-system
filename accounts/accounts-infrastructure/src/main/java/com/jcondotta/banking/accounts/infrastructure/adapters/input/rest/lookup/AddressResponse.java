package com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.lookup;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(name = "AddressResponse", description = "Represents the address of the account holder.")
public record AddressResponse(

    @NotBlank
    @Schema(description = "Street name.", example = "Carrer de Mallorca")
    String street,

    @NotBlank
    @Schema(description = "Street number.", example = "401")
    String streetNumber,

    @Schema(description = "Address addressComplement.", example = "4B")
    String complement,

    @NotBlank
    @Schema(description = "Postal code.", example = "08013")
    String postalCode,

    @NotBlank
    @Schema(description = "City name.", example = "Barcelona")
    String city
) {}