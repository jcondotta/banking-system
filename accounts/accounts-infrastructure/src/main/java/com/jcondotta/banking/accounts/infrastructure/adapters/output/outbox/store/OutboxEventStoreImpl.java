package com.jcondotta.banking.accounts.infrastructure.adapters.output.outbox.store;

import com.jcondotta.banking.accounts.infrastructure.adapters.output.outbox.store.exceptions.OutboxEventAlreadyProcessedException;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.outbox.entity.OutboxEntity;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.outbox.entity.OutboxStatusQueryKey;
import com.jcondotta.banking.accounts.infrastructure.properties.OutboxTableProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.UpdateItemEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.ConditionalCheckFailedException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional.sortBeginsWith;

@Slf4j
@Repository
@RequiredArgsConstructor
public class OutboxEventStoreImpl implements OutboxEventStore {

  private final DynamoDbTable<OutboxEntity> outboxTable;
  private final OutboxTableProperties outboxTableProperties;

  @Override
  public List<OutboxEntity> findPendingEvents(OutboxQuery query) {
    var queryKey = OutboxStatusQueryKey.of(query.shard(), query.status());

    var queryConditional = sortBeginsWith(Key.builder()
      .partitionValue(queryKey.partitionKey())
      .sortValue(queryKey.sortKey())
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

    return items;
  }

  @Override
  public boolean tryClaimEvent(OutboxEntity item) {
    item.markAsProcessing(Instant.now());

    Expression condition = Expression.builder()
      .expression("begins_with(gsi1sk, :pending)")
      .putExpressionValue(":pending", AttributeValue.fromS(OutboxStatusQueryKey.SORT_KEY_PENDING_PREFIX))
      .build();

    var request = UpdateItemEnhancedRequest.builder(OutboxEntity.class)
      .item(item)
      .conditionExpression(condition)
      .build();

    try {
      outboxTable.updateItem(request);
      log.info("Outbox event marked as processing. eventId={}, aggregateId={}", item.getEventId(), item.getAggregateId());
      return true;
    }
    catch (ConditionalCheckFailedException e) {
      return false;
    }
  }

  @Override
  public void markEventAsPublished(OutboxEntity item) {
    item.markAsPublished(Instant.now());

    Expression condition = Expression.builder()
      .expression("begins_with(gsi1sk, :processing)")
      .putExpressionValue(":processing", AttributeValue.fromS(OutboxStatusQueryKey.SORT_KEY_PROCESSING_PREFIX))
      .build();

    var request = UpdateItemEnhancedRequest.builder(OutboxEntity.class)
      .item(item)
      .conditionExpression(condition)
      .build();

    try {
      outboxTable.updateItem(request);
      log.info("Outbox event marked as published. eventId={}, aggregateId={}", item.getEventId(), item.getAggregateId());
    }
    catch (ConditionalCheckFailedException e) {
      throw new OutboxEventAlreadyProcessedException(item.getEventId().toString());
    }
  }
}