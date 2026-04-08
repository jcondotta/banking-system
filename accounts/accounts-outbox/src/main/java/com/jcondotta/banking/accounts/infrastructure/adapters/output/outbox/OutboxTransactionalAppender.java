package com.jcondotta.banking.accounts.infrastructure.adapters.output.outbox;

import com.jcondotta.banking.accounts.DynamoDbTransactionContext;
import com.jcondotta.banking.accounts.application.TransactionContext;
import com.jcondotta.banking.accounts.application.outbound.TransactionalAppender;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.outbox.collector.OutboxEventCollector;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.outbox.entity.OutboxEntity;
import com.jcondotta.domain.core.AggregateRoot;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;

@Component
@RequiredArgsConstructor
public class OutboxTransactionalAppender implements TransactionalAppender {

  private final DynamoDbTable<OutboxEntity> outboxTable;
  private final OutboxEventCollector outboxEventCollector;

  @Override
  public void append(AggregateRoot<?> aggregate, TransactionContext transactionContext) {

    var dynamoDbTransactionContext = (DynamoDbTransactionContext) transactionContext;

    outboxEventCollector.collect(aggregate)
      .forEach(event -> dynamoDbTransactionContext.addPutItem(outboxTable, event));
  }
}