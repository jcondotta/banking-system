package com.jcondotta.banking.accounts.infrastructure.adapters.input.rest;

import com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.entity.OutboxEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.UpdateItemEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OutboxPollerService {

    private final DynamoDbEnhancedClient enhancedClient;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String TABLE_NAME = "banking-entities";
    private static final String INDEX_NAME = "gsi-outbox-status";
    private static final String TOPIC = "bank-account-events";

    public void processPendingEvents(int limit) {

        DynamoDbTable<OutboxEntity> table =
                enhancedClient.table(TABLE_NAME, TableSchema.fromBean(OutboxEntity.class));

        DynamoDbIndex<OutboxEntity> index = table.index(INDEX_NAME);

        QueryConditional queryConditional =
                QueryConditional.sortBeginsWith(
                        Key.builder()
                                .partitionValue("OUTBOX")
                                .sortValue("PENDING#")
                                .build()
                );

        QueryEnhancedRequest request = QueryEnhancedRequest.builder()
                .queryConditional(queryConditional)
                .limit(limit)
                .build();

        List<OutboxEntity> items =
                index.query(request)
                        .stream()
                        .flatMap(page -> page.items().stream())
                        .toList();

        for (OutboxEntity item : items) {
            try {
                publishToKafka(item);
                markAsProcessed(table, item);
            } catch (Exception e) {
                System.err.println("Erro ao processar evento: " + e.getMessage());
            }
        }
    }

    private void publishToKafka(OutboxEntity item) {
        kafkaTemplate.send("bank-account-opened", item.getAggregateId().toString(), item.getPayload());
        System.out.println("Publicado no Kafka: " + item.getSortKey());
    }

    private void markAsProcessed(DynamoDbTable<OutboxEntity> table, OutboxEntity item) {

        String newTimestamp = Instant.now().toString();

        Expression condition = Expression.builder()
                .expression("begins_with(gsi1sk, :pending)")
                .putExpressionValue(":pending",
                        AttributeValue.builder().s("PENDING#").build())
                .build();

        item.setGsi1sk("PROCESSED#" + newTimestamp);

        UpdateItemEnhancedRequest<OutboxEntity> request =
                UpdateItemEnhancedRequest.builder(OutboxEntity.class)
                        .item(item)
                        .conditionExpression(condition)
                        .build();

        table.updateItem(request);
    }
}