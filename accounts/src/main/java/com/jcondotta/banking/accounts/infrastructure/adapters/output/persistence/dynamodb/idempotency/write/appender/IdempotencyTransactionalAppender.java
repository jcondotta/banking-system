package com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.dynamodb.idempotency.write.appender;

import com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.dynamodb.DynamoDbTransactionContext;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.dynamodb.idempotency.entity.IdempotencyEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.enhanced.dynamodb.model.TransactPutItemEnhancedRequest;

@Component
@RequiredArgsConstructor
public class IdempotencyTransactionalAppender {

    private final DynamoDbTable<IdempotencyEntity> idempotencyTable;

    public void append(IdempotencyEntity entity, DynamoDbTransactionContext transactionContext) {
        transactionContext.builder().addPutItem(idempotencyTable, TransactPutItemEnhancedRequest.builder(IdempotencyEntity.class)
            .item(entity)
//            .conditionExpression(Expression.builder()
//                .expression("attribute_not_exists(sortKey)")
//                .build())
            .build());
    }
}
