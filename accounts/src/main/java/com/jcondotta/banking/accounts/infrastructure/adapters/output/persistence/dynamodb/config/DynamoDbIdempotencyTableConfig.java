package com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.dynamodb.config;

import com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.dynamodb.idempotency.entity.IdempotencyEntity;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.dynamodb.properties.BankAccountsTableProperties;
import org.springframework.context.annotation.*;
import software.amazon.awssdk.enhanced.dynamodb.*;

@Configuration
public class DynamoDbIdempotencyTableConfig {
    @Bean
    public DynamoDbTable<IdempotencyEntity> idempotencyTable(DynamoDbEnhancedClient client, BankAccountsTableProperties properties) {
        return client.table(properties.tableName(), TableSchema.fromBean(IdempotencyEntity.class));
    }
}
