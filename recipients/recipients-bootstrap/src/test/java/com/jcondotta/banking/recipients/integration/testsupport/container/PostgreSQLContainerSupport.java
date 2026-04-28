package com.jcondotta.banking.recipients.integration.testsupport.container;

import lombok.extern.slf4j.Slf4j;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.lifecycle.Startables;
import org.testcontainers.utility.DockerImageName;

@Slf4j
public final class PostgreSQLContainerSupport {

    private static final String POSTGRES_IMAGE_NAME = "postgres:17-alpine";
    private static final DockerImageName POSTGRES_IMAGE = DockerImageName.parse(POSTGRES_IMAGE_NAME);

    @SuppressWarnings("resource")
    private static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>(POSTGRES_IMAGE)
            .withDatabaseName("recipients")
            .withUsername("admin")
            .withPassword("password")
            .withLogConsumer(outputFrame -> log.info(outputFrame.getUtf8StringWithoutLineEnding()));

    static {
        try {
            Startables.deepStart(POSTGRES).join();
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
