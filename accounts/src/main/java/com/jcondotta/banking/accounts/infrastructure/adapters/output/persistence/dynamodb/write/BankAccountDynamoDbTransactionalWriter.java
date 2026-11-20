package com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.dynamodb.write;

import com.jcondotta.banking.accounts.application.bankaccount.ports.output.BankAccountTransactionalWriter;
import com.jcondotta.application.idempotency.IdempotentSummary;
import com.jcondotta.banking.accounts.domain.bankaccount.aggregate.BankAccount;
import com.jcondotta.banking.accounts.domain.bankaccount.identity.BankAccountId;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.dynamodb.DynamoDbTransactionContext;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.dynamodb.enums.EntityType;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.dynamodb.idempotency.entity.IdempotencyEntity;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.dynamodb.idempotency.entity.IdempotencyEntityKey;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.dynamodb.idempotency.write.appender.IdempotencyTransactionalAppender;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.dynamodb.outbox.write.appender.OutboxTransactionalAppender;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.dynamodb.write.appender.BankAccountTransactionalAppender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.model.TransactWriteItemsEnhancedRequest;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class BankAccountDynamoDbTransactionalWriter implements BankAccountTransactionalWriter {

    private final DynamoDbEnhancedClient dynamoDbClient;
    private final BankAccountTransactionalAppender bankAccountAppender;
    private final OutboxTransactionalAppender outboxAppender;
    private final IdempotencyTransactionalAppender idempotencyAppender;

    @Override
    public void save(BankAccount bankAccount, IdempotentSummary<BankAccountId> summary) {
        var transactionContext = new DynamoDbTransactionContext(TransactWriteItemsEnhancedRequest.builder());

        var idempotencyEntity = IdempotencyEntity.builder()
            .partitionKey(IdempotencyEntityKey.partitionKey(summary.key()))
            .sortKey(IdempotencyEntityKey.sortKey())
            .entityType(EntityType.IDEMPOTENCY)
            .requestHash(summary.requestHash())
            .bankAccountId(bankAccount.getId().value())
            .createdAt(Instant.now())
            .build();

        bankAccountAppender.append(bankAccount, transactionContext);
        outboxAppender.append(bankAccount, transactionContext);
        idempotencyAppender.append(idempotencyEntity, transactionContext);

        dynamoDbClient.transactWriteItems(transactionContext.builder().build());
    }
}
