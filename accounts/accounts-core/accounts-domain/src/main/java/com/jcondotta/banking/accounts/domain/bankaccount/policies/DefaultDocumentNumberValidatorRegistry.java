package com.jcondotta.banking.accounts.domain.bankaccount.policies;

import com.jcondotta.banking.accounts.domain.bankaccount.enums.DocumentCountry;
import com.jcondotta.banking.accounts.domain.bankaccount.enums.DocumentType;
import com.jcondotta.domain.exception.InvalidDomainDataException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.jcondotta.domain.support.Preconditions.required;

public final class DefaultDocumentNumberValidatorRegistry implements DocumentNumberValidatorRegistry {

  private static final String VALIDATOR_NOT_FOUND = "No document validator found for %s - %s";
  static final String VALIDATORS_NOT_PROVIDED = "validators must be provided";
  static final String VALIDATOR_NOT_PROVIDED = "validator must not be null";
  static final String COUNTRY_NOT_PROVIDED = "country must be provided";
  static final String TYPE_NOT_PROVIDED = "type must be provided";

  private record Key(DocumentCountry country, DocumentType type) {}

  private final Map<Key, DocumentNumberValidator> validators;

  public DefaultDocumentNumberValidatorRegistry(List<DocumentNumberValidator> validators) {
    required(validators, VALIDATORS_NOT_PROVIDED);

    var map = new HashMap<Key, DocumentNumberValidator>();

    for (var validator : validators) {
      required(validator, VALIDATOR_NOT_PROVIDED);

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
    required(country, COUNTRY_NOT_PROVIDED);
    required(type, TYPE_NOT_PROVIDED);

    var validator = validators.get(new Key(country, type));

    if (validator == null) {
      throw new InvalidDomainDataException(
        VALIDATOR_NOT_FOUND.formatted(country, type)
      );
    }

    return validator;
  }
}
