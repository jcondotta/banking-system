package com.jcondotta.banking.recipients.infrastructure.adapters.output.persistence.dynamodb.mapper;


import com.jcondotta.banking.recipients.domain.recipient.aggregate.Recipient;
import com.jcondotta.banking.recipients.domain.recipient.enums.RecipientStatus;
import com.jcondotta.banking.recipients.domain.recipient.identity.BankAccountId;
import com.jcondotta.banking.recipients.domain.recipient.identity.RecipientId;
import com.jcondotta.banking.recipients.domain.recipient.value_objects.Iban;
import com.jcondotta.banking.recipients.domain.recipient.value_objects.RecipientName;
import com.jcondotta.banking.recipients.infrastructure.adapters.output.persistence.dynamodb.entity.AccountRecipientEntity;
import com.jcondotta.banking.recipients.infrastructure.adapters.output.persistence.dynamodb.entity.RecipientEntityKey;
import com.jcondotta.banking.recipients.infrastructure.adapters.output.persistence.dynamodb.enums.EntityType;
import org.springframework.stereotype.Component;

@Component
public class RecipientEntityMapper {

  public AccountRecipientEntity toEntity(BankAccountId bankAccountId, Recipient recipient) {
    return AccountRecipientEntity.builder()
      .partitionKey(RecipientEntityKey.partitionKey(bankAccountId))
      .sortKey(RecipientEntityKey.sortKey(recipient.getId()))
      .entityType(EntityType.RECIPIENT)
      .bankAccountId(bankAccountId.value())
      .recipientId(recipient.getId().value())
      .recipientName(recipient.getRecipientName().value())
      .iban(recipient.getIban().value())
      .recipientStatus(recipient.getStatus().name())
      .createdAt(recipient.getCreatedAt())
      .build();
  }

  public Recipient toDomain(AccountRecipientEntity entity) {
    return Recipient.restore(
      RecipientId.of(entity.getRecipientId()),
      RecipientName.of(entity.getRecipientName()),
      Iban.of(entity.getIban()),
      RecipientStatus.valueOf(entity.getRecipientStatus()),
      entity.getCreatedAt()
    );
  }
}
