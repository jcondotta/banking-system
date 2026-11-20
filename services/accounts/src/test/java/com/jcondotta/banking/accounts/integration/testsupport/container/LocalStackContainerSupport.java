package com.jcondotta.banking.accounts.integration.testsupport.container;

import lombok.extern.slf4j.Slf4j;
import org.testcontainers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.time.Duration;

import static org.awaitility.Awaitility.await;

@Slf4j
public final class LocalStackContainerSupport {

  private static final String LOCALSTACK_IMAGE_NAME = "localstack/localstack:4.6.0";
  private static final String TABLE_NAME = "bank-accounts";
  private static final String IBAN_INDEX_NAME = "gsi-iban";
  private static final String OUTBOX_INDEX_NAME = "gsi-outbox-status";
  private static final DockerImageName LOCALSTACK_IMAGE = DockerImageName.parse(LOCALSTACK_IMAGE_NAME);

  @SuppressWarnings("resource")
  private static final LocalStackContainer LOCALSTACK = new LocalStackContainer(LOCALSTACK_IMAGE)
    .withServices("dynamodb")
    .withLogConsumer(outputFrame -> log.info(outputFrame.getUtf8StringWithoutLineEnding()))
    .withReuse(true);

  private LocalStackContainerSupport() {
  }

  public static String accessKey() {
    AccountsContainerSupport.start();
    return LOCALSTACK.getAccessKey();
  }

  public static String secretKey() {
    AccountsContainerSupport.start();
    return LOCALSTACK.getSecretKey();
  }

  public static String region() {
    AccountsContainerSupport.start();
    return LOCALSTACK.getRegion();
  }

  public static String localStackEndpoint() {
    AccountsContainerSupport.start();
    return LOCALSTACK.getEndpoint().toString();
  }

  static LocalStackContainer container() {
    return LOCALSTACK;
  }

  static void initializeDynamoDb() {
    createBankAccountsTableIfNeeded();
    log.info("LocalStack container started with endpoint: {}", LOCALSTACK.getEndpoint());
  }

  private static void createBankAccountsTableIfNeeded() {
    try (var dynamoDb = dynamoDbClient()) {
      createTableIfMissing(dynamoDb);
      ensureOutboxIndexExists(dynamoDb);
    }
  }

  private static void createTableIfMissing(DynamoDbClient dynamoDb) {
    try {
      dynamoDb.createTable(CreateTableRequest.builder()
        .tableName(TABLE_NAME)
        .attributeDefinitions(
          stringAttribute("partitionKey"),
          stringAttribute("sortKey"),
          stringAttribute("iban"),
          stringAttribute("gsi1pk"),
          stringAttribute("gsi1sk")
        )
        .keySchema(
          key("partitionKey", KeyType.HASH),
          key("sortKey", KeyType.RANGE)
        )
        .globalSecondaryIndexes(
          GlobalSecondaryIndex.builder()
            .indexName(IBAN_INDEX_NAME)
            .keySchema(key("iban", KeyType.HASH))
            .projection(allAttributesProjection())
            .build(),
          GlobalSecondaryIndex.builder()
            .indexName(OUTBOX_INDEX_NAME)
            .keySchema(
              key("gsi1pk", KeyType.HASH),
              key("gsi1sk", KeyType.RANGE)
            )
            .projection(allAttributesProjection())
            .build()
        )
        .billingMode(BillingMode.PAY_PER_REQUEST)
        .build());

      dynamoDb.waiter().waitUntilTableExists(describeTableRequest());
    }
    catch (ResourceInUseException ignored) {
      log.info("DynamoDB table already exists: {}", TABLE_NAME);
    }
  }

  private static void ensureOutboxIndexExists(DynamoDbClient dynamoDb) {
    if (!hasIndex(dynamoDb, OUTBOX_INDEX_NAME)) {
      dynamoDb.updateTable(UpdateTableRequest.builder()
        .tableName(TABLE_NAME)
        .attributeDefinitions(
          stringAttribute("gsi1pk"),
          stringAttribute("gsi1sk")
        )
        .globalSecondaryIndexUpdates(GlobalSecondaryIndexUpdate.builder()
          .create(CreateGlobalSecondaryIndexAction.builder()
            .indexName(OUTBOX_INDEX_NAME)
            .keySchema(
              key("gsi1pk", KeyType.HASH),
              key("gsi1sk", KeyType.RANGE)
            )
            .projection(allAttributesProjection())
            .build())
          .build())
        .build());
    }

    await()
      .atMost(Duration.ofSeconds(30))
      .pollInterval(Duration.ofMillis(100))
      .until(() -> isIndexActive(dynamoDb, OUTBOX_INDEX_NAME));
  }

  private static boolean hasIndex(DynamoDbClient dynamoDb, String indexName) {
    return tableDescription(dynamoDb).globalSecondaryIndexes().stream()
      .anyMatch(index -> index.indexName().equals(indexName));
  }

  private static boolean isIndexActive(DynamoDbClient dynamoDb, String indexName) {
    return tableDescription(dynamoDb).globalSecondaryIndexes().stream()
      .anyMatch(index -> index.indexName().equals(indexName) && index.indexStatus() == IndexStatus.ACTIVE);
  }

  private static TableDescription tableDescription(DynamoDbClient dynamoDb) {
    return dynamoDb.describeTable(describeTableRequest()).table();
  }

  private static DescribeTableRequest describeTableRequest() {
    return DescribeTableRequest.builder().tableName(TABLE_NAME).build();
  }

  private static AttributeDefinition stringAttribute(String name) {
    return AttributeDefinition.builder()
      .attributeName(name)
      .attributeType(ScalarAttributeType.S)
      .build();
  }

  private static KeySchemaElement key(String name, KeyType keyType) {
    return KeySchemaElement.builder()
      .attributeName(name)
      .keyType(keyType)
      .build();
  }

  private static Projection allAttributesProjection() {
    return Projection.builder()
      .projectionType(ProjectionType.ALL)
      .build();
  }

  private static DynamoDbClient dynamoDbClient() {
    return DynamoDbClient.builder()
      .endpointOverride(LOCALSTACK.getEndpoint())
      .region(Region.of(LOCALSTACK.getRegion()))
      .credentialsProvider(StaticCredentialsProvider.create(
        AwsBasicCredentials.create(LOCALSTACK.getAccessKey(), LOCALSTACK.getSecretKey())
      ))
      .build();
  }
}
