package com.jcondotta.banking.accounts.integration.testsupport.container;

import lombok.extern.slf4j.Slf4j;
import org.testcontainers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

@Slf4j
public final class LocalStackContainerSupport {

  private static final String LOCALSTACK_IMAGE_NAME = "localstack/localstack:4.6.0";
  private static final DockerImageName LOCALSTACK_IMAGE = DockerImageName.parse(LOCALSTACK_IMAGE_NAME);

  @SuppressWarnings("resource")
  private static final LocalStackContainer LOCALSTACK = new LocalStackContainer(LOCALSTACK_IMAGE)
    .withServices("dynamodb")
    .withNetwork(AccountsTestNetworkSupport.network())
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
      dynamoDb.createTable(CreateTableRequest.builder()
        .tableName("bank-accounts")
        .attributeDefinitions(
          AttributeDefinition.builder().attributeName("partitionKey").attributeType(ScalarAttributeType.S).build(),
          AttributeDefinition.builder().attributeName("sortKey").attributeType(ScalarAttributeType.S).build()
        )
        .keySchema(
          KeySchemaElement.builder().attributeName("partitionKey").keyType(KeyType.HASH).build(),
          KeySchemaElement.builder().attributeName("sortKey").keyType(KeyType.RANGE).build()
        )
        .billingMode(BillingMode.PAY_PER_REQUEST)
        .build());

      dynamoDb.waiter().waitUntilTableExists(DescribeTableRequest.builder()
        .tableName("bank-accounts")
        .build());
    }
    catch (ResourceInUseException ignored) {
      log.info("DynamoDB table already exists: bank-accounts");
    }
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
