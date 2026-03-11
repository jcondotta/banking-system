package com.jcondotta.banking.accounts.infrastructure.adapters.output.persistence.entity;

public record OutboxKey(
  String partitionKey,
  String sortKey,
  String gsi1pk,
  String gsi1sk
) {}