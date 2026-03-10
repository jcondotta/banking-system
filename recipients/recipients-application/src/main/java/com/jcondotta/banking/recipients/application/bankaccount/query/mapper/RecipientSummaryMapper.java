package com.jcondotta.banking.recipients.application.bankaccount.query.mapper;

import com.jcondotta.banking.recipients.application.bankaccount.query.model.RecipientSummary;
import com.jcondotta.banking.recipients.domain.recipient.aggregate.Recipient;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface RecipientSummaryMapper {

  @Mapping(target = "recipientId", source = "id.value")
  @Mapping(target = "recipientName", source = "recipientName.value")
  @Mapping(target = "iban", source = "iban.value")
  RecipientSummary toSummary(Recipient recipient);

  List<RecipientSummary> toSummaryList(List<Recipient> recipients);
}