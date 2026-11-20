package com.jcondotta.banking.accounts.application.bankaccount.query.get.model;

import com.jcondotta.banking.accounts.domain.bankaccount.enums.HolderType;

import java.time.Instant;
import java.util.UUID;

import static java.util.Objects.requireNonNull;

public record AccountHolderSummary(
  UUID id,
  PersonalInfoSummary personalInfo,
  ContactInfoSummary contactInfo,
  AddressSummary address,
  HolderType type,
  Instant createdAt
) {

  static final String ID_REQUIRED = "id must be provided";
  static final String PERSONAL_INFO_REQUIRED = "personalInfo must be provided";
  static final String CONTACT_INFO_REQUIRED = "contactInfo must be provided";
  static final String ADDRESS_REQUIRED = "address must be provided";
  static final String HOLDER_TYPE_REQUIRED = "type must be provided";
  static final String CREATED_AT_REQUIRED = "createdAt must be provided";

  public AccountHolderSummary {
    requireNonNull(id, ID_REQUIRED);
    requireNonNull(personalInfo, PERSONAL_INFO_REQUIRED);
    requireNonNull(contactInfo, CONTACT_INFO_REQUIRED);
    requireNonNull(address, ADDRESS_REQUIRED);
    requireNonNull(type, HOLDER_TYPE_REQUIRED);
    requireNonNull(createdAt, CREATED_AT_REQUIRED);
  }
}