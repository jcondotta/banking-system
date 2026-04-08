package com.jcondotta.banking.accounts.infrastructure.adapters.output.outbox.entity;

import com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.enums.EntityType;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OutboxEntityTest {

//  private static final Instant NOW = Instant.parse("2026-04-02T12:00:00Z");
//
//  // ── Builder defaults ─────────────────────────────────────────────────────
//
//  @Test
//  void shouldDefaultStatusToPending_whenBuiltWithoutExplicitStatus() {
//    var entity = OutboxEntity.builder().build();
//
//    assertThat(entity.getOutboxStatus()).isEqualTo(OutboxStatus.PENDING);
//  }
//
//  @Test
//  void shouldDefaultEntityTypeToOutboxEvent_whenBuiltWithoutExplicitEntityType() {
//    var entity = OutboxEntity.builder().build();
//
//    assertThat(entity.getEntityType()).isEqualTo(EntityType.OUTBOX_EVENT);
//  }
//
//  // ── markAsProcessing ─────────────────────────────────────────────────────
//
//  @Test
//  void shouldTransitionToProcessing_whenEntityIsPending() {
//    var entity = pendingEntity();
//
//    entity.transitionToProcessing(NOW);
//
//    assertThat(entity.getOutboxStatus()).isEqualTo(OutboxStatus.PROCESSING);
//  }
//
//  @Test
//  void shouldUpdateGsi1sk_whenMarkingAsProcessing() {
//    var entity = pendingEntity();
//    var expectedGsi1sk = OutboxStatusKey.buildSortKey(OutboxStatus.PROCESSING, NOW);
//
//    entity.transitionToProcessing(NOW);
//
//    assertThat(entity.getGsi1sk()).isEqualTo(expectedGsi1sk);
//  }
//
//  @Test
//  void shouldThrowNullPointerException_whenTransitionToProcessingReceivesNullInstant() {
//    var entity = pendingEntity();
//
//    assertThatThrownBy(() -> entity.transitionToProcessing(null))
//      .isInstanceOf(NullPointerException.class)
//      .hasMessage("processingAt must not be null");
//  }
//
//  @Test
//  void shouldThrowIllegalStateException_whenTransitionToProcessingCalledFromPublishedStatus() {
//    var entity = processingEntity();
//    entity.transitionToPublished(NOW);
//
//    assertThatThrownBy(() -> entity.transitionToProcessing(NOW.plusSeconds(1)))
//      .isInstanceOf(IllegalStateException.class)
//      .hasMessageContaining("PUBLISHED")
//      .hasMessageContaining("PROCESSING");
//  }
//
//  @Test
//  void shouldThrowIllegalStateException_whenTransitionToProcessingCalledFromFailedStatus() {
//    var entity = failedEntity();
//
//    assertThatThrownBy(() -> entity.transitionToProcessing(NOW))
//      .isInstanceOf(IllegalStateException.class)
//      .hasMessageContaining("FAILED")
//      .hasMessageContaining("PROCESSING");
//  }
//
//  // ── markAsPublished ──────────────────────────────────────────────────────
//
//  @Test
//  void shouldTransitionToPublished_whenEntityIsProcessing() {
//    var entity = processingEntity();
//
//    entity.transitionToPublished(NOW);
//
//    assertThat(entity.getOutboxStatus()).isEqualTo(OutboxStatus.PUBLISHED);
//  }
//
//  @Test
//  void shouldSetPublishedAt_whenMarkingAsPublished() {
//    var entity = processingEntity();
//
//    entity.transitionToPublished(NOW);
//
//    assertThat(entity.getPublishedAt()).isEqualTo(NOW);
//  }
//
//  @Test
//  void shouldUpdateGsi1sk_whenMarkingAsPublished() {
//    var entity = processingEntity();
//    var expectedGsi1sk = OutboxStatusKey.buildSortKey(OutboxStatus.PUBLISHED, NOW);
//
//    entity.transitionToPublished(NOW);
//
//    assertThat(entity.getGsi1sk()).isEqualTo(expectedGsi1sk);
//  }
//
//  @Test
//  void shouldSetTimeToLiveToOneDayAfterPublishedAt_whenMarkingAsPublished() {
//    var entity = processingEntity();
//    long expectedTtl = NOW.plus(1, ChronoUnit.DAYS).getEpochSecond();
//
//    entity.transitionToPublished(NOW);
//
//    assertThat(entity.getTimeToLive()).isEqualTo(expectedTtl);
//  }
//
//  @Test
//  void shouldBeIdempotent_whenTransitionToPublishedCalledOnAlreadyPublishedEntity() {
//    var entity = processingEntity();
//    entity.transitionToPublished(NOW);
//
//    // Second call with a later instant should be a no-op
//    Instant later = NOW.plusSeconds(60);
//    entity.transitionToPublished(later);
//
//    assertThat(entity.getPublishedAt()).isEqualTo(NOW);
//    assertThat(entity.getOutboxStatus()).isEqualTo(OutboxStatus.PUBLISHED);
//  }
//
//  @Test
//  void shouldThrowNullPointerException_whenTransitionToPublishedReceivesNullInstant() {
//    var entity = processingEntity();
//
//    assertThatThrownBy(() -> entity.transitionToPublished(null))
//      .isInstanceOf(NullPointerException.class)
//      .hasMessage("publishedAt must not be null");
//  }
//
//  @Test
//  void shouldThrowNullPointerException_whenTransitionToPublishedReceivesNullInstantEvenIfAlreadyPublished() {
//    // null check happens before the idempotency check — NPE is always thrown for null input
//    var entity = processingEntity();
//    entity.transitionToPublished(NOW);
//
//    assertThatThrownBy(() -> entity.transitionToPublished(null))
//      .isInstanceOf(NullPointerException.class)
//      .hasMessage("publishedAt must not be null");
//  }
//
//  @Test
//  void shouldThrowIllegalStateException_whenTransitionToPublishedCalledFromPendingStatus() {
//    var entity = pendingEntity();
//
//    assertThatThrownBy(() -> entity.transitionToPublished(NOW))
//      .isInstanceOf(IllegalStateException.class)
//      .hasMessageContaining("PENDING")
//      .hasMessageContaining("PUBLISHED");
//  }
//
//  // ── setStatus (indirectly via full transition path) ──────────────────────
//
//  @Test
//  void shouldFollowFullLifecycle_whenTransitioningPendingToProcessingToPublished() {
//    var entity = pendingEntity();
//
//    entity.transitionToProcessing(NOW);
//    assertThat(entity.getOutboxStatus()).isEqualTo(OutboxStatus.PROCESSING);
//
//    entity.transitionToPublished(NOW.plusSeconds(5));
//    assertThat(entity.getOutboxStatus()).isEqualTo(OutboxStatus.PUBLISHED);
//  }
//
//  @Test
//  void shouldThrowIllegalStateException_whenSetStatusIsCalledWithInvalidTransition() {
//    // FAILED is a terminal state — no further transitions allowed
//    var entity = failedEntity();
//
//    assertThatThrownBy(() -> entity.transitionToProcessing(NOW))
//      .isInstanceOf(IllegalStateException.class);
//  }
//
//  // ── helpers ──────────────────────────────────────────────────────────────
//
//  private static OutboxEntity pendingEntity() {
//    return OutboxEntity.builder()
//      .gsi1sk(OutboxStatusKey.buildSortKey(OutboxStatus.PENDING, NOW.minusSeconds(30)))
//      .build();
//  }
//
//  private static OutboxEntity processingEntity() {
//    var entity = pendingEntity();
//    entity.transitionToProcessing(NOW.minusSeconds(10));
//    return entity;
//  }
//
//  /**
//   * Builds a FAILED entity by directly setting status via builder.
//   * Note: there is currently no markAsFailed() method on OutboxEntity;
//   * FAILED is a valid target from PROCESSING per OutboxStatus, but OutboxEntity
//   * does not expose a transition method for it. This entity is constructed
//   * via the builder for testing terminal-state guard logic only.
//   *
//   * TODO: consider adding a markAsFailed(Instant) method to support the
//   * PROCESSING → FAILED transition that OutboxStatus already models.
//   */
//  private static OutboxEntity failedEntity() {
//    return OutboxEntity.builder()
//      .outboxStatus(OutboxStatus.FAILED)
//      .gsi1sk(OutboxStatusKey.buildSortKey(OutboxStatus.FAILED, NOW.minusSeconds(5)))
//      .build();
//  }
}
