package com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.repository;

import com.jcondotta.banking.accounts.infrastructure.adapters.output.messaging.outbox.OutboxEventCollector;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.entity.BankAccountEntityKey;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.entity.BankingEntity;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.entity.OutboxEntity;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.mapper.BankAccountEntityMapper;
import com.jcondotta.banking.accounts.domain.bankaccount.aggregate.BankAccount;
import com.jcondotta.banking.accounts.domain.bankaccount.identity.BankAccountId;
import com.jcondotta.banking.accounts.domain.bankaccount.repository.BankAccountRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.TransactWriteItemsEnhancedRequest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Repository
@AllArgsConstructor
public class BankAccountDynamoDbRepository implements BankAccountRepository {

  private final DynamoDbEnhancedClient dynamoDbClient;

  private final DynamoDbTable<BankingEntity> bankingTable;
  private final DynamoDbTable<OutboxEntity> outboxTable;

  private final BankAccountEntityMapper bankAccountEntityMapper;
  private final OutboxEventCollector outboxEventCollector;

  @Override
  public Optional<BankAccount> findById(BankAccountId id) {
    var partitionKey = BankAccountEntityKey.partitionKey(id);
    var queryConditional = QueryConditional.keyEqualTo(Key.builder()
      .partitionValue(partitionKey)
      .build());

    var bankingEntities = bankingTable.query(queryConditional)
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

  public List<OutboxEntity> findOutboxEvents(UUID aggregateId) {

    var partitionKey = "BANK_ACCOUNT#" + aggregateId;

    return outboxTable.query(r -> r
        .queryConditional(
          QueryConditional.sortBeginsWith(
            Key.builder()
              .partitionValue(partitionKey)
              .sortValue("OUTBOX#")
              .build()
          )
        )
      )
      .stream()
      .flatMap(page -> page.items().stream())
      .toList();
  }

  @Override
  public void save(BankAccount bankAccount) {
    TransactWriteItemsEnhancedRequest.Builder builder = TransactWriteItemsEnhancedRequest.builder();

    bankAccountEntityMapper.toEntities(bankAccount)
      .forEach(bankingEntity -> builder
        .addPutItem(bankingTable, bankingEntity)
      );

//    outboxEventCollector.collect(bankAccount)
//      .forEach(entry -> builder
//        .addPutItem(outboxTable, entry)
//      );

    dynamoDbClient.transactWriteItems(builder.build());

    log.info(
      "BankAccount persisted successfully. id={}", bankAccount.getId()
    );
  }
}