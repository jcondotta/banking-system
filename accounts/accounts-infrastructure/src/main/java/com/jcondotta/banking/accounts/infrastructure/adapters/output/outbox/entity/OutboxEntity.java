package com.jcondotta.banking.accounts.infrastructure.adapters.output.outbox.entity;

import com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.enums.EntityType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.UUID;

@DynamoDbBean
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
public class OutboxEntity {

  private static final EntityType DEFAULT_ENTITY_TYPE = EntityType.OUTBOX_EVENT;
  private static final OutboxStatus DEFAULT_STATUS = OutboxStatus.PENDING;

  private String partitionKey;
  private String sortKey;
  private String gsi1pk;
  private String gsi1sk;

  @Builder.Default
  private EntityType entityType = DEFAULT_ENTITY_TYPE;

  @Builder.Default
  private OutboxStatus status = DEFAULT_STATUS;

  private UUID eventId;
  private String aggregateId;
  private String eventType;
  private String payload;
//  private Instant processingAt;
  private Instant publishedAt;
  private Instant createdAt;
  private Long timeToLive;

  @DynamoDbPartitionKey
  @DynamoDbAttribute("partitionKey")
  public String getPartitionKey() { return partitionKey; }

  @DynamoDbSortKey
  @DynamoDbAttribute("sortKey")
  public String getSortKey() { return sortKey; }

  @DynamoDbAttribute("entityType")
  public EntityType getEntityType() { return entityType; }

  @DynamoDbAttribute("outboxStatus")
  public OutboxStatus getStatus() { return status; }

  @DynamoDbAttribute("eventId")
  public UUID getEventId() { return eventId; }

  @DynamoDbAttribute("aggregateId")
  public String getAggregateId() { return aggregateId; }

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

  @DynamoDbAttribute("createdAt")
  public Instant getCreatedAt() {
    return createdAt;
  }

  @DynamoDbAttribute("ttl")
  public Long getTimeToLive() {
    return timeToLive;
  }

  public void markAsPublished(Instant now) {
    Objects.requireNonNull(now, "publishedAt must not be null");

    if (this.status == OutboxStatus.PUBLISHED) {
      return;
    }

    setStatus(OutboxStatus.PUBLISHED);

    this.publishedAt = now;
    this.gsi1sk = OutboxStatusKey.buildSortKey(status, now);
    this.timeToLive = now.plus(1, ChronoUnit.DAYS).getEpochSecond();
  }

  public void markAsProcessing(Instant now) {
    Objects.requireNonNull(now, "processingAt must not be null");

    setStatus(OutboxStatus.PROCESSING);

    this.gsi1sk = OutboxStatusKey.buildSortKey(status, now);
  }

  @SuppressWarnings("all")
  void setStatus(OutboxStatus status) {
    if (!this.status.canTransitionTo(status)) {
      throw new IllegalStateException(
        "Invalid status transition: %s -> %s".formatted(this.status, status)
      );
    }
    this.status = status;
  }
}
