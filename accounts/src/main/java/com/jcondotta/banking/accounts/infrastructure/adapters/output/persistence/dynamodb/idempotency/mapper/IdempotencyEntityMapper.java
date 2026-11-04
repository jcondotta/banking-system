package com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.dynamodb.idempotency.mapper;

import com.jcondotta.banking.accounts.application.common.idempotency.IdempotencyKey;
import com.jcondotta.banking.accounts.application.common.idempotency.IdempotencyRecord;
import com.jcondotta.banking.accounts.domain.bankaccount.identity.BankAccountId;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.dynamodb.enums.EntityType;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.dynamodb.idempotency.entity.IdempotencyEntity;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.dynamodb.idempotency.entity.IdempotencyEntityKey;

public final class IdempotencyEntityMapper {
    private IdempotencyEntityMapper() {}

    public static IdempotencyEntity toEntity(IdempotencyRecord record) {
        return IdempotencyEntity.builder()
            .entityType(EntityType.IDEMPOTENCY)
            .partitionKey(IdempotencyEntityKey.partitionKey(record.key()))
            .sortKey(IdempotencyEntityKey.sortKey())
            .requestHash(record.requestHash())
            .hashVersion(record.hashVersion())
            .bankAccountId(record.bankAccountId().value())
            .createdAt(record.createdAt()).build();
    }

    public static IdempotencyRecord toRecord(IdempotencyKey key, IdempotencyEntity entity) {
        return new IdempotencyRecord(
            key,
            entity.getRequestHash(),
            entity.getHashVersion(),
            BankAccountId.of(entity.getBankAccountId()),
            entity.getCreatedAt()
        );
    }
}
