package com.jcondotta.banking.accounts.domain.bankaccount.enums;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HolderTypeTest {

  @Test
  void shouldIdentifyPrimaryAccountHolderType_whenTypeIsPrimary() {
    assertThat(HolderType.PRIMARY.isPrimary()).isTrue();
    assertThat(HolderType.JOINT.isPrimary()).isFalse();
  }

  @Test
  void shouldIdentifyJointAccountHolderType_whenTypeIsJoint() {
    assertThat(HolderType.JOINT.isJoint()).isTrue();
    assertThat(HolderType.PRIMARY.isJoint()).isFalse();
  }
}
