package com.jcondotta.banking.accounts.domain.bankaccount.value_objects.personal;

import com.jcondotta.banking.accounts.domain.bankaccount.enums.DocumentCountry;
import com.jcondotta.banking.accounts.domain.bankaccount.enums.DocumentType;
import com.jcondotta.banking.accounts.domain.bankaccount.policies.DefaultDocumentNumberValidatorRegistry;
import com.jcondotta.banking.accounts.domain.bankaccount.policies.DocumentNumberValidationPolicy;
import com.jcondotta.banking.accounts.domain.bankaccount.policies.SpanishDniNumberValidator;
import com.jcondotta.banking.accounts.domain.bankaccount.policies.SpanishNieNumberValidator;

import java.util.List;

import static com.jcondotta.domain.support.DomainPreconditions.required;

public record IdentityDocument(DocumentCountry country, DocumentType type, DocumentNumber number) {

  public static final String DOCUMENT_COUNTRY_NOT_PROVIDED = "Document country must be provided";
  public static final String DOCUMENT_TYPE_NOT_PROVIDED = "Document type must be provided";
  public static final String DOCUMENT_NUMBER_NOT_PROVIDED = "Document number must be provided";

  private static final DocumentNumberValidationPolicy VALIDATION_POLICY = new DocumentNumberValidationPolicy(
    new DefaultDocumentNumberValidatorRegistry(
      List.of(
        new SpanishDniNumberValidator(),
        new SpanishNieNumberValidator()
      )
    )
  );

  public IdentityDocument {
    required(country, DOCUMENT_COUNTRY_NOT_PROVIDED);
    required(type, DOCUMENT_TYPE_NOT_PROVIDED);
    required(number, DOCUMENT_NUMBER_NOT_PROVIDED);

    VALIDATION_POLICY.validate(country, type, number);
  }

  public static IdentityDocument of(DocumentCountry country, DocumentType type, DocumentNumber number) {
    return new IdentityDocument(country, type, number);
  }
}