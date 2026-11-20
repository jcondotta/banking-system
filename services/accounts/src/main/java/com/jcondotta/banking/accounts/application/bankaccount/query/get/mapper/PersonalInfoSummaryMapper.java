package com.jcondotta.banking.accounts.application.bankaccount.query.get.mapper;

import com.jcondotta.banking.accounts.application.bankaccount.query.get.model.PersonalInfoSummary;
import com.jcondotta.banking.accounts.domain.bankaccount.value_objects.personal.PersonalInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
  componentModel = "spring",
  uses = IdentityDocumentSummaryMapper.class,
  injectionStrategy = org.mapstruct.InjectionStrategy.CONSTRUCTOR
)
public interface PersonalInfoSummaryMapper {

  @Mapping(target = "firstName", expression = "java(personalInfo.holderName().firstName())")
  @Mapping(target = "lastName", expression = "java(personalInfo.holderName().lastName())")
  @Mapping(target = "identityDocument", source = "identityDocument")
  @Mapping(target = "dateOfBirth", expression = "java(personalInfo.dateOfBirth().value())")
  PersonalInfoSummary toDetails(PersonalInfo personalInfo);
}