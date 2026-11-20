package com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.dynamodb.write.appender;

import com.jcondotta.banking.accounts.domain.bankaccount.aggregate.BankAccount;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.dynamodb.DynamoDbTransactionContext;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.dynamodb.entity.BankingEntity;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.dynamodb.mapper.BankAccountEntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.enhanced.dynamodb.model.TransactPutItemEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

@Component
@RequiredArgsConstructor
public class BankAccountTransactionalAppender {

    private final DynamoDbTable<BankingEntity> bankingTable;
    private final BankAccountEntityMapper bankAccountEntityMapper;

    public void append(BankAccount bankAccount, DynamoDbTransactionContext transactionContext) {
        var entities = bankAccountEntityMapper.toEntities(bankAccount);
        var bankAccountEntity = entities.get(0);
        var holderEntities = entities.subList(1, entities.size());

        transactionContext.builder().addPutItem(bankingTable, TransactPutItemEnhancedRequest.builder(BankingEntity.class)
            .item(bankAccountEntity)
            .conditionExpression(buildVersionCondition(bankAccount.getVersion()))
            .build());

        holderEntities.forEach(entity -> transactionContext.addPutItem(bankingTable, entity));
    }

    private static Expression buildVersionCondition(long currentVersion) {
        if (currentVersion == 0) {
            return Expression.builder()
                .expression("attribute_not_exists(sortKey)")
                .build();
        }
        return Expression.builder()
            .expression("#v = :currentVersion")
            .putExpressionName("#v", "version")
            .putExpressionValue(":currentVersion", AttributeValue.fromN(String.valueOf(currentVersion)))
            .build();
    }
}
