package com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.dynamodb;

import com.jcondotta.application.TransactionContext;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.model.TransactWriteItemsEnhancedRequest;

public record DynamoDbTransactionContext(TransactWriteItemsEnhancedRequest.Builder builder)
  implements TransactionContext {

  public <T> void addPutItem(DynamoDbTable<T> table, T item) {
    builder.addPutItem(table, item);
  }
}
