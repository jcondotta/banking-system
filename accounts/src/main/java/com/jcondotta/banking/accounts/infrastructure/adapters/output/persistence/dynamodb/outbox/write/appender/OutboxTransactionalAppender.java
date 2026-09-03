package com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.dynamodb.outbox.write.appender;

import com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.dynamodb.DynamoDbTransactionContext;
import com.jcondotta.application.TransactionContext;
import com.jcondotta.banking.accounts.application.bankaccount.ports.output.TransactionalAppender;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.dynamodb.outbox.write.collector.OutboxEventCollector;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.dynamodb.outbox.entity.OutboxEntity;
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
