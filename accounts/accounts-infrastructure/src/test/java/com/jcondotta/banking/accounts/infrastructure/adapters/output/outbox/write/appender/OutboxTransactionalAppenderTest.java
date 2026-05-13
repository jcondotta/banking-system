package com.jcondotta.banking.accounts.infrastructure.adapters.output.outbox.write.appender;

import com.jcondotta.banking.accounts.infrastructure.adapters.output.outbox.entity.OutboxEntity;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.outbox.write.collector.OutboxEventCollector;
import com.jcondotta.banking.infrastructure.adapters.config.aws.dynamodb.DynamoDbTransactionContext;
import com.jcondotta.domain.core.AggregateRoot;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.TransactWriteItemsEnhancedRequest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OutboxTransactionalAppenderTest {

  private final TableSchema<OutboxEntity> outboxTableSchema = TableSchema.fromBean(OutboxEntity.class);

  @Mock
  private DynamoDbTable<OutboxEntity> outboxTable;

  @Mock
  private OutboxEventCollector outboxEventCollector;

  @Mock
  private AggregateRoot<?> aggregate;

  private OutboxTransactionalAppender appender;

  @BeforeEach
  void setUp() {
    appender = new OutboxTransactionalAppender(outboxTable, outboxEventCollector);
  }

  @Test
  void shouldAppendOutboxEventsToTransactionContext() {
    when(outboxTable.tableSchema()).thenReturn(outboxTableSchema);
    var event1 = OutboxEntity.builder().eventType("bank-account-opened").build();
    var event2 = OutboxEntity.builder().eventType("bank-account-status-changed").build();
    var transactionContext = new DynamoDbTransactionContext(TransactWriteItemsEnhancedRequest.builder());

    when(outboxEventCollector.collect(aggregate)).thenReturn(List.of(event1, event2));

    appender.append(aggregate, transactionContext);

    assertThat(transactionContext.builder().build().transactWriteItems()).hasSize(2);
    verify(outboxEventCollector).collect(aggregate);
  }

  @Test
  void shouldNotAppendItems_whenCollectorReturnsNoEvents() {
    var transactionContext = new DynamoDbTransactionContext(TransactWriteItemsEnhancedRequest.builder());

    when(outboxEventCollector.collect(aggregate)).thenReturn(List.of());

    appender.append(aggregate, transactionContext);

    assertThat(transactionContext.builder().build().transactWriteItems()).isNullOrEmpty();
    verifyNoInteractions(outboxTable);
  }
}
