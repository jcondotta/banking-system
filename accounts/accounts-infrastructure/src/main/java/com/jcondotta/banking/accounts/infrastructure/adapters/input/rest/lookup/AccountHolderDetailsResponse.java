package com.jcondotta.banking.accounts.infrastructure.adapters.input.rest.lookup;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jcondotta.banking.accounts.domain.bankaccount.enums.HolderType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.UUID;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

@Schema(name = "AccountHolderDetailsResponse", description = "Represents the details of an account holder.")
public record AccountHolderDetailsResponse(

  @NotNull
  @Schema(
    description = "Unique identifier of the account holder.",
    example = "c6a4a1b2-0f8c-41e3-a622-98d66de824a9",
    requiredMode = RequiredMode.REQUIRED
  )
  UUID id,

  @NotNull
  @Schema(
    description = "Type of the account holder.",
    example = "PRIMARY",
    allowableValues = {"PRIMARY", "JOINT"},
    requiredMode = RequiredMode.REQUIRED
  )
  HolderType type,

  @NotNull
  @Schema(
    description = "Personal information of the account holder.",
    requiredMode = RequiredMode.REQUIRED
  )
  PersonalInfoResponse personalInfo,

  @NotNull
  @Schema(
    description = "Contact information of the account holder.",
    requiredMode = RequiredMode.REQUIRED
  )
  ContactInfoResponse contactInfo,

  @NotNull
  @Schema(
    description = "Address of the account holder.",
    requiredMode = RequiredMode.REQUIRED
  )
  AddressResponse address,

  @NotNull
  @JsonFormat(shape = JsonFormat.Shape.STRING)
  @Schema(
    description = "Date and time when the account holder was created (UTC).",
    example = "2023-08-23T12:55:00Z",
    requiredMode = RequiredMode.REQUIRED
  )
  Instant createdAt

) {}