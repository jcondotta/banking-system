package com.jcondotta.banking.accounts.infrastructure.adapters.output.messaging.outbox;

import com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.entity.OutboxEntity;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.UpdateItemEnhancedRequest;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.net.URI;
import java.time.Instant;
import java.util.List;

public class SimpleOutboxPoller {

    private final DynamoDbEnhancedClient enhancedClient;
    private final DynamoDbTable<OutboxEntity> table;

    public SimpleOutboxPoller() {

        DynamoDbClient dynamoDbClient = DynamoDbClient.builder()
                .endpointOverride(URI.create("http://localhost:4566")) // LocalStack
                .region(Region.US_EAST_1)
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create("test", "test")
                        )
                )
                .build();

        this.enhancedClient = DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDbClient)
                .build();

        this.table = enhancedClient.table(
                "banking-entities",
                TableSchema.fromBean(OutboxEntity.class)
        );
    }

    public void processPendingEvents(int limit) {

        DynamoDbIndex<OutboxEntity> index =
                table.index("gsi-outbox-status");

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
                publish(item);
                markAsProcessed(item);
            } catch (Exception e) {
                System.out.println("Erro ao processar evento: " + e.getMessage());
            }
        }
    }

    private void publish(OutboxEntity item) {
        // Simulação de publish
        System.out.println("Publishing event: " + item.getSortKey());
        System.out.println(item.getPayload());
    }

    private void markAsProcessed(OutboxEntity item) {

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

        System.out.println("Evento marcado como PROCESSED: " + item.getSortKey());
    }

    public static void main(String[] args) {
        SimpleOutboxPoller poller = new SimpleOutboxPoller();
        poller.processPendingEvents(10);
    }
}