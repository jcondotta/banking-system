package com.jcondotta.banking.recipients.infrastructure.adapters.output.persistence.mapper;

import com.jcondotta.banking.recipients.application.recipient.query.model.RecipientSummary;
import com.jcondotta.banking.recipients.infrastructure.adapters.output.persistence.entity.RecipientEntity;
import org.springframework.stereotype.Component;

@Component
public class RecipientSummaryMapper {

  public RecipientSummary fromEntity(RecipientEntity entity) {
    return new RecipientSummary(
      entity.getId(),
      entity.getBankAccountId(),
      entity.getName(),
      entity.getIban(),
      entity.getCreatedAt()
    );
  }
}
