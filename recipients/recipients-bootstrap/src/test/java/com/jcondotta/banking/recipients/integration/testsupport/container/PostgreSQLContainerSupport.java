package com.jcondotta.banking.recipients.integration.testsupport.container;

import com.jcondotta.banking.recipients.integration.testsupport.container.toxiproxy.ProxiedService;
import com.jcondotta.banking.recipients.integration.testsupport.container.toxiproxy.ToxiproxyContainerSupport;
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

    private static final String JDBC_URL_TEMPLATE = "jdbc:postgresql://%s:%d/%s";

    @SuppressWarnings("resource")
    private static final PostgreSQLContainer POSTGRES = new PostgreSQLContainer(POSTGRES_IMAGE)
      .withDatabaseName(DATABASE_NAME)
      .withUsername(DATABASE_USERNAME)
      .withPassword(DATABASE_PASSWORD)
      .withNetwork(RecipientsTestNetworkSupport.network())
      .withNetworkAliases(POSTGRES_NETWORK_ALIAS)
      .withLogConsumer(outputFrame -> log.info(outputFrame.getUtf8StringWithoutLineEnding()));

    private static final ProxiedService POSTGRES_PROXY;
    private static final String JDBC_URL;

    static {
        try {
            Startables.deepStart(POSTGRES).join();

            POSTGRES_PROXY = ToxiproxyContainerSupport.proxy("postgres", POSTGRES_NETWORK_ALIAS, PostgreSQLContainer.POSTGRESQL_PORT);
            JDBC_URL = JDBC_URL_TEMPLATE.formatted(POSTGRES_PROXY.host(), POSTGRES_PROXY.port(), DATABASE_NAME);

            log.info("PostgreSQL JDBC URL through Toxiproxy: {}", JDBC_URL);
        }
        catch (Exception e) {
            log.error("Failed to start PostgreSQL container: {}", e.getMessage());
            throw new RuntimeException("Failed to start PostgreSQL container", e);
        }
    }

    private PostgreSQLContainerSupport() {}

    public static String jdbcUrl() {
        return JDBC_URL;
    }

    public static String username() {
        return POSTGRES.getUsername();
    }

    public static String password() {
        return POSTGRES.getPassword();
    }

    public static void cutConnection() {
        POSTGRES_PROXY.cutConnection();
    }

    public static void restoreConnection() {
        POSTGRES_PROXY.restoreConnection();
    }
}
