package com.jcondotta.banking.recipients.infrastructure.adapters.output.persistence.dynamodb.repository;

import com.jcondotta.banking.recipients.domain.bankaccount.testsupport.BankAccountFixtures;
import com.jcondotta.banking.recipients.domain.recipient.aggregate.BankAccount;
import com.jcondotta.banking.recipients.domain.recipient.identity.BankAccountId;
import com.jcondotta.banking.recipients.infrastructure.adapters.output.persistence.dynamodb.entity.AccountRecipientEntity;
import com.jcondotta.banking.recipients.infrastructure.adapters.output.persistence.dynamodb.enums.EntityType;
import com.jcondotta.banking.recipients.infrastructure.adapters.output.persistence.dynamodb.mapper.BankAccountEntityMapper;
import com.jcondotta.banking.recipients.infrastructure.support.DynamoPageIterable;
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
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BankAccountDynamoDbRepositoryTest {

  @Mock
  private DynamoDbEnhancedClient dynamoDbClient;

  @Mock
  private DynamoDbTable<AccountRecipientEntity> accountRecipientsTable;

  @Mock
  private BankAccountEntityMapper bankAccountEntityMapper;

  private BankAccountDynamoDbRepository repository;

  @BeforeEach
  void setUp() {
    repository = new BankAccountDynamoDbRepository(
      dynamoDbClient,
      accountRecipientsTable,
      bankAccountEntityMapper
    );
  }

  @Test
  void shouldReturnBankAccount_whenEntitiesAreFound() {
    var bankAccount = BankAccountFixtures.WITH_ONE_RECIPIENT.create();
    var entities = List.of(
      AccountRecipientEntity.builder().entityType(EntityType.BANK_ACCOUNT).build(),
      AccountRecipientEntity.builder().entityType(EntityType.RECIPIENT).build()
    );

    when(accountRecipientsTable.query(any(QueryConditional.class)))
      .thenReturn(DynamoPageIterable.pageOf(entities));
    when(bankAccountEntityMapper.restore(entities)).thenReturn(bankAccount);

    var result = repository.findById(bankAccount.getId());

    assertThat(result).contains(bankAccount);
    verify(bankAccountEntityMapper).restore(entities);
  }

  @Test
  void shouldReturnEmpty_whenNoEntitiesAreFound() {
    var bankAccountId = BankAccountId.of(UUID.randomUUID());

    when(accountRecipientsTable.query(any(QueryConditional.class)))
      .thenReturn(DynamoPageIterable.emptyPage());

    var result = repository.findById(bankAccountId);

    assertThat(result).isEmpty();
    verifyNoInteractions(bankAccountEntityMapper);
  }

  @Test
  void shouldPersistMappedEntities_whenBankAccountIsSaved() {
    when(accountRecipientsTable.tableSchema())
      .thenReturn(TableSchema.fromBean(AccountRecipientEntity.class));

    var bankAccount = BankAccountFixtures.WITH_ONE_RECIPIENT.create();
    var entities = List.of(
      AccountRecipientEntity.builder().entityType(EntityType.BANK_ACCOUNT).build(),
      AccountRecipientEntity.builder().entityType(EntityType.RECIPIENT).build()
    );

    when(bankAccountEntityMapper.toEntities(bankAccount)).thenReturn(entities);

    repository.save(bankAccount);

    var captor = ArgumentCaptor.forClass(TransactWriteItemsEnhancedRequest.class);
    verify(dynamoDbClient).transactWriteItems(captor.capture());

    assertThat(captor.getValue().transactWriteItems()).hasSize(entities.size());
  }

  @Test
  void shouldPersistOnlyBankAccountEntity_whenBankAccountHasNoRecipients() {
    when(accountRecipientsTable.tableSchema())
      .thenReturn(TableSchema.fromBean(AccountRecipientEntity.class));

    var bankAccount = BankAccountFixtures.WITH_NO_RECIPIENTS.create();
    var entities = List.of(
      AccountRecipientEntity.builder().entityType(EntityType.BANK_ACCOUNT).build()
    );

    when(bankAccountEntityMapper.toEntities(bankAccount)).thenReturn(entities);

    repository.save(bankAccount);

    var captor = ArgumentCaptor.forClass(TransactWriteItemsEnhancedRequest.class);
    verify(dynamoDbClient).transactWriteItems(captor.capture());

    assertThat(captor.getValue().transactWriteItems()).hasSize(1);
  }
}
