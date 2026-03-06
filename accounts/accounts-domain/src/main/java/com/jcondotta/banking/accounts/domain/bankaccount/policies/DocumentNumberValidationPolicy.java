package com.jcondotta.banking.accounts.domain.bankaccount.policies;

import com.jcondotta.banking.accounts.domain.bankaccount.enums.DocumentCountry;
import com.jcondotta.banking.accounts.domain.bankaccount.enums.DocumentType;
import com.jcondotta.banking.accounts.domain.bankaccount.value_objects.personal.DocumentNumber;

import java.util.Objects;

public final class DocumentNumberValidationPolicy {

  public static final String COUNTRY_NOT_PROVIDED = "Document country must be provided";
  public static final String TYPE_NOT_PROVIDED = "Document type must be provided";
  public static final String NUMBER_NOT_PROVIDED = "Document number must be provided";

  public static final String REGISTRY_NOT_PROVIDED = "Validator registry must be provided";

  private final DocumentNumberValidatorRegistry registry;

  public DocumentNumberValidationPolicy(DocumentNumberValidatorRegistry registry) {
    this.registry = Objects.requireNonNull(registry, REGISTRY_NOT_PROVIDED);
  }

  public void validate(DocumentCountry country, DocumentType type, DocumentNumber number) {
    Objects.requireNonNull(country, COUNTRY_NOT_PROVIDED);
    Objects.requireNonNull(type, TYPE_NOT_PROVIDED);
    Objects.requireNonNull(number, NUMBER_NOT_PROVIDED);

    registry.resolve(country, type).validate(number);
  }
}