package com.jcondotta.banking.recipients.integration.testsupport.container;

import lombok.extern.slf4j.Slf4j;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

@Slf4j
public final class KafkaContainerSupport {

  private static final String KAFKA_IMAGE_NAME = "apache/kafka:3.7.0";
  private static final DockerImageName KAFKA_IMAGE = DockerImageName.parse(KAFKA_IMAGE_NAME);

  @SuppressWarnings("resource")
  private static final KafkaContainer KAFKA = new KafkaContainer(KAFKA_IMAGE)
    .withReuse(true);

  private KafkaContainerSupport() {
  }

  public static String bootstrapServers() {
    RecipientsContainerSupport.start();
    return KAFKA.getBootstrapServers();
  }

  static KafkaContainer container() {
    return KAFKA;
  }

  static void logStarted() {
    log.info("Kafka container started with bootstrap servers: {}", KAFKA.getBootstrapServers());
  }
}
