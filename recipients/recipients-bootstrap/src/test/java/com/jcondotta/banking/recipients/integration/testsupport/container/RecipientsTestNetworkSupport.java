package com.jcondotta.banking.recipients.integration.testsupport.container;

import org.testcontainers.containers.Network;

public final class RecipientsTestNetworkSupport {

  private static final Network NETWORK = Network.newNetwork();

  private RecipientsTestNetworkSupport() {
  }

  public static Network network() {
    return NETWORK;
  }
}
