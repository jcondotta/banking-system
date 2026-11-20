package com.jcondotta.banking.recipients.infrastructure.adapters.output.persistence.mapper;

import com.jcondotta.banking.recipients.domain.recipient.aggregate.Recipient;
import com.jcondotta.banking.recipients.domain.recipient.identity.BankAccountId;
import com.jcondotta.banking.recipients.domain.recipient.identity.RecipientId;
import com.jcondotta.banking.recipients.domain.recipient.value_objects.Iban;
import com.jcondotta.banking.recipients.domain.recipient.value_objects.RecipientName;
import com.jcondotta.banking.recipients.infrastructure.adapters.output.persistence.entity.RecipientEntity;
import org.springframework.stereotype.Component;

@Component
public class RecipientEntityMapper {

  public RecipientEntity toEntity(Recipient recipient) {
    return RecipientEntity.builder()
      .id(recipient.getId().value())
      .bankAccountId(recipient.getBankAccountId().value())
      .name(recipient.getRecipientName().value())
      .iban(recipient.getIban().value())
      .createdAt(recipient.getCreatedAt())
      .version(recipient.getVersion())
      .build();
  }

  public Recipient toDomain(RecipientEntity entity) {
    return Recipient.restore(
      RecipientId.of(entity.getId()),
      BankAccountId.of(entity.getBankAccountId()),
      RecipientName.of(entity.getName()),
      Iban.of(entity.getIban()),
      entity.getCreatedAt(),
      entity.getVersion()
    );
  }
}
