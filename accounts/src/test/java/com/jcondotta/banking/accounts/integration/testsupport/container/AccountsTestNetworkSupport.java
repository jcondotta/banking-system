package com.jcondotta.banking.accounts.integration.testsupport.container;

import org.testcontainers.containers.Network;

public final class AccountsTestNetworkSupport {

  private static final Network NETWORK = Network.newNetwork();

  private AccountsTestNetworkSupport() {
  }

  public static Network network() {
    return NETWORK;
  }
}
