package com.jcondotta.banking.accounts.domain.bankaccount.policies;

import com.jcondotta.banking.accounts.domain.bankaccount.enums.DocumentCountry;
import com.jcondotta.banking.accounts.domain.bankaccount.enums.DocumentType;
import com.jcondotta.domain.exception.DomainValidationException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class DefaultDocumentNumberValidatorRegistry implements DocumentNumberValidatorRegistry {

  private static final String VALIDATOR_NOT_FOUND = "No document validator found for %s - %s";

  private record Key(DocumentCountry country, DocumentType type) {}

  private final Map<Key, DocumentNumberValidator> validators;

  public DefaultDocumentNumberValidatorRegistry(List<DocumentNumberValidator> validators) {
    Objects.requireNonNull(validators, "validators must be provided");

    var map = new HashMap<Key, DocumentNumberValidator>();

    for (var validator : validators) {
      Objects.requireNonNull(validator, "validator must not be null");

      var key = new Key(
        validator.supportedCountry(),
        validator.supportedType()
      );

      map.put(key, validator);
    }

    this.validators = Map.copyOf(map);
  }

  @Override
  public DocumentNumberValidator resolve(DocumentCountry country, DocumentType type) {
    Objects.requireNonNull(country, "country must be provided");
    Objects.requireNonNull(type, "type must be provided");

    var validator = validators.get(new Key(country, type));

    if (validator == null) {
      throw new DomainValidationException(
        VALIDATOR_NOT_FOUND.formatted(country, type)
      );
    }

    return validator;
  }
}