package com.jcondotta.banking.recipients.integration.testsupport.container.toxiproxy;

import eu.rekawek.toxiproxy.Proxy;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public record ProxiedService(Proxy proxy, String host, int port) {

  public void cutConnection() {
    try {
      restoreConnection();

      for (BandwidthCutDirection toxic : BandwidthCutDirection.values()) {
        proxy.toxics().bandwidth(toxic.name(), toxic.direction(), 0);
      }
    }
    catch (IOException e) {
      throw new RuntimeException("Failed to cut Toxiproxy connection", e);
    }
  }

  public void restoreConnection() {
    for (BandwidthCutDirection toxic : BandwidthCutDirection.values()) {
      removeToxicIfPresent(toxic.name());
    }
  }

  public void removeToxicIfPresent(String toxicName) {
    try {
      proxy.toxics().get(toxicName).remove();
    }
    catch (IOException e) {
      log.debug("Toxiproxy toxic '{}' already absent", toxicName);
    }
  }
}
