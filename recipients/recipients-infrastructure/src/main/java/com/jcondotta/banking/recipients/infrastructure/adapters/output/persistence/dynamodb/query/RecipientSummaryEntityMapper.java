package com.jcondotta.banking.recipients.infrastructure.adapters.output.persistence.dynamodb.query;

import com.jcondotta.banking.recipients.application.bankaccount.query.model.RecipientSummary;
import com.jcondotta.banking.recipients.infrastructure.adapters.output.persistence.dynamodb.entity.AccountRecipientEntity;
import org.springframework.stereotype.Component;

@Component
class RecipientSummaryEntityMapper {

  RecipientSummary fromEntity(AccountRecipientEntity entity) {
    return new RecipientSummary(
      entity.getRecipientId(),
      entity.getRecipientName(),
      entity.getIban(),
      entity.getCreatedAt()
    );
  }
}
