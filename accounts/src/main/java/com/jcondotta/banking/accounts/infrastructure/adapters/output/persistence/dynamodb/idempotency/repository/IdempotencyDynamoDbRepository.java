package com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.dynamodb.idempotency.repository;

import com.jcondotta.application.idempotency.IdempotencyKey;
import com.jcondotta.application.idempotency.IdempotencyRepository;
import com.jcondotta.banking.accounts.application.common.idempotency.*;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.dynamodb.enums.EntityType;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.dynamodb.idempotency.entity.IdempotencyEntity;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.dynamodb.idempotency.entity.IdempotencyEntityKey;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.dynamodb.idempotency.mapper.IdempotencyEntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.model.GetItemEnhancedRequest;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class IdempotencyDynamoDbRepository implements IdempotencyRepository<IdempotencyRecord> {
    private final DynamoDbTable<IdempotencyEntity> table;

    @Override
    public Optional<IdempotencyRecord> find(IdempotencyKey key) {
        var dynamoKey = Key.builder()
          .partitionValue(IdempotencyEntityKey.partitionKey(key))
          .sortValue(IdempotencyEntityKey.sortKey())
          .build();
        return Optional.ofNullable(table.getItem(GetItemEnhancedRequest.builder()
            .key(dynamoKey).consistentRead(true).build()))
            .map(entity -> {
                if (entity.getEntityType() != EntityType.IDEMPOTENCY) {
                    throw new IllegalStateException("Unexpected entity type for idempotency record");
                }
                return IdempotencyEntityMapper.toRecord(key, entity);
            });
    }
}
