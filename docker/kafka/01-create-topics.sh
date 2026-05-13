#!/bin/bash

set -e

echo "🚀 Running Kafka init script..."

BOOTSTRAP_SERVER="kafka-1:29092"

create_topic() {
  local topic_name=$1

  kafka-topics --bootstrap-server "$BOOTSTRAP_SERVER" \
    --create \
    --if-not-exists \
    --topic "$topic_name" \
    --partitions 3 \
    --replication-factor 1
}

create_topic "bank-account-opened"
create_topic "bank-account-status-changed"
create_topic "joint-account-holder-added"

echo "✅ Kafka topics created successfully"
