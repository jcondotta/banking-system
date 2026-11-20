package com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.dynamodb.idempotency.mapper;

import com.jcondotta.application.idempotency.identity.IdempotencyKey;
import com.jcondotta.application.idempotency.IdempotencyRecord;
import com.jcondotta.banking.accounts.domain.bankaccount.identity.BankAccountId;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.dynamodb.enums.EntityType;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.dynamodb.idempotency.entity.IdempotencyEntity;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.dynamodb.idempotency.entity.IdempotencyEntityKey;
import org.springframework.stereotype.Component;

@Component
public class IdempotencyEntityMapper {

    public IdempotencyEntity toEntity(IdempotencyRecord<BankAccountId> record) {
        return IdempotencyEntity.builder()
            .entityType(EntityType.IDEMPOTENCY)
            .partitionKey(IdempotencyEntityKey.partitionKey(record.key()))
            .sortKey(IdempotencyEntityKey.sortKey())
            .requestHash(record.requestHash())
            .bankAccountId(record.aggregateId().value())
            .build();
    }

    public IdempotencyRecord<BankAccountId> toRecord(IdempotencyKey key, IdempotencyEntity entity) {
        return new IdempotencyRecord<>(key, entity.getRequestHash(), BankAccountId.of(entity.getBankAccountId()));
    }
}
