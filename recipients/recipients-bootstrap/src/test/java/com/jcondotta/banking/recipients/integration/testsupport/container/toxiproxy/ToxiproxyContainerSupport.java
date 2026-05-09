package com.jcondotta.banking.recipients.integration.testsupport.container.toxiproxy;

import com.jcondotta.banking.recipients.integration.testsupport.container.RecipientsTestNetworkSupport;
import eu.rekawek.toxiproxy.ToxiproxyClient;
import lombok.extern.slf4j.Slf4j;
import org.testcontainers.lifecycle.Startables;
import org.testcontainers.toxiproxy.ToxiproxyContainer;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public final class ToxiproxyContainerSupport {

  private static final String TOXIPROXY_IMAGE_NAME = "ghcr.io/shopify/toxiproxy:2.12.0";
  private static final DockerImageName TOXIPROXY_IMAGE = DockerImageName.parse(TOXIPROXY_IMAGE_NAME);

  @SuppressWarnings("resource")
  private static final ToxiproxyContainer TOXIPROXY = new ToxiproxyContainer(TOXIPROXY_IMAGE)
    .withNetwork(RecipientsTestNetworkSupport.network());

  private static final ToxiproxyClient TOXIPROXY_CLIENT = createClient();

  private static final AtomicInteger NEXT_PORT = new AtomicInteger(8666);
  private static final Map<String, ProxiedService> PROXIES = new ConcurrentHashMap<>();

  private ToxiproxyContainerSupport() {}

  private static ToxiproxyClient createClient() {
    try {
      Startables.deepStart(TOXIPROXY).join();
      return new ToxiproxyClient(TOXIPROXY.getHost(), TOXIPROXY.getControlPort());
    }
    catch (Exception e) {
      log.error("Failed to start Toxiproxy container: {}", e.getMessage());
      throw new RuntimeException("Failed to start Toxiproxy container", e);
    }
  }

  public static ProxiedService proxy(String name, String upstreamHost, int upstreamPort) {
    return PROXIES.computeIfAbsent(name, proxyName -> createProxy(proxyName, upstreamHost, upstreamPort));
  }

  private static ProxiedService createProxy(String name, String upstreamHost, int upstreamPort) {
    var proxyPort = NEXT_PORT.getAndIncrement();
    var listen    = "0.0.0.0:%d".formatted(proxyPort);
    var upstream  = "%s:%d".formatted(upstreamHost, upstreamPort);

    try {
      var proxy      = TOXIPROXY_CLIENT.createProxy(name, listen, upstream);
      var mappedPort = TOXIPROXY.getMappedPort(proxyPort);

      log.info("Toxiproxy proxy '{}' -> {}:{} mapped from container port {} and forwarding to {}",
        name, TOXIPROXY.getHost(), mappedPort, proxyPort, upstream);

      return new ProxiedService(proxy, TOXIPROXY.getHost(), mappedPort);
    }
    catch (IOException e) {
      throw new RuntimeException("Failed to create Toxiproxy proxy '%s'".formatted(name), e);
    }
  }
}
