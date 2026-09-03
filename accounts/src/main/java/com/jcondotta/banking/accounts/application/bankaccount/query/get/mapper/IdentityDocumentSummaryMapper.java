package com.jcondotta.banking.accounts.application.bankaccount.query.get.mapper;

import com.jcondotta.banking.accounts.application.bankaccount.query.get.model.IdentityDocumentSummary;
import com.jcondotta.banking.accounts.domain.bankaccount.value_objects.personal.DocumentNumber;
import com.jcondotta.banking.accounts.domain.bankaccount.value_objects.personal.IdentityDocument;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface IdentityDocumentSummaryMapper {

  @Mapping(target = "country", source = "country")
  @Mapping(target = "type", source = "type")
  @Mapping(target = "number", source = "number")
  IdentityDocumentSummary toSummary(IdentityDocument identityDocument);

  default String map(Enum<?> value) {
    return value != null ? value.name() : null;
  }

  default String map(DocumentNumber number) {
    return number != null ? number.value() : null;
  }
}