package com.jcondotta.banking.recipients.infrastructure.adapters.output.persistence.dynamodb.mapper;

import com.jcondotta.banking.recipients.domain.recipient.aggregate.BankAccount;
import com.jcondotta.banking.recipients.domain.recipient.aggregate.Recipients;
import com.jcondotta.banking.recipients.domain.recipient.enums.AccountStatus;
import com.jcondotta.banking.recipients.domain.recipient.identity.BankAccountId;
import com.jcondotta.banking.recipients.infrastructure.adapters.output.persistence.dynamodb.entity.BankAccountEntityKey;
import com.jcondotta.banking.recipients.infrastructure.adapters.output.persistence.dynamodb.entity.AccountRecipientEntity;
import com.jcondotta.banking.recipients.infrastructure.adapters.output.persistence.dynamodb.enums.EntityType;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Component
@AllArgsConstructor
public class BankAccountEntityMapper {

  private final RecipientEntityMapper recipientEntityMapper;

  public List<AccountRecipientEntity> toEntities(BankAccount bankAccount) {
    var bankAccountEntity = toEntity(bankAccount);
    var recipientEntities = bankAccount.getRecipients().stream()
      .map(recipient -> recipientEntityMapper.toEntity(bankAccount.getId(), recipient))
      .toList();

    return Stream.concat(
      Stream.of(bankAccountEntity),
      recipientEntities.stream()
    ).toList();
  }

  AccountRecipientEntity toEntity(BankAccount bankAccount) {
    return AccountRecipientEntity.builder()
      .partitionKey(BankAccountEntityKey.partitionKey(bankAccount.getId()))
      .sortKey(BankAccountEntityKey.sortKey(bankAccount.getId()))
      .entityType(EntityType.BANK_ACCOUNT)
      .bankAccountId(bankAccount.getId().value())
      .accountStatus(bankAccount.getAccountStatus().name())
      .build();
  }

  public BankAccount restore(List<AccountRecipientEntity> bankingEntities) {
    var bankAccountEntity = findBankAccountEntity(bankingEntities)
      .orElseThrow(() -> new IllegalStateException("Bank account entity not found"));

    var recipientEntities = bankingEntities.stream()
      .filter(AccountRecipientEntity::isRecipient)
      .map(recipientEntityMapper::toDomain)
      .toList();

    return BankAccount.restore(
      BankAccountId.of(bankAccountEntity.getBankAccountId()),
      AccountStatus.valueOf(bankAccountEntity.getAccountStatus()),
      Recipients.of(recipientEntities)
    );
  }

  private Optional<AccountRecipientEntity> findBankAccountEntity(List<AccountRecipientEntity> entities) {
    return entities.stream()
      .filter(AccountRecipientEntity::isBankAccount)
      .findFirst();
  }
}
