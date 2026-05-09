package com.jcondotta.banking.recipients.integration.testsupport.container.toxiproxy;

import eu.rekawek.toxiproxy.model.ToxicDirection;

enum BandwidthCutDirection {

  DOWNSTREAM(ToxicDirection.DOWNSTREAM),
  UPSTREAM(ToxicDirection.UPSTREAM);

  private final ToxicDirection direction;

  BandwidthCutDirection(ToxicDirection direction) {
    this.direction = direction;
  }

  ToxicDirection direction() {
    return direction;
  }
}
