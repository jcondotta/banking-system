package com.jcondotta.banking.recipients.integration.testsupport.container;

import lombok.extern.slf4j.Slf4j;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.lifecycle.Startables;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

import static org.testcontainers.containers.localstack.LocalStackContainer.Service;

@Slf4j
public final class LocalStackContainerSupport {

    private static final LocalStackContainer LOCALSTACK =
        new LocalStackContainer(DockerImageName.parse("localstack/localstack:4.6.0"))
            .withServices(Service.DYNAMODB)
            .withCopyFileToContainer(
                MountableFile.forClasspathResource("localstack/init-aws.sh"),
                "/etc/localstack/init/ready.d/init-aws.sh")
            .withLogConsumer(outputFrame -> log.info(outputFrame.getUtf8StringWithoutLineEnding()))
            .withReuse(true);

    static {
        try {
            Startables.deepStart(LOCALSTACK).join();
        } catch (Exception e) {
            log.error("Failed to start LocalStack container: {}", e.getMessage());
            throw new RuntimeException("Failed to start LocalStack container", e);
        }
    }

    private LocalStackContainerSupport() {}

    public static String dynamoEndpoint() {
        return LOCALSTACK.getEndpointOverride(Service.DYNAMODB).toString();
    }

    public static String region() {
        return LOCALSTACK.getRegion();
    }

    public static String accessKey() {
        return LOCALSTACK.getAccessKey();
    }

    public static String secretKey() {
        return LOCALSTACK.getSecretKey();
    }
}