package com.jcondotta.banking.accounts.outbox;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "app.outbox.worker.enabled=false")
class AccountsOutboxApplicationTests {

  @Test
  void contextLoads() {
  }

}
