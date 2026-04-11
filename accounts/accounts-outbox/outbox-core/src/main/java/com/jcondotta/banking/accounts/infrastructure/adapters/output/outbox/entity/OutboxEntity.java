package com.jcondotta.banking.accounts.infrastructure.adapters.output.outbox.entity;

import lombok.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@DynamoDbBean
public class OutboxEntity {

  private String partitionKey;
  private String sortKey;

  private String gsi1pk;
  private String gsi1sk;

  private int shard;
  private Instant nextAttemptAt;

  @Builder.Default
  private int retryCount = 0;

  private UUID eventId;
  private String aggregateId;
  private String eventType;
  private String payload;
  private Instant createdAt;

  @DynamoDbPartitionKey
  @DynamoDbAttribute("partitionKey")
  public String getPartitionKey() {
    return partitionKey;
  }

  @DynamoDbSortKey
  @DynamoDbAttribute("sortKey")
  public String getSortKey() {
    return sortKey;
  }

  @DynamoDbSecondaryPartitionKey(indexNames = "gsi-outbox-status")
  public String getGsi1pk() { return gsi1pk; }

  @DynamoDbSecondarySortKey(indexNames = "gsi-outbox-status")
  public String getGsi1sk() { return gsi1sk; }
}
