package com.jcondotta.banking.recipients.infrastructure.adapters.output.persistence.dynamodb.query;

import com.jcondotta.banking.recipients.application.bankaccount.query.list_recipients.RecipientQueryRepository;
import com.jcondotta.banking.recipients.application.bankaccount.query.model.RecipientSummary;
import com.jcondotta.banking.recipients.domain.recipient.enums.RecipientStatus;
import com.jcondotta.banking.recipients.domain.recipient.exceptions.BankAccountNotFoundException;
import com.jcondotta.banking.recipients.domain.recipient.identity.BankAccountId;
import com.jcondotta.banking.recipients.infrastructure.adapters.output.persistence.dynamodb.entity.AccountRecipientEntity;
import com.jcondotta.banking.recipients.infrastructure.adapters.output.persistence.dynamodb.entity.BankAccountEntityKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

import java.util.List;

@Component
@RequiredArgsConstructor
class RecipientQueryRepositoryImpl implements RecipientQueryRepository {

  private static final String ACTIVE_RECIPIENT_STATUS = RecipientStatus.ACTIVE.name();

  private final DynamoDbTable<AccountRecipientEntity> accountRecipientsTable;
  private final RecipientSummaryEntityMapper recipientSummaryEntityMapper;

  @Override
  public List<RecipientSummary> findActiveByBankAccountId(BankAccountId bankAccountId) {
    var entities = findByPartitionKey(bankAccountId);

    if (entities.stream().noneMatch(AccountRecipientEntity::isBankAccount)) {
      throw new BankAccountNotFoundException(bankAccountId);
    }

    return entities.stream()
      .filter(AccountRecipientEntity::isRecipient)
      .filter(entity -> ACTIVE_RECIPIENT_STATUS.equals(entity.getRecipientStatus()))
      .map(recipientSummaryEntityMapper::fromEntity)
      .toList();
  }

  private List<AccountRecipientEntity> findByPartitionKey(BankAccountId bankAccountId) {
    var partitionKey = BankAccountEntityKey.partitionKey(bankAccountId);
    var queryConditional = QueryConditional.keyEqualTo(Key.builder()
      .partitionValue(partitionKey)
      .build());

    return accountRecipientsTable.query(queryConditional)
      .items()
      .stream()
      .toList();
  }
}
