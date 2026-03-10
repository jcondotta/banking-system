package com.jcondotta.banking.recipients.infrastructure.bankaccount.adapters.output.persistence.mapper;


import com.jcondotta.banking.recipients.domain.recipient.aggregate.Recipient;
import com.jcondotta.banking.recipients.domain.recipient.enums.RecipientStatus;
import com.jcondotta.banking.recipients.domain.recipient.identity.BankAccountId;
import com.jcondotta.banking.recipients.domain.recipient.identity.RecipientId;
import com.jcondotta.banking.recipients.domain.recipient.value_objects.Iban;
import com.jcondotta.banking.recipients.domain.recipient.value_objects.RecipientName;
import com.jcondotta.banking.recipients.infrastructure.bankaccount.adapters.output.persistence.entity.AccountRecipientEntity;
import com.jcondotta.banking.recipients.infrastructure.bankaccount.adapters.output.persistence.entity.RecipientEntityKey;
import com.jcondotta.banking.recipients.infrastructure.bankaccount.adapters.output.persistence.enums.EntityType;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RecipientEntityMapper {

  default AccountRecipientEntity toEntity(BankAccountId bankAccountId, Recipient recipient) {
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

  default Recipient toDomain(AccountRecipientEntity entity) {
    return Recipient.restore(
      RecipientId.of(entity.getRecipientId()),
      RecipientName.of(entity.getRecipientName()),
      Iban.of(entity.getIban()),
      RecipientStatus.valueOf(entity.getRecipientStatus()),
      entity.getCreatedAt()
    );
  }
}