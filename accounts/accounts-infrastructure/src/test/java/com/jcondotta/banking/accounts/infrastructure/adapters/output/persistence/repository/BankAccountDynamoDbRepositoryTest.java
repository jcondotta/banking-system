package com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.repository;

import com.jcondotta.banking.accounts.domain.bankaccount.enums.HolderType;
import com.jcondotta.banking.accounts.domain.bankaccount.identity.BankAccountId;
import com.jcondotta.banking.accounts.domain.testsupport.AccountHolderFixtures;
import com.jcondotta.banking.accounts.domain.testsupport.BankAccountTestFactory;
import com.jcondotta.banking.accounts.application.outbound.TransactionalAppender;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.outbox.entity.OutboxEntity;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.entity.BankingEntity;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.enums.EntityType;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.mapper.BankAccountEntityMapper;
import com.jcondotta.banking.accounts.infrastructure.support.DynamoPageIterable;
import com.jcondotta.banking.infrastructure.adapters.config.aws.dynamodb.DynamoDbTransactionContext;
import com.jcondotta.domain.core.AggregateRoot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.TransactWriteItemsEnhancedRequest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BankAccountDynamoDbRepositoryTest {

  private static final AccountHolderFixtures PRIMARY = AccountHolderFixtures.JEFFERSON;
  private static final AccountHolderFixtures JOINT = AccountHolderFixtures.VIRGINIO;

  private final TableSchema<BankingEntity> bankingTableSchema = TableSchema.fromBean(BankingEntity.class);
  private final TableSchema<OutboxEntity> outboxTableSchema = TableSchema.fromBean(OutboxEntity.class);

  @Mock
  private DynamoDbEnhancedClient dynamoDbClient;

  @Mock
  private DynamoDbTable<BankingEntity> bankingTable;

  @Mock
  private DynamoDbTable<OutboxEntity> outboxTable;

  @Mock
  private BankAccountEntityMapper bankAccountEntityMapper;

  @Mock
  private TransactionalAppender transactionalAppender;

  private BankAccountDynamoDbRepository repository;

  @BeforeEach
  void setUp() {
    repository = new BankAccountDynamoDbRepository(
      dynamoDbClient,
      bankingTable,
      bankAccountEntityMapper,
      List.of()
    );
  }

  @Nested
  class FindById {

    @Test
    void shouldReturnBankAccount_whenSingleHolderEntitiesAreFound() {
      var account = BankAccountTestFactory.withPrimary(PRIMARY);

      var entities = List.of(
        BankingEntity.builder().entityType(EntityType.BANK_ACCOUNT).build(),
        BankingEntity.builder().entityType(EntityType.ACCOUNT_HOLDER).build()
      );

      when(bankingTable.query(any(QueryConditional.class)))
        .thenReturn(DynamoPageIterable.pageOf(entities));
      when(bankAccountEntityMapper.restore(entities)).thenReturn(account);

      var result = repository.findById(account.getId());

      assertThat(result).contains(account);
      verify(bankAccountEntityMapper).restore(entities);
    }

    @Test
    void shouldReturnBankAccount_whenPrimaryAndJointHolderEntitiesAreFound() {
      var account = BankAccountTestFactory.withPrimaryAndJoint(PRIMARY, JOINT);

      var entities = List.of(
        BankingEntity.builder().entityType(EntityType.BANK_ACCOUNT).build(),
        BankingEntity.builder()
          .entityType(EntityType.ACCOUNT_HOLDER)
          .holderType(HolderType.PRIMARY.name())
          .build(),
        BankingEntity.builder()
          .entityType(EntityType.ACCOUNT_HOLDER)
          .holderType(HolderType.JOINT.name())
          .build()
      );

      when(bankingTable.query(any(QueryConditional.class)))
        .thenReturn(DynamoPageIterable.pageOf(entities));
      when(bankAccountEntityMapper.restore(entities)).thenReturn(account);

      var result = repository.findById(account.getId());

      assertThat(result).contains(account);
      verify(bankAccountEntityMapper).restore(entities);
    }

    @Test
    void shouldReturnEmpty_whenNoEntitiesAreFound() {
      var id = BankAccountId.newId();

      when(bankingTable.query(any(QueryConditional.class)))
        .thenReturn(DynamoPageIterable.emptyPage());

      var result = repository.findById(id);

      assertThat(result).isEmpty();
      verifyNoInteractions(bankAccountEntityMapper);
    }
  }

  @Nested
  class Save {

    @BeforeEach
    void setUp() {
      when(bankingTable.tableSchema()).thenReturn(bankingTableSchema);
    }

    @Test
    void shouldPersistOnlyBankingEntities_whenNoTransactionalAppenderExists() {
      var account = BankAccountTestFactory.withPrimary(PRIMARY);
      var bankingEntities = List.of(
        BankingEntity.builder().entityType(EntityType.BANK_ACCOUNT).build(),
        BankingEntity.builder().entityType(EntityType.ACCOUNT_HOLDER).build()
      );

      when(bankAccountEntityMapper.toEntities(account)).thenReturn(bankingEntities);

      repository.save(account);

      var captor = ArgumentCaptor.forClass(TransactWriteItemsEnhancedRequest.class);
      verify(dynamoDbClient).transactWriteItems(captor.capture());

      assertThat(captor.getValue().transactWriteItems()).hasSize(bankingEntities.size());
    }

    @Test
    void shouldInvokeTransactionalAppender_whenSavingBankAccount() {
      repository = new BankAccountDynamoDbRepository(
        dynamoDbClient,
        bankingTable,
        bankAccountEntityMapper,
        List.of(transactionalAppender)
      );

      var account = BankAccountTestFactory.withPrimary(PRIMARY);
      var bankingEntities = List.of(BankingEntity.builder().entityType(EntityType.BANK_ACCOUNT).build());

      when(bankAccountEntityMapper.toEntities(account)).thenReturn(bankingEntities);

      repository.save(account);

      verify(transactionalAppender).append(eq(account), any(DynamoDbTransactionContext.class));
      verify(dynamoDbClient).transactWriteItems(any(TransactWriteItemsEnhancedRequest.class));
    }

    @Test
    void shouldPersistBankingEntitiesAndAppenderItemsInSameTransaction() {
      when(outboxTable.tableSchema()).thenReturn(outboxTableSchema);

      var appender = new TestOutboxAppender(outboxTable);
      repository = new BankAccountDynamoDbRepository(
        dynamoDbClient,
        bankingTable,
        bankAccountEntityMapper,
        List.of(appender)
      );

      var account = BankAccountTestFactory.withPrimary(PRIMARY);
      var bankingEntities = List.of(
        BankingEntity.builder().entityType(EntityType.BANK_ACCOUNT).build(),
        BankingEntity.builder().entityType(EntityType.ACCOUNT_HOLDER).build()
      );

      when(bankAccountEntityMapper.toEntities(account)).thenReturn(bankingEntities);

      repository.save(account);

      var captor = ArgumentCaptor.forClass(TransactWriteItemsEnhancedRequest.class);
      verify(dynamoDbClient).transactWriteItems(captor.capture());

      assertThat(captor.getValue().transactWriteItems()).hasSize(bankingEntities.size() + 1);
    }
  }

  private record TestOutboxAppender(DynamoDbTable<OutboxEntity> outboxTable) implements TransactionalAppender {

    @Override
    public void append(AggregateRoot<?> aggregate, com.jcondotta.application.TransactionContext transactionContext) {
      ((DynamoDbTransactionContext) transactionContext).addPutItem(
        outboxTable,
        OutboxEntity.builder().eventType("bank-account-opened").build()
      );
    }
  }
}
