package com.jcondotta.banking.accounts.integration.testsupport.container;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.lifecycle.Startables;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

import java.util.Map;

import static org.testcontainers.containers.localstack.LocalStackContainer.Service;

@Slf4j
public class LocalStackTestContainer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

  static final String LOCAL_STACK_IMAGE_NAME = "localstack/localstack:4.6.0";
  static final DockerImageName LOCALSTACK_IMAGE = DockerImageName.parse(LOCAL_STACK_IMAGE_NAME);

  static final LocalStackContainer LOCALSTACK_CONTAINER =
      new LocalStackContainer(LOCALSTACK_IMAGE)
          .withServices(Service.DYNAMODB)
          .withCopyFileToContainer(
              MountableFile.forHostPath("../docker/localstack/"),
              "/etc/localstack/init/ready.d/")
          .withLogConsumer(outputFrame -> log.info(outputFrame.getUtf8StringWithoutLineEnding()))
          .withReuse(true);

  private static void startContainer() {
    try {
      Startables.deepStart(LOCALSTACK_CONTAINER).join();
    } catch (Exception e) {
      log.error("Failed to start LocalStack container: {}", e.getMessage());
      throw new RuntimeException("Failed to start LocalStack container", e);
    }
  }

  protected static Map<String, String> getContainerProperties() {
    return Map.of(
        "AWS_ACCESS_KEY_ID", LOCALSTACK_CONTAINER.getAccessKey(),
        "AWS_SECRET_ACCESS_KEY", LOCALSTACK_CONTAINER.getSecretKey(),
        "AWS_DEFAULT_REGION", LOCALSTACK_CONTAINER.getRegion(),
        "AWS_DYNAMODB_ENDPOINT", LOCALSTACK_CONTAINER.getEndpointOverride(Service.DYNAMODB).toString()
    );
  }

  @Override
  public void initialize(@NotNull ConfigurableApplicationContext applicationContext) {
    startContainer();
    TestPropertyValues.of(getContainerProperties()).applyTo(applicationContext.getEnvironment());
  }
}
