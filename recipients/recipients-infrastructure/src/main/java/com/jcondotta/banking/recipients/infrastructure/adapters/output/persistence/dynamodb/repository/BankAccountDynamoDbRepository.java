package com.jcondotta.banking.recipients.infrastructure.adapters.output.persistence.dynamodb.repository;

import com.jcondotta.banking.recipients.domain.recipient.aggregate.BankAccount;
import com.jcondotta.banking.recipients.domain.recipient.identity.BankAccountId;
import com.jcondotta.banking.recipients.domain.recipient.repository.BankAccountRepository;
import com.jcondotta.banking.recipients.infrastructure.adapters.output.persistence.dynamodb.entity.BankAccountEntityKey;
import com.jcondotta.banking.recipients.infrastructure.adapters.output.persistence.dynamodb.entity.AccountRecipientEntity;
import com.jcondotta.banking.recipients.infrastructure.adapters.output.persistence.dynamodb.mapper.BankAccountEntityMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.TransactWriteItemsEnhancedRequest;

import java.util.Optional;

@Slf4j
@Repository
@AllArgsConstructor
public class BankAccountDynamoDbRepository implements BankAccountRepository {

  private final DynamoDbEnhancedClient dynamoDbClient;

  private final DynamoDbTable<AccountRecipientEntity> accountRecipientsTable;

  private final BankAccountEntityMapper bankAccountEntityMapper;

  @Override
  public Optional<BankAccount> findById(BankAccountId id) {
    var partitionKey = BankAccountEntityKey.partitionKey(id);
    var queryConditional = QueryConditional.keyEqualTo(Key.builder()
      .partitionValue(partitionKey)
      .build());

    var bankingEntities = accountRecipientsTable.query(queryConditional)
      .items().stream()
      .toList();

    if (bankingEntities.isEmpty()) {
      log.debug("BankAccount not found. id={}", id.value());
      return Optional.empty();
    }

    var bankAccount = bankAccountEntityMapper.restore(bankingEntities);
    log.info(
      "BankAccount restored successfully. id={}", bankAccount.getId().value()
    );
    return Optional.of(bankAccount);
  }

  @Override
  public void save(BankAccount bankAccount) {
    TransactWriteItemsEnhancedRequest.Builder builder = TransactWriteItemsEnhancedRequest.builder();

    bankAccountEntityMapper.toEntities(bankAccount)
      .forEach(bankingEntity -> builder
        .addPutItem(accountRecipientsTable, bankingEntity)
      );

    bankAccount.getRecipients()
      .forEach(r -> log.debug("Saving recipient [recipientId={}, status={}]", r.getId(), r.getStatus()));

    dynamoDbClient.transactWriteItems(builder.build());

    log.info(
      "BankAccount persisted successfully. id={}", bankAccount.getId().value()
    );
  }
}