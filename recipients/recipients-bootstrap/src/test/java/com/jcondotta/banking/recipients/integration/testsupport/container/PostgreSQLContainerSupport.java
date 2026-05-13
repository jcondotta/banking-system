package com.jcondotta.banking.recipients.integration.testsupport.container;

import lombok.extern.slf4j.Slf4j;
import org.testcontainers.lifecycle.Startables;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@Slf4j
public final class PostgreSQLContainerSupport {

    private static final String POSTGRES_IMAGE_NAME = "postgres:17-alpine";
    private static final DockerImageName POSTGRES_IMAGE = DockerImageName.parse(POSTGRES_IMAGE_NAME);

    private static final String POSTGRES_NETWORK_ALIAS = "postgres";

    private static final String DATABASE_NAME = "recipients";
    private static final String DATABASE_USERNAME = "admin";
    private static final String DATABASE_PASSWORD = "password";

    @SuppressWarnings("resource")
    private static final PostgreSQLContainer POSTGRES = new PostgreSQLContainer(POSTGRES_IMAGE)
      .withDatabaseName(DATABASE_NAME)
      .withUsername(DATABASE_USERNAME)
      .withPassword(DATABASE_PASSWORD)
      .withNetwork(RecipientsTestNetworkSupport.network())
      .withNetworkAliases(POSTGRES_NETWORK_ALIAS)
      .withLogConsumer(outputFrame -> log.info(outputFrame.getUtf8StringWithoutLineEnding()));

    static {
        try {
            Startables.deepStart(POSTGRES).join();
            log.info("PostgreSQL JDBC URL: {}", POSTGRES.getJdbcUrl());
        }
        catch (Exception e) {
            log.error("Failed to start PostgreSQL container: {}", e.getMessage());
            throw new RuntimeException("Failed to start PostgreSQL container", e);
        }
    }

    private PostgreSQLContainerSupport() {}

    public static String jdbcUrl() {
        return POSTGRES.getJdbcUrl();
    }

    public static String username() {
        return POSTGRES.getUsername();
    }

    public static String password() {
        return POSTGRES.getPassword();
    }
}
