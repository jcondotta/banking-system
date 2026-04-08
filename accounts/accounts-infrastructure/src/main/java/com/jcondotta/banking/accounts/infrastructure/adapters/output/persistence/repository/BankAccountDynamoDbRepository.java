package com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.repository;

import com.jcondotta.banking.accounts.DynamoDbTransactionContext;
import com.jcondotta.banking.accounts.application.outbound.TransactionalAppender;
import com.jcondotta.banking.accounts.domain.bankaccount.aggregate.BankAccount;
import com.jcondotta.banking.accounts.domain.bankaccount.identity.BankAccountId;
import com.jcondotta.banking.accounts.domain.bankaccount.repository.BankAccountRepository;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.entity.BankAccountEntityKey;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.entity.BankingEntity;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.mapper.BankAccountEntityMapper;
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

@Slf4j
@Repository
@AllArgsConstructor
public class BankAccountDynamoDbRepository implements BankAccountRepository {

  private final DynamoDbEnhancedClient dynamoDbClient;
  private final DynamoDbTable<BankingEntity> bankingTable;

  private final BankAccountEntityMapper bankAccountEntityMapper;
  private final List<TransactionalAppender> appenders;

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
    log.info("BankAccount restored successfully. id={}", bankAccount.getId().value());
    return Optional.of(bankAccount);
  }

  @Override
  public void save(BankAccount bankAccount) {
    var builder = TransactWriteItemsEnhancedRequest.builder();
    var transactionContext = new DynamoDbTransactionContext(builder);

    bankAccountEntityMapper.toEntities(bankAccount)
      .forEach(entity -> transactionContext.addPutItem(bankingTable, entity));

    appenders.forEach(appender -> appender.append(bankAccount, transactionContext));

    dynamoDbClient.transactWriteItems(transactionContext.builder().build());

    log.info("BankAccount persisted successfully. id={}", bankAccount.getId().value());
  }
}
