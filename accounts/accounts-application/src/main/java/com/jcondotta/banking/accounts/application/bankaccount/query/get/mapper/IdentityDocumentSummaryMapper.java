package com.jcondotta.banking.accounts.application.bankaccount.query.get.mapper;

import com.jcondotta.banking.accounts.application.bankaccount.query.get.model.IdentityDocumentSummary;
import com.jcondotta.banking.accounts.domain.bankaccount.value_objects.personal.IdentityDocument;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface IdentityDocumentSummaryMapper {

  default IdentityDocumentSummary toSummary(IdentityDocument identityDocument) {

    if (identityDocument == null) {
      return null;
    }

    return new IdentityDocumentSummary(
      identityDocument.country().name(),
      identityDocument.type().name(),
      identityDocument.number().value()
    );
  }
}