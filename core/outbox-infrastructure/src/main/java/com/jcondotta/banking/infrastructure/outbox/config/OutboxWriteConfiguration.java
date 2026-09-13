package com.jcondotta.banking.infrastructure.outbox.config;

import com.jcondotta.banking.infrastructure.outbox.properties.OutboxProperties;
import com.jcondotta.banking.infrastructure.outbox.shard.OutboxShardResolver;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * Opt-in write-side configuration imported by bounded contexts that persist an outbox.
 * It deliberately has no component stereotype so services without an outbox do not bind
 * outbox properties merely because infrastructure-core is on the classpath.
 */
@EnableConfigurationProperties(OutboxProperties.class)
public class OutboxWriteConfiguration {

  @Bean
  OutboxShardResolver outboxShardResolver(OutboxProperties outboxProperties) {
    return new OutboxShardResolver(outboxProperties.shards());
  }
}
