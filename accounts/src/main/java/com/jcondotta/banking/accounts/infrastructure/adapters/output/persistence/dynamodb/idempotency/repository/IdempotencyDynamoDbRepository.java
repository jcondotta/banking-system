package com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.dynamodb.idempotency.repository;

import com.jcondotta.application.idempotency.identity.IdempotencyKey;
import com.jcondotta.application.idempotency.repository.IdempotencyRepository;
import com.jcondotta.application.idempotency.IdempotencyRecord;
import com.jcondotta.banking.accounts.domain.bankaccount.identity.BankAccountId;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.dynamodb.enums.EntityType;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.dynamodb.idempotency.entity.IdempotencyEntity;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.dynamodb.idempotency.entity.IdempotencyEntityKey;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.dynamodb.idempotency.mapper.IdempotencyEntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.GetItemEnhancedRequest;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class IdempotencyDynamoDbRepository implements IdempotencyRepository<IdempotencyRecord<BankAccountId>> {

    private final DynamoDbTable<IdempotencyEntity> table;
    private final IdempotencyEntityMapper idempotencyEntityMapper;

    @Override
    public Optional<IdempotencyRecord<BankAccountId>> find(IdempotencyKey key) {
        var dynamoKey = Key.builder()
            .partitionValue(IdempotencyEntityKey.partitionKey(key))
            .sortValue(IdempotencyEntityKey.sortKey())
            .build();

        var request = GetItemEnhancedRequest.builder()
            .key(dynamoKey)
            .consistentRead(true)
            .build();

        return Optional.ofNullable(table.getItem(request))
            .map(entity -> {
                if (entity.getEntityType() != EntityType.IDEMPOTENCY) {
                    throw new IllegalStateException("Unexpected entity type for idempotency record");
                }
                return idempotencyEntityMapper.toRecord(key, entity);
            });
    }
}
