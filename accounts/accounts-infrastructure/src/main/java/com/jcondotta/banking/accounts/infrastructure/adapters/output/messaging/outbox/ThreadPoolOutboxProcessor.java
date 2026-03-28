package com.jcondotta.banking.accounts.infrastructure.adapters.output.messaging.outbox;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ThreadPoolOutboxProcessor implements OutboxProcessingStrategy {

//    private final ExecutorService executor;
//
//    public ThreadPoolOutboxProcessor(int poolSize) {
//        this.executor = Executors.newFixedThreadPool(poolSize);
//        log.info("ThreadPoolOutboxProcessor initialized [poolSize={}]", poolSize);
//    }
//
//    @Override
//    public void process(OutboxRepository repository, OutboxPublisher publisher) {
//        log.info("Processing outbox events with platform thread pool");
//        long start = System.nanoTime();
//
//        var shardTasks = IntStream.range(0, OutboxConstants.OUTBOX_TOTAL_SHARDS)
//            .<Callable<Void>>mapToObj(shard -> () -> {
//                processShard(shard, repository, publisher);
//                return null;
//            })
//            .toList();
//
//        try {
//            executor.invokeAll(shardTasks);
//        } catch (InterruptedException e) {
//            Thread.currentThread().interrupt();
//            log.error("Outbox shard processing interrupted", e);
//        }
//
//        log.info("Outbox processing completed [strategy=PLATFORM_THREADS, durationMs={}]",
//            TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start));
//    }
//
//    private void processShard(int shard, OutboxRepository repository, OutboxPublisher publisher) {
//        long start = System.nanoTime();
//        var pendingEvents = repository.findEvents(new OutboxQuery(shard, 10, OutboxStatus.PENDING));
//
//        pendingEvents.forEach(outbox -> processEvent(outbox, repository, publisher));
//
//        log.info("Shard processed [strategy=PLATFORM_THREADS, shard={}, events={}, durationMs={}]",
//            shard, pendingEvents.size(), TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - start));
//    }
//
//    private void processEvent(OutboxEntity outbox, OutboxRepository repository, OutboxPublisher publisher) {
//        try {
//            publisher.publish(outbox);
//            repository.markAsPublished(outbox);
//        } catch (Exception e) {
//            log.error("Failed to process outbox event [eventId={}]", outbox.getEventId(), e);
//        }
//    }
//
//    @PreDestroy
//    public void shutdown() {
//        executor.shutdown();
//    }

    @Override
    public void process() {

    }
}
