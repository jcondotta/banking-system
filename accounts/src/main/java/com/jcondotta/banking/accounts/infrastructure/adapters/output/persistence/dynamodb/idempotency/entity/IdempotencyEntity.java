package com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.dynamodb.idempotency.entity;

import lombok.*;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.dynamodb.enums.EntityType;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@DynamoDbBean
public class IdempotencyEntity {
    private String partitionKey;
    private String sortKey;
    private EntityType entityType;
    private String requestHash;
    private int hashVersion;
    private UUID bankAccountId;
    private Instant createdAt;

    @DynamoDbPartitionKey
    public String getPartitionKey() { return partitionKey; }

    @DynamoDbSortKey
    public String getSortKey() { return sortKey; }
}
