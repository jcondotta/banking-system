package com.jcondotta.banking.accounts.infrastructure.adapters.output.outbox.store;

import com.jcondotta.banking.accounts.infrastructure.adapters.output.outbox.entity.OutboxEntity;
//import com.jcondotta.banking.accounts.infrastructure.adapters.output.outbox.entity.OutboxQueryKey;
import com.jcondotta.banking.accounts.infrastructure.properties.OutboxProcessingProperties;
import com.jcondotta.banking.accounts.infrastructure.properties.OutboxTableProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.DeleteItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.UpdateItemEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.ConditionalCheckFailedException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class OutboxEventStoreImpl implements OutboxEventStore {

  private final DynamoDbTable<OutboxEntity> outboxTable;
  private final OutboxTableProperties outboxTableProperties;
  private final OutboxProcessingProperties processingProperties;

  @Override
  public List<OutboxEntity> findPendingEvents(OutboxQuery query) {
//    var queryKey = OutboxQueryKey.of(query.shard(), Instant.now());

    var queryConditional = QueryConditional.sortLessThanOrEqualTo(Key.builder()
//      .partitionValue(queryKey.partitionKey())
//      .sortValue(queryKey.sortKey())
      .build()
    );

    var queryRequest = QueryEnhancedRequest.builder()
      .queryConditional(queryConditional)
      .limit(query.limit())
      .build();

    var dynamoIndexName = outboxTableProperties.indexes().gsi1().name();
    var pageIterator = outboxTable.index(dynamoIndexName)
      .query(queryRequest)
      .iterator();

    List<OutboxEntity> items = new ArrayList<>(query.limit());
    while (pageIterator.hasNext() && items.size() < query.limit()) {
      var pageItems = pageIterator.next().items();

      for (var item : pageItems) {
        items.add(item);

        if (items.size() >= query.limit()) {
          break;
        }
      }
    }

    log.debug("[shard={}] fetched {} pending events", query.shard(), items.size());

    return items;
  }

  @Override
  public Optional<OutboxEntity> tryClaimEvent(OutboxEntity item) {
    Instant now = Instant.now();
    Instant newVisibility = now.plus(processingProperties.lease().duration());

    Expression condition = Expression.builder()
      .expression("nextAttemptAt <= :now")
      .putExpressionValue(":now", AttributeValue.fromS(now.toString()))
      .build();

    OutboxEntity updatedItem = item.toBuilder()
      .nextAttemptAt(newVisibility)
      .gsi1sk(newVisibility.toString())
      .retryCount(item.getRetryCount() + 1)
      .build();

    var request = UpdateItemEnhancedRequest.builder(OutboxEntity.class)
      .item(updatedItem)
      .conditionExpression(condition)
      .build();

    try {
      outboxTable.updateItem(request);
      log.info("Outbox event claimed. eventId={}, gsi1sk={}, retryCount={}", updatedItem.getEventId(), updatedItem.getGsi1sk(), updatedItem.getRetryCount());
      return Optional.of(updatedItem);

    }
    catch (ConditionalCheckFailedException e) {
      return Optional.empty();
    }
  }

  @Override
  public void deletePublishedEvent(OutboxEntity item) {
    Expression condition = Expression.builder()
      .expression("nextAttemptAt = :claimedAt")
      .putExpressionValue(":claimedAt", AttributeValue.fromS(item.getNextAttemptAt().toString()))
      .build();

    var key = Key.builder()
      .partitionValue(item.getPartitionKey())
      .sortValue(item.getSortKey())
      .build();

    var request = DeleteItemEnhancedRequest.builder()
      .key(key)
      .conditionExpression(condition)
      .build();

    try {
      outboxTable.deleteItem(request);
      log.info("Outbox event deleted after publish. eventId={}, aggregateId={}", item.getEventId(), item.getAggregateId());
    }
    catch (ConditionalCheckFailedException e) {
      log.warn("Outbox event delete skipped — lease was re-claimed before delete completed. eventId={}", item.getEventId());
    }
  }

  @Override
  public void deadLetterEvent(OutboxEntity item) {
    log.error("Outbox event exceeded max retries, moving to dead letter. eventId={}, aggregateId={}, eventType={}, retryCount={}",
      item.getEventId(), item.getAggregateId(), item.getEventType(), item.getRetryCount());

    outboxTable.deleteItem(item);
  }
}
