package com.jcondotta.banking.infrastructure.outbox.factory;

import com.jcondotta.banking.infrastructure.adapters.output.messaging.outbox.KafkaOutboxEventPublisher;
import com.jcondotta.banking.infrastructure.outbox.concurrency.ShardExecutor;
import com.jcondotta.banking.infrastructure.outbox.dispatcher.OutboxDispatcher;
import com.jcondotta.banking.infrastructure.outbox.dispatcher.VirtualThreadOutboxDispatcher;
import com.jcondotta.banking.infrastructure.outbox.processor.OutboxEventCompleter;
import com.jcondotta.banking.infrastructure.outbox.processor.OutboxEventShardProcessor;
import com.jcondotta.banking.infrastructure.outbox.properties.OutboxProperties;
import com.jcondotta.banking.infrastructure.outbox.record.OutboxRecord;
import com.jcondotta.banking.infrastructure.outbox.store.OutboxEventStore;
import org.springframework.kafka.core.KafkaTemplate;

/**
 * Factory that assembles the full Kafka-backed outbox worker pipeline from a single call.
 *
 * <p>Use this inside a consuming module's {@code @Bean} method to reduce four individual
 * bean declarations (publisher → completer → processor → dispatcher) down to one:
 *
 * <pre>{@code
 * @Bean
 * OutboxDispatcher outboxDispatcher(
 *     OutboxEventStore<MyEntity> eventStore,
 *     KafkaTemplate<String, byte[]> kafkaTemplate,
 *     ShardExecutor<Integer> shardExecutor,
 *     OutboxProperties outboxProperties) {
 *   return KafkaOutboxWorkerFactory.createDispatcher(eventStore, kafkaTemplate, shardExecutor, outboxProperties);
 * }
 * }</pre>
 *
 * <p>For custom pipelines that need to replace one of the intermediate components,
 * instantiate the components directly instead of using this factory.
 */
public final class KafkaOutboxWorkerFactory {

  private KafkaOutboxWorkerFactory() {}

  public static <T extends OutboxRecord> OutboxDispatcher createDispatcher(
    OutboxEventStore<T> eventStore,
    KafkaTemplate<String, byte[]> kafkaTemplate,
    ShardExecutor<Integer> shardExecutor,
    OutboxProperties outboxProperties
  ) {
    var publisher  = new KafkaOutboxEventPublisher<T>(kafkaTemplate, outboxProperties);
    var completer  = new OutboxEventCompleter<T>(publisher, eventStore);
    var processor  = new OutboxEventShardProcessor<T>(shardExecutor, eventStore, completer, outboxProperties);
    return new VirtualThreadOutboxDispatcher<T>(eventStore, processor, outboxProperties);
  }
}
