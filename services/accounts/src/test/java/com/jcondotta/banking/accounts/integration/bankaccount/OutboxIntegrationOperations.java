package com.jcondotta.banking.accounts.integration.bankaccount;

import com.jcondotta.banking.accounts.domain.bankaccount.identity.BankAccountId;
import com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.dynamodb.outbox.entity.OutboxEntity;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

final class OutboxIntegrationOperations {

  private final DynamoDbTable<OutboxEntity> outboxTable;

  OutboxIntegrationOperations(DynamoDbTable<OutboxEntity> outboxTable) {
    this.outboxTable = outboxTable;
  }

  List<OutboxEntity> findOutboxEvents(BankAccountId id) {
    var query = QueryConditional.keyEqualTo(Key.builder()
      .partitionValue("BANK_ACCOUNT#%s".formatted(id.asString()))
      .build());

    return outboxTable.query(query)
      .items()
      .stream()
      .filter(item -> item.getSortKey().startsWith("OUTBOX#"))
      .toList();
  }

  void clearOutboxEvents() {
    outboxTable.scan().items().stream()
      .filter(item -> item.getSortKey().startsWith("OUTBOX#"))
      .forEach(outboxTable::deleteItem);
  }

  void assertOutboxEvents(BankAccountId id, String... eventTypes) {
    var events = findOutboxEvents(id);

    assertThat(events)
      .extracting(OutboxEntity::getEventType)
      .containsExactlyInAnyOrder(eventTypes);

    assertThat(events)
      .allSatisfy(event -> {
        assertThat(event.getAggregateId()).isEqualTo(id.asString());
        assertThat(event.getEventId()).isNotNull();
        assertThat(event.getPayload()).contains(id.asString());
        assertThat(event.getCreatedAt()).isNotNull();
        assertThat(event.getNextAttemptAt()).isNotNull();
      });
  }
}
