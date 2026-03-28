package com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.repository;

import com.jcondotta.banking.accounts.infrastructure.adapters.output.messaging.outbox.OutboxEventCollector;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.entity.BankingEntity;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.entity.OutboxEntity;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.enums.EntityType;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.mapper.BankAccountEntityMapper;
import com.jcondotta.banking.accounts.infrastructure.fixtures.AccountHolderFixtures;
import com.jcondotta.banking.accounts.infrastructure.fixtures.BankAccountTestFixture;
import com.jcondotta.banking.accounts.infrastructure.support.DynamoPageIterable;
import com.jcondotta.banking.accounts.domain.bankaccount.aggregate.BankAccount;
import com.jcondotta.banking.accounts.domain.bankaccount.enums.HolderType;
import com.jcondotta.banking.accounts.domain.bankaccount.identity.BankAccountId;
import org.junit.jupiter.api.BeforeEach;
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

  private static final AccountHolderFixtures PRIMARY_ACCOUNT_HOLDER = null;//AccountHolderFixtures.JEFFERSON;

  @Mock
  private DynamoDbEnhancedClient dynamoDbClient;

  @Mock
  private DynamoDbTable<BankingEntity> bankingTable;

  @Mock
  private DynamoDbTable<OutboxEntity> outboxTable;

  @Mock
  private BankAccountEntityMapper entityMapper;

  @Mock
  private OutboxEventCollector outboxCollector;

  private BankAccountDynamoDbRepository repository;

  private final TableSchema<BankingEntity> bankingTableSchema = TableSchema.fromBean(BankingEntity.class);
  private final TableSchema<OutboxEntity> outboxTableSchema = TableSchema.fromBean(OutboxEntity.class);

//  @BeforeEach
//  void setUp() {
//    repository = new BankAccountDynamoDbRepository(
//      dynamoDbClient,
//      bankingTable,
//      outboxTable,
//      entityMapper,
//      outboxCollector
//    );
//  }
//
//  @Test
//  void shouldReturnBankAccount_whenEntitiesAreFound() {
//    BankAccount bankAccount = BankAccountTestFixture.openActiveAccount(PRIMARY_ACCOUNT_HOLDER);
//
//    BankingEntity bankAccountEntity = BankingEntity.builder()
//      .entityType(EntityType.BANK_ACCOUNT)
//      .build();
//
//    BankingEntity accountHolderEntity = BankingEntity.builder()
//      .entityType(EntityType.ACCOUNT_HOLDER)
//      .build();
//
//    var bankingEntities = List.of(bankAccountEntity, accountHolderEntity);
//
//    when(bankingTable.query(any(QueryConditional.class)))
//      .thenReturn(DynamoPageIterable.pageOf(bankingEntities));
//
//    when(entityMapper.restore(bankingEntities)).thenReturn(bankAccount);
//
//    assertThat(repository.findById(bankAccount.getId())).hasValue(bankAccount);
//
//    verify(bankingTable).query(any(QueryConditional.class));
//    verify(entityMapper).restore(bankingEntities);
//  }
//
//  @Test
//  void shouldReturnBankAccount_whenMultipleAccountHoldersExist() {
//    BankAccount bankAccount = BankAccountTestFixture.openActiveAccount(PRIMARY_ACCOUNT_HOLDER);
//
//    BankingEntity bankAccountEntity = BankingEntity.builder()
//      .entityType(EntityType.BANK_ACCOUNT)
//      .build();
//
//    BankingEntity primaryHolder = BankingEntity.builder()
//      .entityType(EntityType.ACCOUNT_HOLDER)
//      .holderType(HolderType.PRIMARY.name())
//      .build();
//
//    BankingEntity jointHolder = BankingEntity.builder()
//      .entityType(EntityType.ACCOUNT_HOLDER)
//      .holderType(HolderType.JOINT.name())
//      .build();
//
//    var bankingEntities = List.of(bankAccountEntity, primaryHolder, jointHolder);
//
//    when(bankingTable.query(any(QueryConditional.class)))
//      .thenReturn(DynamoPageIterable.pageOf(bankingEntities));
//
//    when(entityMapper.restore(bankingEntities)).thenReturn(bankAccount);
//
//    assertThat(repository.findById(bankAccount.getId())).hasValue(bankAccount);
//
//    verify(bankingTable).query(any(QueryConditional.class));
//    verify(entityMapper).restore(bankingEntities);
//  }
//
//  @Test
//  void shouldReturnEmpty_whenBankAccountDoesNotExist() {
//    BankAccountId bankAccountId = BankAccountId.newId();
//
//    when(bankingTable.query(any(QueryConditional.class)))
//      .thenReturn(DynamoPageIterable.emptyPage());
//
//    assertThat(repository.findById(bankAccountId)).isEmpty();
//
//    verify(bankingTable).query(any(QueryConditional.class));
//    verifyNoInteractions(entityMapper);
//  }
//
//  @Test
//  void shouldPersistAggregateAndOutboxEvents_whenSavingBankAccount() {
//    BankAccount bankAccount = BankAccountTestFixture.openPendingAccount(PRIMARY_ACCOUNT_HOLDER);
//
//    when(bankingTable.tableSchema()).thenReturn(bankingTableSchema);
//    when(outboxTable.tableSchema()).thenReturn(outboxTableSchema);
//
//    List<BankingEntity> entities = List.of(
//      BankingEntity.builder().entityType(EntityType.BANK_ACCOUNT).build(),
//      BankingEntity.builder().entityType(EntityType.ACCOUNT_HOLDER).build()
//    );
//
//    var outboxEntity = OutboxEntity.builder()
//      .entityType(EntityType.OUTBOX_EVENT)
//      .eventType("bank-account-opened")
//      .build();
//
//    when(entityMapper.toEntities(bankAccount)).thenReturn(entities);
//    when(outboxCollector.collect(bankAccount)).thenReturn(List.of(outboxEntity));
//
//    repository.save(bankAccount);
//
//    var argumentCaptor = ArgumentCaptor.forClass(TransactWriteItemsEnhancedRequest.class);
//    verify(dynamoDbClient).transactWriteItems(argumentCaptor.capture());
//
//    var request = argumentCaptor.getValue();
//    assertThat(request.transactWriteItems())
//      .hasSize(entities.size() + 1);
//  }
}