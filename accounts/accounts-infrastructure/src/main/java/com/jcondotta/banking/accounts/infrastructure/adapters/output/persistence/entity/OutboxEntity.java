package com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.entity;

import com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.enums.EntityType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

import java.time.Instant;
import java.util.UUID;

@Setter
@DynamoDbBean
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OutboxEntity {

  private String partitionKey;
  private String sortKey;

  private String gsi1pk;
  private String gsi1sk;

  private EntityType entityType;

  private UUID eventId;
  private UUID aggregateId;
  private String eventType;

  private String payload;
  private Instant publishedAt;

  @DynamoDbPartitionKey
  @DynamoDbAttribute("partitionKey")
  public String getPartitionKey() { return partitionKey; }

  @DynamoDbSortKey
  @DynamoDbAttribute("sortKey")
  public String getSortKey() { return sortKey; }

  @DynamoDbAttribute("entityType")
  public EntityType getEntityType() { return entityType; }

  @DynamoDbAttribute("eventId")
  public UUID getEventId() { return eventId; }

  @DynamoDbAttribute("aggregateId")
  public UUID getAggregateId() { return aggregateId; }

  @DynamoDbAttribute("eventType")
  public String getEventType() { return eventType; }

  @DynamoDbAttribute("payload")
  public String getPayload() { return payload; }

  @DynamoDbAttribute("publishedAt")
  public Instant getPublishedAt() { return publishedAt; }

  @DynamoDbSecondaryPartitionKey(indexNames = "gsi-outbox-status")
  public String getGsi1pk() {
    return gsi1pk;
  }

  @DynamoDbSecondarySortKey(indexNames = "gsi-outbox-status")
  public String getGsi1sk() {
    return gsi1sk;
  }
}
