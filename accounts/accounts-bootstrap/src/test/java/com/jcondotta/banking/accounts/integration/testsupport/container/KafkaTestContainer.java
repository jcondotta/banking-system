package com.jcondotta.banking.accounts.integration.testsupport.container;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.lifecycle.Startables;
import org.testcontainers.utility.DockerImageName;

import java.util.Map;

@Slf4j
public class KafkaTestContainer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

  private static final String KAFKA_IMAGE_NAME = "apache/kafka:3.7.0";
  private static final DockerImageName KAFKA_IMAGE = DockerImageName.parse(KAFKA_IMAGE_NAME);

  @SuppressWarnings("all")
  private static final KafkaContainer KAFKA_CONTAINER = new KafkaContainer(KAFKA_IMAGE)
    .withReuse(true);

  private static void startContainer() {
    try {
      Startables.deepStart(KAFKA_CONTAINER).join();
      log.info("Kafka container started with bootstrap servers: {}", KAFKA_CONTAINER.getBootstrapServers());
    }
    catch (Exception e) {
      log.error("Failed to start Kafka container: {}", e.getMessage());
      throw new RuntimeException("Failed to start Kafka container", e);
    }
  }

  private static Map<String, String> getContainerProperties() {
    return Map.of(
      "KAFKA_BOOTSTRAP_SERVERS", KAFKA_CONTAINER.getBootstrapServers(),
      "KAFKA_BANK_ACCOUNT_OPENED_TOPIC_NAME", "bank-account-opened",
      "KAFKA_BANK_ACCOUNT_STATUS_CHANGED_TOPIC_NAME", "bank-account-status-changed",
      "KAFKA_JOINT_ACCOUNT_HOLDER_ADDED_TOPIC_NAME", "joint-account-holder-added"
      );
  }

  @Override
  public void initialize(@NotNull ConfigurableApplicationContext applicationContext) {
    startContainer();
    TestPropertyValues.of(getContainerProperties()).applyTo(applicationContext.getEnvironment());
  }
}