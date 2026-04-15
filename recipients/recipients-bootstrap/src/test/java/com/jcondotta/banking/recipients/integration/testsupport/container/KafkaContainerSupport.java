package com.jcondotta.banking.recipients.integration.testsupport.container;

import lombok.extern.slf4j.Slf4j;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.lifecycle.Startables;
import org.testcontainers.utility.DockerImageName;

@Slf4j
public final class KafkaContainerSupport {

    private static final KafkaContainer KAFKA =
        new KafkaContainer(DockerImageName.parse("apache/kafka:3.7.0"))
            .withReuse(true);

    static {
        try {
            Startables.deepStart(KAFKA).join();
            log.info("Kafka container started with bootstrap servers: {}", KAFKA.getBootstrapServers());
        }
        catch (Exception e) {
            log.error("Failed to start Kafka container: {}", e.getMessage());
            throw new RuntimeException("Failed to start Kafka container", e);
        }
    }

    private KafkaContainerSupport() {}

    public static String bootstrapServers() {
        return KAFKA.getBootstrapServers();
    }
}