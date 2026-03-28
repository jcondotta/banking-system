package com.jcondotta.banking.accounts.infrastructure.adapters.output.messaging.outbox.query;

import com.jcondotta.banking.accounts.infrastructure.adapters.output.messaging.outbox.OutboxEventAlreadyProcessedException;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.entity.OutboxEntity;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.entity.OutboxStatusQueryKey;
import com.jcondotta.banking.accounts.infrastructure.properties.OutboxTableProperties;
import lombok.RequiredArgsConstructor;
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

@Repository
@RequiredArgsConstructor
public class OutboxRepositoryImpl implements OutboxRepository {

  private final DynamoDbTable<OutboxEntity> outboxTable;
  private final OutboxTableProperties outboxTableProperties;

  @Override
  public List<OutboxEntity> findEvents(OutboxQuery query) {
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

  public void markAsPublished(OutboxEntity item) {
    item.markAsPublished(Instant.now());

    Expression condition = Expression.builder()
      .expression("begins_with(gsi1sk, :pending)")
      .putExpressionValue(":pending", AttributeValue.fromS(OutboxStatusQueryKey.SORT_KEY_PENDING_PREFIX))
      .build();

    var updateItemEnhancedRequest = UpdateItemEnhancedRequest.builder(OutboxEntity.class)
      .item(item)
      .conditionExpression(condition)
      .build();

    try {
      outboxTable.updateItem(updateItemEnhancedRequest);
    }
    catch (ConditionalCheckFailedException e) {
      throw new OutboxEventAlreadyProcessedException(item.getEventId().toString());
    }
  }
}
